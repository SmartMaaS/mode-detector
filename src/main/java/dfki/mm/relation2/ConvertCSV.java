package dfki.mm.relation2;

import org.openstreetmap.osmosis.core.domain.v0_6.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ConvertCSV {
    private static final Logger log = LoggerFactory.getLogger(ConvertCSV.class);


    public static void main(String[] args) throws IOException {
//        String from = "/dev/shm/1/berlin-transport-0531.osm.pbf";
//        String from = "/dev/shm/1/saarland-transport-0531.osm.pbf";
//        String to = "/dev/shm/1/0601/berlin-%s-%s-0604.csv";
//        String from = "input/germany-transport-0531.osm.pbf";
//        String to = "/dev/shm/1/0601/germany-%s-%s-0601.csv";
//        convert(from, to);

//        convert("/dev/shm/1/berlin-transport-0531.osm.pbf", "/dev/shm/1/berlin-%s-%s-0604.csv");
        convert("/dev/shm/1/saarland-transport-0531.osm.pbf", "/dev/shm/1/saarland-%s-%s-0604.csv");
    }

    public static void convert(String from, String to) throws IOException {

        log.info("filter {} -> {}", from, to);
        RelationFilter f = new RelationFilter();
        f.load(from);

//        Map<Long, Relation> relationMap = f.allRelations.parallelStream().collect(Collectors.toMap(Entity::getId, e -> e));
//        Map<Long, Way> wayMap = f.allWays.parallelStream().collect(Collectors.toMap(Entity::getId, e -> e));
        Set<Long> busLineWay = new HashSet<>();
        Set<Long> railLineWay = new HashSet<>();

        Set<Long> busStopWay = new HashSet<>();
        Set<Long> railStopWay = new HashSet<>();

        Set<Long> busStopNode = new HashSet<>();
        Set<Long> railStopNode = new HashSet<>();


//        Objects.requireNonNull(FilterUtils.load(from)).run();
        for (Relation r : f.allRelations) {
            String type = r.getTags().parallelStream().filter(e -> "type".equals(e.getKey()))
                    .map(Tag::getValue)
                    .findFirst().orElse("xxx");
            if ("network".equals(type) || "route_master".equals(type)) {
                continue;
            }
            Stream<String> routes = r.getTags().parallelStream().filter(e -> "route".equals(e.getKey())).map(Tag::getValue).distinct();
            boolean isBus = false;
            boolean isRail = false;
            for (String route : routes.collect(Collectors.toSet())) {
                switch (route) {
                    case "train":       // original train
                    case "rail":
                    case "subway":      // original train
                    case "light_rail":  // original train
                        isRail = true;
                        break;
                    case "tram":        // original bus
                    case "bus":         // original bus
                    case "trolleybus":
                    case "funicular":
                    case "ferry":
                        isBus = true;
                        break;
                    default:
                        log.warn("Route: {}", route);
                }
            }
            if (!isBus && !isRail) {
                log.warn("What relation??? {}", r);
                continue;
            }
            for (RelationMember m : r.getMembers()) {
                switch (m.getMemberRole()) {
                    case "backward_stop":
                    case "forward_stop":
                    case "stop":
                    case "stop_entry_only":
                    case "stop_exit_only":
                    case "platform":
                    case "platform_entry_only":
                    case "platform_exit_only":
                        switch (m.getMemberType()) {
                            case Node:
                                if (isBus) {
                                    busStopNode.add(m.getMemberId());
                                }
                                if (isRail) {
                                    railStopNode.add(m.getMemberId());
                                }
                                break;
                            case Way:
                                if (isBus) {
                                    busStopWay.add(m.getMemberId());
                                }
                                if (isRail) {
                                    railStopWay.add(m.getMemberId());
                                }
                                break;
                        }
                        break;
                    case "backward":
                    case "forward":
                    case "route":
                    case "":
                        switch (m.getMemberType()) {
                            case Node:
                                // map issue, probably someone forgot to specify a relation of the stop/platform
                                log.warn("(stop) Node without role: {} -> {}", r, m);
                                if (isBus) {
                                    busStopNode.add(m.getMemberId());
                                }
                                if (isRail) {
                                    railStopNode.add(m.getMemberId());
                                }
                                break;
                            case Way:
                                if (isBus) {
                                    busLineWay.add(m.getMemberId());
                                }
                                if (isRail) {
                                    railLineWay.add(m.getMemberId());
                                }
                                break;
                            default:
                                log.warn("Waht is it? {} -> {}", r, m);
                                break;
                        }
                        break;
                    default:
                        log.warn("other relation role: {} {}", r, m);
                }
            }
        }
        f.allRelations.clear();
        log.info("Relations processed");

        Set<MyNodeConnection> linesSet = new HashSet<>();
        for (Way w : f.allWays) {
            if (busStopWay.contains(w.getId())) {
                w.getWayNodes().forEach(e -> busStopNode.add(e.getNodeId()));
            }
            if (railStopWay.contains(w.getId())) {
                w.getWayNodes().forEach(e -> railStopNode.add(e.getNodeId()));
            }
            boolean bus = busLineWay.contains(w.getId());
            boolean rail = railLineWay.contains(w.getId());
            if (bus || rail) {
                long prev = -1;
                for (WayNode n : w.getWayNodes()) {
                    long id = n.getNodeId();
                    if (prev != -1) {
                        linesSet.add(new MyNodeConnection(prev, id, bus, rail));
                    }
                    prev = id;
                }
            }
        }
        log.info("Ways processed");

        busStopWay.clear();
        busLineWay.clear();
        railStopWay.clear();
        railLineWay.clear();
        f.allWays.clear();
        List<MyNodeConnection> lines = new ArrayList<>(linesSet);
        linesSet.clear();

        MyNode.writeStops(String.format(to, "bus", "stops"),
                f.allNodes.parallelStream()
                        .filter(e -> busStopNode.contains(e.getId()))
                        .map(e -> new MyNode(e.getId()).setLatLon(e.getLatitude(), e.getLongitude()))
                        .collect(Collectors.toUnmodifiableList()));
        MyNode.writeStops(String.format(to, "rail", "stops"),
                f.allNodes.parallelStream()
                        .filter(e -> railStopNode.contains(e.getId()))
                        .map(e -> new MyNode(e.getId()).setLatLon(e.getLatitude(), e.getLongitude()))
                        .collect(Collectors.toUnmodifiableList()));

//        busStopNode.clear();
//        railStopNode.clear();
        lines.forEach(e -> {
            railStopWay.add(e.node1);
            railStopWay.add(e.node2);
        } );
        MyNode.writeStops(String.format(to, "way", "nodes"),
                f.allNodes.parallelStream()
                        .filter(e -> railStopWay.contains(e.getId()))
                        .map(e -> new MyNode(e.getId()).setLatLon(e.getLatitude(), e.getLongitude()))
                        .collect(Collectors.toUnmodifiableList()));
        railStopWay.clear();

        MyNodeConnection.writeLines(String.format(to, "bus", "lines"),
                lines.stream().filter(e -> e.isBus));
        MyNodeConnection.writeLines(String.format(to, "rail", "lines"),
                lines.stream().filter(e -> e.isRail));
        log.info("Done");

    }





}
