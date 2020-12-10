package dfki.mm.functional;

import dfki.mm.DataHolder;
import dfki.mm.map.OSMMap;
import dfki.mm.relation2.MyNodeBucket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;

public class MapData  {

    private static final Logger log = LoggerFactory.getLogger(MapData.class);

    private Map<Integer, MyNodeBucket> map;

    public MapData() {
        map = MyNodeBucket.initContainer();
    }

//    public boolean loadMap(String path) {
//        log.info("loadMap: Loading map from {}", path);
////        isMapLoading.set(true);
////        Map<Integer, MyNodeBucket> map = MyNodeBucket.initContainer();
//        try {
//            MyNodeBucket.loadCSV(path, map);
//            this.map = map;
//            isMapLoaded.set(true);
////            isMapLoading.set(false);
//            log.info("loadMap: SUCCESS");
//            return true;
//        } catch (IOException e) {
//            log.error("loadMap: loading {} FAILED", path, e);
//            return false;
//        }
//    }

    public Map<Integer, MyNodeBucket> getMap() {
//        if (isMapLoaded.get()) {// && !isMapLoading.get()) {
        return map;
//        } else {
//            return null;
//        }
    }

    public boolean updateMap(String path) {
        log.info("updateMap: Loading map from {}", path);
//        if (isMapLoaded.get()) {// && !isMapLoading.get()) {
        try {
            MyNodeBucket.loadCSV(path, map);
            log.info("updateMap: SUCCESS");
            return true;
        } catch (IOException e) {
            log.error("updateMap: loading {} FAILED", path, e);
        }
//        }
        return false;

    }

    public EnumMap<MyNodeBucket.NodeType, MyNodeBucket.NodeWithDistance> findNearest(
            double latitude, double longitude, int maxRadius) {

        return MyNodeBucket.getNearestNodes(getMap(), latitude, longitude, maxRadius);

    }
}
