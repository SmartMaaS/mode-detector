package dfki.mm.relation2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * lon: -180 -- 180
 * lat: -70 -- 70
 *
 * TODO:
 * lines -> pairs
 * add pairs by each side to list
 * avoid double checks
 */
public class MyNodeBucket {

    private static final Logger log = LoggerFactory.getLogger(MyNodeBucket.class);


    public static void loadCSV(String filename, Map<Integer, MyNodeBucket> container) throws IOException {
        log.info("loadCSV Loading {}", filename);
        Map<Long, MyNode> nodeMap = new HashMap<>();

        MyNode.readCSV(String.format(filename, "bus", "stops")).forEach(e -> {
            MyNode x = nodeMap.get(e.id);
            if (x == null) {
                nodeMap.put(e.id, e);
                x = e;
            }
            x.setBusStop();
        });
        MyNode.readCSV(String.format(filename, "rail", "stops")).forEach(e -> {
            MyNode x = nodeMap.get(e.id);
            if (x == null) {
                nodeMap.put(e.id, e);
                x = e;
            }
            x.setRailStop();
        });
        MyNode.readCSV(String.format(filename, "way", "nodes")).forEach(e -> nodeMap.putIfAbsent(e.id, e));
//        MyNode.readCSV(String.format(filename, "rail", "stops")).forEach(e -> nodeMap.computeIfAbsent(e.id, x -> e).setRailStop());
//        MyNode.readCSV(String.format(filename, "way", "nodes")).forEach(e -> nodeMap.computeIfAbsent(e.id, x -> e));

        log.info("loadCSV loaded {} nodes", nodeMap.size());

        MyNodeConnection.readCSV(String.format(filename, "bus", "lines")).forEach(e -> {
            MyNode n1 = nodeMap.get(e.node1).setBusLine();
            MyNode n2 = nodeMap.get(e.node2).setBusLine();
            if (n1.busNeighbors == null) {
                n1.busNeighbors = new ArrayList<>();
            }
            if (n2.busNeighbors == null) {
                n2.busNeighbors = new ArrayList<>();
            }
            n1.busNeighbors.add(n2);
            n2.busNeighbors.add(n1);
        });

        MyNodeConnection.readCSV(String.format(filename, "rail", "lines")).forEach(e -> {
            MyNode n1 = nodeMap.get(e.node1).setRailLine();
            MyNode n2 = nodeMap.get(e.node2).setRailLine();
            if (n1.railNeighbors == null) {
                n1.railNeighbors = new ArrayList<>();
            }
            if (n2.railNeighbors == null) {
                n2.railNeighbors = new ArrayList<>();
            }
            n1.railNeighbors.add(n2);
            n2.railNeighbors.add(n1);
        });

        for (MyNode n : nodeMap.values()) {
            MyNodeBucket c = get(container, getX(n.lon.floatValue()), getY(n.lat.floatValue()));
            c.addNode(n);
        }
        log.info("loadCSV done");
    }


    private List<MyNode> getList(NodeType nodeType) {
        return lists.computeIfAbsent(nodeType, k -> new ArrayList<>());
    }

    private void addNode(MyNode n) {
        if (n.isRailStop) {
            getList(NodeType.RAIL_STOP).add(n);
        }
        if (n.isRailLine) {
            getList(NodeType.RAIL_LINE).add(n);
        }
        if (n.isBusStop) {
            getList(NodeType.BUS_STOP).add(n);
        }
        if (n.isBusLine) {
            getList(NodeType.BUS_LINE).add(n);
        }
    }

    public enum NodeType {
        BUS_STOP,
        BUS_LINE,
        RAIL_STOP,
        RAIL_LINE,
    }

    private static MyNodeBucket emptyBucket = new MyNodeBucket();
    static {
        EnumSet.allOf(NodeType.class).forEach(e -> emptyBucket.lists.put(e, Collections.emptyList()));
    }

    public static class NodeWithDistance {
        public static final MyNode defaultNode = new MyNode(-1).setLatLon(0d,0d);
        public MyNode node;
        public double distance = Double.MAX_VALUE;

