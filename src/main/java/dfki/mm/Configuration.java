package dfki.mm;

//import com.moandjiezana.toml.Toml;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public enum Configuration implements MessageListener {
    INSTANCE;

    private static final Logger log = LoggerFactory.getLogger(Configuration.class);

    private final Map<InternalMessage, CopyOnWriteArrayList<MessageListener>> listeners;
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    Configuration() {
        Map<InternalMessage, CopyOnWriteArrayList<MessageListener>> l = new HashMap<>();
        for (InternalMessage value : InternalMessage.values()) {
            l.put(value, new CopyOnWriteArrayList<>());
        }
        listeners = Collections.unmodifiableMap(l);
    }


    public boolean startJetty = false;
    public int port = 12340;
    public int androidSubmitPort = 8903;
    public List<String> mapsToLoad = new ArrayList<>(
        Arrays.asList(
                "input/berlin-csv-0531/berlin-%s-%s-0531.csv.gz",
                "input/saarland-csv-0604/saarland-%s-%s-0604.csv.gz"));

    public String URL = "http://0.0.0.0:12349/";
//    public final String URL = "http://asr-ws-fury.dfki.net:12349/";
    public String URL_LIST = URL + "models";
    public String URL_PREDICT = URL + "predict";
    public String URL_TRAIN = URL + "train";


    public void load(String filename) throws IOException {
        log.info("load: Loading config from {}", Paths.get(filename).toAbsolutePath());

//        Toml toml = new Toml().read();

        Properties p = new Properties();
        p.load(Files.newBufferedReader(Paths.get(filename)));

        port = Integer.parseInt(p.getProperty("port"));
        startJetty = Boolean.parseBoolean(p.getProperty("startJetty", "false"));
//        mapsToLoad.clear();
        mapsToLoad = Arrays.asList(p.getProperty("maps").strip().split(" "));
        setUrl(p.getProperty("python-url"));

        log.info("load: port = {}", port);
        log.info("load: startJetty = {}", startJetty);
        log.info("load: mapsToLoad = {}", mapsToLoad);
    }

    private void setUrl(String url) {
        URL = url;
        URL_LIST = URL + "models";
        URL_PREDICT = URL + "predict";
        URL_TRAIN = URL + "train";

        log.info("load: URL = {}", URL);
        log.info("load: URL_LIST = {}", URL_LIST);
        log.info("load: URL_PREDICT = {}", URL_PREDICT);
        log.info("load: URL_TRAIN = {}", URL_TRAIN);
    }

    @Override
    public void onMessage(InternalMessage message, Collection<String> data) {
        Collection<String> finalData = Collections.unmodifiableCollection(data);
        for (MessageListener messageListener : listeners.get(message)) {
//            executorService.submit(() -> notify(messageListener, message, finalData));
            notify(messageListener, message, finalData);
        }
    }

    private void notify(MessageListener messageListener, InternalMessage message, Collection<String> data) {
        try {
            messageListener.onMessage(message, data);
        } catch (Exception e) {
            log.info("Error passing a message [{}] to [{}]", message, messageListener, e);
        }
    }

    public void subscribe(InternalMessage message, MessageListener messageListener) {
        listeners.get(message).add(messageListener);
    }
}
