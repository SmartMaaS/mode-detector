package dfki.mm.functional;

import dfki.mm.Configuration;
import dfki.mm.DataHolder;
import dfki.mm.InternalMessage;
import dfki.mm.tracks.GpsTrack;
import dfki.mm.tracks.MyJsonParser;
import dfki.mm.tracks.extension.Extension;
import dfki.mm.util.FileUtil;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

public class TrackManager {

    private String savesPath = "/dev/shm/1/saves";
    private String tracksPath = savesPath + "/tracks";

    private static final Logger log = LoggerFactory.getLogger(TrackManager.class);
    private static final DecimalFormat df = new DecimalFormat("#.##");
    public final List<GpsTrack> tracks = new ArrayList<>();
//    public final Set<Integer> hashes = new HashSet<>();

    public List<GpsTrack> addTracksMissingFields(List<GpsTrack> newTracks) {
        return addTracks(newTracks, null);
    }

    public List<GpsTrack> addTracks(List<GpsTrack> newTracks) {
        return addTracks(newTracks, ExtensionManager.INSTANCE.getAllExtensions());
    }

    private List<GpsTrack> addTracks(List<GpsTrack> newTracks, List<Extension> allExtensions) {
        long start = System.nanoTime();
        ExecutorService es = Executors.newCachedThreadPool();
//        List<Extension> allExtensions = ExtensionManager.INSTANCE.getAllExtensions();
        AtomicInteger counterSubmitted = new AtomicInteger();
        AtomicInteger counterRejected = new AtomicInteger();
        AtomicInteger counterStarted = new AtomicInteger();
        AtomicInteger counterCompleted = new AtomicInteger();
        ConcurrentLinkedQueue<GpsTrack> acceptedQueue = new ConcurrentLinkedQueue<>();
        ConcurrentLinkedQueue<GpsTrack> rejectedQueue = new ConcurrentLinkedQueue<>();
        List<GpsTrack> accepted = Collections.emptyList();
        List<GpsTrack> rejected;
        try {
            DataHolder.INSTANCE.rwl.readLock().lock();
            for (GpsTrack newTrack : newTracks) {
                counterSubmitted.incrementAndGet();
                if (newTrack.exception == null) {
                    es.submit(() -> {
                        counterStarted.incrementAndGet();
                        try {
                            List<Extension> needed = allExtensions == null || allExtensions.size() == 0 ?
                                    newTrack.fixMissingFields() : allExtensions;

                            for (Extension extension : needed) {
                                extension.compute(newTrack);
                            }
                        } catch (Exception e) {
                            if (newTrack.exception == null) {
                                newTrack.exception = e;
                            }
                            System.out.print('.');
                        } finally {
                            if (newTrack.exception == null) {
                                acceptedQueue.add(newTrack);
                                counterCompleted.incrementAndGet();
                            } else {
                                rejectedQueue.add(newTrack);
                                counterRejected.incrementAndGet();
                            }
                        }
                    });
                } else {
                    counterRejected.incrementAndGet();
                    rejectedQueue.add(newTrack);
                }
            }
//            log.info("addTracks before shutdown: {} {}-{}={}",
//                    counterSubmitted.get(), counterStarted.get(), counterRejected.get(), counterCompleted.get());
            es.shutdown();
            boolean completedAll = es.awaitTermination(600, TimeUnit.SECONDS);
            if (!completedAll) {
                log.warn("Not all tracks completed before the timeout, try increasing it.");
                log.info("addTracks after wait: {} {}-{}={}",
                        counterSubmitted.get(), counterStarted.get(), counterRejected.get(), counterCompleted.get());
            }
//            accepted.sort(Comparator.comparing(o -> o.name));
//            Collections.sort(accepted, Comparator.comparing(o -> o.name));
//            this.tracks.addAll(accepted);
//            log.info("addTracks after shutdown: {} {}-{}={}",
//                    counterSubmitted.get(), counterStarted.get(), counterRejected.get(), counterCompleted.get());
            accepted = acceptedQueue.stream().sorted(Comparator.comparing(o -> o.name)).collect(Collectors.toList());
            this.tracks.addAll(accepted);
        } catch (InterruptedException e) {
            e.printStackTrace();
//            accepted = Collections.emptyList();
        } finally {
            DataHolder.INSTANCE.rwl.readLock().unlock();
        }
        rejected = new ArrayList<>(rejectedQueue);
//        for (GpsTrack track : newTracks) {
//            if (hashes.add(track.hashCode())) {
//                this.tracks.add(track);
//            } else {
//                log.info("Track rejected: {}", track.name);
//            }
//        }
//        log.info("addTracks end shutdown: {} {}-{}={}",
//                counterSubmitted.get(), counterStarted.get(), counterRejected.get(), counterCompleted.get());
        log.info("addTracks: completed in {} seconds", df.format((System.nanoTime() - start) / 10000000L * 1e-2));
//        log.info("addTracks: {} accepted queue, {} rejected queue", acceptedQueue.size(), rejectedQueue.size());
        log.info("addTracks[{}]: {} submitted, {} rejected", accepted.size(), newTracks.size(), rejected.size());
        for (GpsTrack gpsTrack : rejected) {
            log.warn("'{}' rejected", gpsTrack.name, gpsTrack.exception);
        }
        Configuration.INSTANCE.onMessage(InternalMessage.TRACKS_RELOAD, Collections.emptyList());
        return rejected;
    }

//    public void preprocess(List<GpsTrack> newTracks) {
//
//    }