        public NodeWithDistance set(MyNode node, double distance) {
            this.node = node;
            this.distance = distance;
            return this;
        }

        public MyNode getNodeOrDefault() {
            return node == null ? defaultNode : node;
        }

        public double getDistanceOrMax(double max) {
            return node == null ? max : distance;
        }
    }

    public Map<NodeType, List<MyNode>> lists = new EnumMap<>(NodeType.class);

    public static final int MAX_LON = 180;
    public static final int MAX_LAT = 80;

    public static final int MULT_LON = 64;
    public static final int MULT_LAT = 64;

    public static final int DIM_X = MAX_LON * MULT_LON * 2;
    public static final int DIM_Y = MAX_LAT * MULT_LAT * 2;

    public static int getX(double lon) {
        return (int)((lon + MAX_LON) * MULT_LON);
    }

    public static int getY(double lat) {
        return (int)((lat + MAX_LAT) * MULT_LAT);
    }

//    public static MyNodeBucket getID(Map<Integer, MyNodeBucket> container, float lat, float lon) {
//        return container.get(getX(lon) + getY(lat) * DIM_X);
//    }

    public static MyNodeBucket get(Map<Integer, MyNodeBucket> container, int x, int y) {
//        return container.get(Math.floorMod(x, DIM_X) + Math.floorMod(y, DIM_Y) * DIM_X);
        return container.computeIfAbsent(
                Math.floorMod(x, DIM_X) + Math.floorMod(y, DIM_Y) * DIM_X,
                k -> new MyNodeBucket());
    }

    public static MyNodeBucket getOrEmpty(Map<Integer, MyNodeBucket> container, int x, int y) {
//        return container.get(Math.floorMod(x, DIM_X) + Math.floorMod(y, DIM_Y) * DIM_X);
        MyNodeBucket ret = container.get(Math.floorMod(x, DIM_X) + Math.floorMod(y, DIM_Y) * DIM_X);
        return ret == null ? emptyBucket : ret;
    }

