package dfki.mm.main;

import dfki.mm.Configuration;
import dfki.mm.DataHolder;
import dfki.mm.wui.JettyMain;
import dfki.mm.wui.android.JettyMainSubmit;
import org.eclipse.jetty.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Main class
 */
public class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    /**
     *
     * @param args  Can define path for a configuration file
     */
    public static void main(String[] args) throws Exception {
        log.info("Current path: {}", Paths.get("").toAbsolutePath());
        String[] searchConfigPaths = {
                "input/dfki-config.properties",
                "config.txt",
        };

        if (args.length > 0) {
            Configuration.INSTANCE.load(args[0]);
        } else {
            for (String configPath : searchConfigPaths) {
                if (Files.exists(Paths.get(configPath))) {
                    Configuration.INSTANCE.load(configPath);
                    break;
                }
            }
        }

        DataHolder.INSTANCE.init();
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(Main::loadMaps);

        // todo remove when not debugging
        // will not fail when another thread
        // executorService.submit(() -> DataHolder.INSTANCE.trackManager.restore(null));
         executorService.submit(() -> DataHolder.INSTANCE.trackManager.restore("./input/all.json.gz"));

        executorService.shutdown();

        Server androidJetty = JettyMainSubmit.main();
        Server wuiJetty = JettyMain.main();

        // androidJetty.join();
        wuiJetty.join();
    }

    private static void loadMaps() {
        log.info("Loading osm data");
        for (String mapPath : Configuration.INSTANCE.mapsToLoad) {
//            DataHolder.INSTANCE.mapData.updateMap(mapPath);
        }
//        DataHolder.INSTANCE.updateMap("input/berlin-csv-0604/berlin-%s-%s-0604.csv.gz");
//        DataHolder.INSTANCE.updateMap("input/saarland-csv-0604/saarland-%s-%s-0604.csv.gz");
    }
}