    public boolean renameTrack(GpsTrack track, String newName) {
//        if (hashes.contains(Objects.hashCode(newName))) {
//            return false;
//        }
//        hashes.remove(track.hashCode());
        track.name = newName;
//        hashes.add(track.hashCode());
        Configuration.INSTANCE.onMessage(InternalMessage.TRACKS_RELOAD, Collections.emptyList());
        return true;
    }

    public boolean removeTracks(List<GpsTrack> toRemove) {
        boolean ret = false;
        try {
            DataHolder.INSTANCE.rwl.readLock().lock();
            for (GpsTrack track : toRemove) {
                if (tracks.remove(track)) {
//                hashes.remove(track.hashCode());
                    ret = true;
                }
            }
        } finally {
            DataHolder.INSTANCE.rwl.readLock().unlock();
        }
        if (ret) {
            Configuration.INSTANCE.onMessage(InternalMessage.TRACKS_RELOAD, Collections.emptyList());
//            Configuration.INSTANCE.onMessage(InternalMessage.TRACK_REMOVED, Collections.emptyList());
        }
        return ret;
    }

    public GpsTrack getTrack(int id) {
//        if (hashes.contains(id)) {
        return tracks.parallelStream().filter(track -> track.id == id).findAny().orElse(null);
//        }
//        return null;
    }

//    public void loadTracks(String[] params) {
//        List<GpsTrack> tracks = new ArrayList<>();
//        for (String name : params) {
//            try {
//                tracks.add(loadTrack(name));
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        DataHolder.INSTANCE.trackManager.addTracks(tracks);
//
//    }
//
//    public void saveTracks(String[] params) {
//        List<GpsTrack> tracks = DataHolder.INSTANCE.trackManager.tracks;
//        for (String id : params) {
//            int i = Integer.parseInt(id);
//            try {
//                saveTrack(tracks.get(i));
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//
//    }

//    public void saveTrack(GpsTrack track) throws IOException {
//        Path path = Paths.get(tracksPath);
//        Files.createDirectories(path);
//        path = path.resolve(track.name + ".json");
//        Files.writeString(path, GpsTrack.serialize(track));
//    }
//
//    public GpsTrack loadTrack(String name) throws IOException {
//        Path path = Paths.get(tracksPath);
//        if (Files.exists(path)) {
//            path = path.resolve(name);
//            return GpsTrack.deserialize(Files.readString(path));
//        }
//        return null;
//    }

    public List<String> getSavedTracks() {
        Path path = Paths.get(tracksPath);
        if (Files.exists(path)) {
            try {
                return Files.list(path).map(p -> p.getFileName().toString()).collect(Collectors.toList());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return Collections.emptyList();
    }

    public void backup(List<GpsTrack> tracks) {
        log.info("backup: {}", tracksPath);
        try {
            Path path = Paths.get(tracksPath);
            Files.createDirectories(path);
            path = path.resolve("all" + ".json");
            JSONArray ret = new JSONArray();
            for (JSONObject jsonObject : MyJsonParser.tracksToJson(tracks, ExtensionManager.INSTANCE.getCoreExtensions())) {
                ret.put(jsonObject);
            }
            Files.writeString(path, ret.toString(2));
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }



    public void restore(String filePath) {
        try {
            Path path = filePath == null ? Paths.get(tracksPath, "all.json.gz") : Paths.get(filePath);
            log.info("restore [{}] : {}", filePath, path);
//            if (filePath != null) {
//                path = Paths.get(tracksPath);
//            } else {
//                path = Paths.get(tracksPath);
//                path = path.resolve("all" + ".json");
//            }
//            List<Field> fields = ExtensionManager.INSTANCE.getAllFields();

            if (Files.exists(path)) {
                JSONArray root = new JSONArray(path.getFileName().toString().endsWith(".gz")
                        ? FileUtil.readGZip(path)
                        : Files.readString(path));
                List<GpsTrack> ret = MyJsonParser.fromJson(root);
                for (GpsTrack gpsTrack : ret) {
                    gpsTrack.updateModes();
                }
//                DataHolder.INSTANCE.trackManager.tracks.addAll(ret);
                DataHolder.INSTANCE.trackManager.addTracksMissingFields(ret);
//                Configuration.INSTANCE.onMessage(InternalMessage.TRACKS_RELOAD, Collections.emptyList());
            }
        } catch (Exception e) {
            log.warn("cannot restore", e);
//            throw new RuntimeException(e);
        }
//        return null;
    }

}