    public static EnumMap<NodeType, NodeWithDistance> getNearestNodes(Map<Integer, MyNodeBucket> container,
                                                                      double lat, double lon, int maxRadius) {
        EnumMap<NodeType, NodeWithDistance> ret = new EnumMap<>(NodeType.class);
        for (NodeType t : NodeType.values()) {
            ret.put(t, new NodeWithDistance());
        }
        Set<NodeType> neededTypesStops = EnumSet.of(NodeType.BUS_STOP, NodeType.RAIL_STOP);
        Set<NodeType> neededTypesLines = EnumSet.of(NodeType.BUS_LINE, NodeType.RAIL_LINE);
        maxRadius = Math.max(2, maxRadius);
        int r = 1;
        int x = getX(lon);
        int y = getY(lat);
        MyNode tmp = new MyNode(-1).setLatLon((double) lat, (double) lon);

        List<MyNodeBucket> toCheck = new LinkedList<>();
        toCheck.add(getOrEmpty(container, x, y));
        while (r < maxRadius) {
            toCheck.add(getOrEmpty(container, x - r, y - r));
            toCheck.add(getOrEmpty(container, x - r, y + r));
            toCheck.add(getOrEmpty(container, x + r, y - r));
            toCheck.add(getOrEmpty(container, x + r, y + r));
            for (int a = -r + 1; a < r; a++) {
                toCheck.add(getOrEmpty(container, x + r, y + a));
                toCheck.add(getOrEmpty(container, x - r, y + a));
                toCheck.add(getOrEmpty(container, x + a, y - r));
                toCheck.add(getOrEmpty(container, x + a, y + r));
            }
            for (MyNodeBucket b : toCheck) {
                for (NodeType t : neededTypesStops) {
                    NodeWithDistance best = ret.get(t);
                    List<MyNode> nodes = b.lists.get(t);
                    if (nodes != null) {
                        for (MyNode node : nodes) {
                            double d = GeoMath2.computeDistance(node, lat, lon);
                            if (d < best.distance) {
                                best.set(node, d);
                            }
                        }
                    }
                }
                for (NodeType t : neededTypesLines) {
                    NodeWithDistance best = ret.get(t);
                    List<MyNode> nodes = b.lists.get(t);
                    if (nodes != null) {
                        for (MyNode node : nodes) {
//                            float ATDistanceFromNearestNode = GeoMath.computeAlongTrackDistance(nearestNode, neighborNode, currentPosition);
//                            float ATDdistanceFromNeighborNode = GeoMath.computeAlongTrackDistance(neighborNode, nearestNode, currentPosition);
//
//                            float maxDistance = GeoMath.computeDistance(neighborNode, nearestNode);
//
//                            // add intermediate point only if it is located between nearest node and neighbor node
//                            if (ATDistanceFromNearestNode + ATDdistanceFromNeighborNode <= maxDistance) {
//                                float ratio = ATDistanceFromNearestNode / maxDistance;
//                                OSMNode intermediatePoint = GeoMath.computeIntermediatePoint(nearestNode, neighborNode, ratio);
//                                intermediatePointList.add(intermediatePoint);
//                            }
                            double d1 = GeoMath2.computeDistance(node, lat, lon);
//                            float d2 = GeoMath2.computeDistance(node, lat, lon);
                            if (d1 < best.distance) {
                                best.set(node, d1);
                            }
                            List<MyNode> neighbors = t == NodeType.RAIL_LINE ? node.railNeighbors : node.busNeighbors;
                            if (neighbors == null) {
                                continue;
                            }
                            for (MyNode n2 : neighbors) {
                                double t1 = GeoMath2.computeAlongTrackDistance(node, n2, tmp);
                                double t2 = GeoMath2.computeAlongTrackDistance(n2, node, tmp);
                                double m = GeoMath2.computeDistance(node, n2);
//                                float d2 = GeoMath2.computeDistance(n2, lat, lon);
//                                if (d2 < best.distance) {
//                                    best.set(node, d2);
//                                }
//                                float max = Math.max(d1, d2);
//                                if (t == NodeType.RAIL_LINE) System.out.print(".");
                                if (t1 + t2 <= m * 1.1) {
//                                    if (t == NodeType.RAIL_LINE) System.out.println("+");
                                    double ratio = d1 / m;
                                    MyNode  intermediatePoint = GeoMath2.computeIntermediatePoint(node, n2, ratio);
                                    double d2 = GeoMath2.computeDistance(intermediatePoint, lat, lon);
                                    if (d2 < best.distance) {
//                                        if (t == NodeType.RAIL_LINE) System.out.print("!");
                                        best.set(intermediatePoint, d2);
                                    }
                                }

                            }
                        }
                    }
                }
            }
            toCheck.clear();
            if (ret.values().parallelStream().noneMatch(e -> e.node == null)) {
                break;
            }
            neededTypesStops.removeAll(ret.entrySet().parallelStream()
                    .filter(e -> e.getValue().node != null)
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toSet()));
            neededTypesLines.removeAll(ret.entrySet().parallelStream()
                    .filter(e -> e.getValue().node != null)
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toSet()));
            r++;
        }
//        ret.forEach((e1, e2) -> System.out.print("\n" + e1.toString() + ":" + e2.node.id));
        return ret;
    }


//    public static class MyNodeBucket extends MapBucket<MyNode> {}

//    public static MyNodeBucket[][] initContainer() {
//        return new MyNodeBucket[DIM_X][DIM_Y];
//    }

    public static Map<Integer, MyNodeBucket> initContainer() {
        return new HashMap<>();
    }



//    public static List<MyNode> getNodes(MyNodeBucket[][] container, NodeType nodeType, float lat, float lon, int r) {
//        int x = getX(lon);
//        int y = getY(lat);
//        if (r == 0) {
//            return getNodes(container[x][y], nodeType);
//        }
//        for (int i = 0; i < ; i++) {
//
//        }
//        return null;
//    }

//    public static List<MyNode> getNodes(MyNodeBucket bucket, NodeType nodeType) {
//        List<MyNode> ret = bucket.lists.get(nodeType);
//        if (ret == null) {
//            return Collections.emptyList();
//        }
//        return ret;
//    }
}
