package dfki.mm.functional;

//import dfki.mm.tracks.Extension;
import dfki.mm.Configuration;
import dfki.mm.DataHolder;
import dfki.mm.InternalMessage;
import dfki.mm.tracks.Field;
import dfki.mm.tracks.GpsTrack;
import dfki.mm.tracks.extension.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public enum ExtensionManager {
    INSTANCE;

    private static final Logger log = LoggerFactory.getLogger(ExtensionManager.class);

    private final List<Extension> allExtensions = new ArrayList<>();
    private final List<Field<?>> allFields = new ArrayList<>();

    public Extension getExtension(int id) {
        return allExtensions.stream().filter(e -> e.id == id).findAny().orElse(null);
    }

    public final MainExtension mainExtension = new MainExtension();
    public final OsmExtension osmExtension = new OsmExtension(mainExtension);
//    public final RelExtension relExtension = new RelExtension(mainExtension);
    public final MyExtension myExtension = new MyExtension(mainExtension, osmExtension);
    public final OldExtension oldExtension = new OldExtension(mainExtension);

    private AtomicInteger position = new AtomicInteger();

    ExtensionManager() {
        addExtensionSimple(mainExtension);
        addExtensionSimple(osmExtension);
        addExtensionSimple(myExtension);
        addExtensionSimple(oldExtension);
//        all.add(relExtension);
    }

    private void addExtensionSimple(Extension extension) {
        allExtensions.add(extension);
        List<Field<?>> fields = extension.getFields();
        fields.forEach(e -> e.position = position.getAndIncrement());
        allFields.addAll(fields);
    }

    public void addExtension(Extension extension) {
        addExtensionSimple(extension);
        for (GpsTrack track : DataHolder.INSTANCE.trackManager.tracks) {
            track.addExtension(extension);
            extension.compute(track);
        }
        Configuration.INSTANCE.onMessage(InternalMessage.EXTENSIONS_RELOAD, Collections.emptyList());
    }

    public Extension extensionById(int id) {
        return allExtensions.stream().filter(e -> e.id == id).findAny().orElse(null);
    }

    public Field fieldById(int id) {
        return allFields.stream().filter(e -> e.id == id).findAny().orElse(null);
    }

    public List<Field<?>> getAllFields() {
        return Collections.unmodifiableList(allFields);
    }

    public List<Extension> getAllExtensions() {
        return Collections.unmodifiableList(allExtensions);
    }

    public List<Extension> getCoreExtensions() {
        return List.of(
                mainExtension,
                osmExtension,
                myExtension,
                oldExtension
        );
    }

    public Extension extensionByName(String name) {
        return allExtensions.stream().filter(e -> e.getName().equals(name)).findAny().orElse(null);
    }

    public Field fieldByName(Extension extension, String name) {
        return extension.getFields().stream().filter(e -> e.name.equals(name)).findAny().orElse(null);
    }

    public Field fieldByName(String fullName) {
        int i = fullName.indexOf('.');
        if (i < 1) {
            return null;
        }
        Extension extension = extensionByName(fullName.substring(0, i));
        if (extension == null) {
            return null;
        }
        return fieldByName(extension, fullName.substring(i + 1));
    }


    //    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
//    private final Lock r = readWriteLock.readLock();
//    private final Lock w = readWriteLock.writeLock();
//
//    public boolean add(Extension extension) {
//        w.lock();
//        try {
//            if (!all.contains(extension)) {
//                all.add(extension);
//                return true;
//            } else {
//                return false;
//            }
//        } finally {
//            w.unlock();
//        }
//    }
//
//    public boolean remove(Extension extension) {
//        w.lock();
//        try {
//            if (!all.contains(extension)) {
//                all.add(extension);
//                return true;
//            } else {
//                return false;
//            }
//        } finally {
//            w.unlock();
//        }
//    }
//
//    public List<Extension> getAll() {
//        r.lock();
//        try {
//            return new ArrayList<>(all);
//        } finally {
//            r.unlock();
//        }
//    }
//
//    public Extension get(int id) {
//        r.lock();
//        try {
//            for (Extension extension : all) {
//                if (extension.id == id) {
//                    return extension;
//                }
//            }
//            return null;
//        } finally {
//            r.unlock();
//        }
//    }
}
