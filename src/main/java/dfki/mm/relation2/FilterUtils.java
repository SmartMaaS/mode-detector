package dfki.mm.relation2;

import crosby.binary.osmosis.OsmosisReader;
import crosby.binary.osmosis.OsmosisSerializer;
import org.openstreetmap.osmosis.core.task.v0_6.Sink;
import org.openstreetmap.osmosis.osmbinary.file.BlockOutputStream;
import org.openstreetmap.osmosis.xml.common.CompressionMethod;
import org.openstreetmap.osmosis.xml.v0_6.XmlReader;
import org.openstreetmap.osmosis.xml.v0_6.XmlWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

public class FilterUtils {

    private static final Logger log = LoggerFactory.getLogger(FilterUtils.class);


    public static Runnable load(String filename, Sink sink) throws IOException {
        if (filename.endsWith(".osm.pbf")) {
            return loadPBF(Files.newInputStream(Paths.get(filename)), sink);
        } else if (filename.endsWith(".osm.gz")) {
            return loadOSM(Paths.get(filename).toFile(), CompressionMethod.GZip, sink);
        } else if (filename.endsWith(".osm.bz2")) {
            return loadOSM(Paths.get(filename).toFile(), CompressionMethod.BZip2, sink);
        } else if (filename.endsWith(".osm")) {
            return loadOSM(Paths.get(filename).toFile(), CompressionMethod.None, sink);
        }
        return null;
    }

    private static Runnable loadOSM(File inputFile, CompressionMethod compressionMethod, Sink sink) {
        XmlReader or = new XmlReader(inputFile, true, compressionMethod);
        or.setSink(sink);
        return or;
    }

    private static Runnable loadPBF(InputStream is, Sink sink) {
        OsmosisReader or = new OsmosisReader(is);
        or.setSink(sink);
        return or;
    }

    public static Sink getOutSink(String to) throws IOException {
        final Sink out;
        if (to.endsWith(".osm.pbf")) {
            BlockOutputStream bos = new BlockOutputStream(Files.newOutputStream(Paths.get(to)));
            out = new OsmosisSerializer(bos);
        } else {
            out = new XmlWriter(Files.newBufferedWriter(Paths.get(to)));
        }
        return out;
    }

    public static List<String> readCSV(String filename) throws IOException {
        boolean isZip = filename.endsWith(".gz");
        try (BufferedReader br = isZip
                ? new BufferedReader(new InputStreamReader(new GZIPInputStream(Files.newInputStream(Paths.get(filename)))))
                : Files.newBufferedReader(Paths.get(filename))) {
            String line = br.readLine();
            //header
            return br.lines()
                    .map(String::trim)
                    .filter(e -> e.length() > 0).collect(Collectors.toUnmodifiableList());
        } catch (Exception e) {
            log.error("Failed loading {}", filename, e);
            throw new RuntimeException(e);
        }
    }

}
