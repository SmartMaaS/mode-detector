package dfki.mm.wui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

/**
 * https://stackoverflow.com/questions/29746667/java-nio-file-filesystemnotfoundexception-when-getting-file-from-resources-folde/29747012
 * https://stackoverflow.com/questions/309424/how-do-i-read-convert-an-inputstream-into-a-string-in-java
 */
public class ResourceLoader {
//    public static final String FILE_INDEX = "/index.html";
//    public static final String FILE_PATH_STATIC = "static";

//    public static final String[] FILES_MAP = {
//            "osm-clicker-template.html",
//            "osm-clicker-template-mapbox.html",
//            "osm-clicker-template-osm.html",
//    };

//    Document doc;


    private static final Logger log = LoggerFactory.getLogger(ResourceLoader.class);

//    /**
//     * Adds file to a map
//     * @param path file path
//     * @param root parent directory path (removed from name)
//     */
//    private void loadStatic(Path path, Path root) {
//        // https://docs.oracle.com/javase/tutorial/essential/io/pathOps.html#convert
//        Path p = root.relativize(path);
//        String pathString = p.toString();
//        log.info("Loading {} -> {}", path, pathString);
//        try {
//            statics.put(pathString, Files.readString(path));
//        } catch (IOException e) {
//            log.warn("Unable to load resource: {}", path, e);
//            statics.put(pathString, "error47");
////            e.printStackTrace();
//        }
//    }

    public ResourceLoader() throws IOException, URISyntaxException {
//        URL resource = Thread.currentThread().getContextClassLoader().getResource(FILE_INDEX);
//        String string = Files.readString(Paths.get(FILE_INDEX));
//        getClass().getResourceAsStream()
//        String string =

//        String string = String.join("\n", read4(FILE_INDEX));

//        doc = Jsoup.parse(string);

//        for (String s : Arrays.asList(FILES_MAP)) {
//
//        }

//        resource = Thread.currentThread().getContextClassLoader().getResource(FILE_PATH_STATIC);
//        Path path = Paths.get(resource.toURI());
//        Files.walk(path)
//                .filter(Files::isRegularFile)
//                .forEach(e -> loadStatic(e, path));
    }


    public static String readString(String path) {
        return String.join("\n", read4(path));
    }

    public static List<String> read4(String path) {
        try {
            log.debug("read4 {}", path);

            InputStream inputStream = ResourceLoader.class.getResourceAsStream(path);
            List<String> result = new BufferedReader(new InputStreamReader(inputStream)).lines()
                    .parallel().collect(Collectors.toList());
            return result;
        } catch (RuntimeException e) {
            log.error("Cannot read resource: {}", path, e);
            throw e;
        }
    }

    public static byte[] readBytes(String path) {
        try {
            log.debug("readBytes {}", path);

            InputStream inputStream = ResourceLoader.class.getResourceAsStream(path);
            return inputStream.readAllBytes();
        } catch (RuntimeException | IOException e) {
            log.error("Cannot read resource: {}", path, e);
            throw e instanceof RuntimeException ? (RuntimeException)e : new RuntimeException(e);
        }
//        return null;
    }
}
