package dfki.mm.relation2;

import crosby.binary.osmosis.OsmosisReader;
import org.openstreetmap.osmosis.core.container.v0_6.EntityContainer;
import org.openstreetmap.osmosis.core.domain.v0_6.*;
import org.openstreetmap.osmosis.core.task.v0_6.Sink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class RelationFilter {

    private static final Logger log = LoggerFactory.getLogger(RelationFilter.class);

    public List<Bound> allBounds = new ArrayList<>();
    public List<Relation> allRelations = new ArrayList<>();
    public List<Way> allWays = new ArrayList<>();
    public List<Node> allNodes = new ArrayList<>();

    public static void main(String[] args) throws IOException {
//        filter(
//                "/dev/shm/1/bremen-latest.osm.pbf",
//                "/dev/shm/1/bremen-transport-0531.osm.pbf"
//                );
//        filter(
//                "input/berlin-latest.osm.pbf",
//                "/dev/shm/1/berlin-transport-0531.osm.pbf"
//                );
        filter(
                "input/saarland-latest.osm.pbf",
                "/dev/shm/1/saarland-transport-0531.osm.pbf"
                );
//        filterOOM(
//                "/dev/shm/1/germany-latest.osm.pbf",
//                "/dev/shm/1/germany-transport-0531.osm.pbf"
//        );

    }

    public static void filterOOM(String from, String to) throws IOException {
        Set<Long> relationSet = new HashSet<>();
        Set<Long> waySet = new HashSet<>();
        Set<Long> nodeSet = new HashSet<>();

        log.info("filterOOM {} -> {}", from, to);
//        RelationFilter f = new RelationFilter();
        Objects.requireNonNull(FilterUtils.load(from, new IgnorantSink() {
            @Override
            public void process(EntityContainer entityContainer) {
//                super.process(entityContainer);
                if (entityContainer.getEntity().getType() == EntityType.Relation) {
                    Relation r = (Relation) entityContainer.getEntity();
                    if (relationTags(r.getTags())) {
                        relationSet.add(r.getId());
                        for (RelationMember m : r.getMembers()) {
                            switch(m.getMemberType()) {
                                case Way:
                                    waySet.add(m.getMemberId());
                                    break;
                                case Node:
                                    nodeSet.add(m.getMemberId());
                                    break;
                                default:
//                            log.warn("Suspicious relation member: {} {} {}", m.getMemberRole(), m, r);
                            }
                        }
                    }

                }
            }
        })).run();
        log.info("filterOOM loaded relations");

        Objects.requireNonNull(FilterUtils.load(from, new IgnorantSink() {
            @Override
            public void process(EntityContainer entityContainer) {
//                super.process(entityContainer);
                if (entityContainer.getEntity().getType() == EntityType.Way) {
                    Way w = (Way) entityContainer.getEntity();
                    if (waySet.contains(w.getId())) {
                        w.getWayNodes().forEach(wayNode -> nodeSet.add(wayNode.getNodeId()));
                    }
                }
            }
        })).run();
        log.info("filterOOM loaded ways");

        Sink out = FilterUtils.getOutSink(to);
        OsmosisReader or = new OsmosisReader(Files.newInputStream(Paths.get(from)));
        or.setSink(new Sink() {
            @Override
            public void process(EntityContainer entityContainer) {
                Entity entity = entityContainer.getEntity();
                switch (entity.getType()) {
                    case Node:
                        if (nodeSet.contains(entity.getId())) {
                            out.process(entityContainer);
                        }
                        break;
                    case Way:
                        if (waySet.contains(entity.getId())) {
                            out.process(entityContainer);
                        }
                        break;
                    case Relation:
                        if (relationSet.contains(entity.getId())) {
                            out.process(entityContainer);
                        }
                        break;
                    case Bound:
                        out.process(entityContainer);
                        break;
                }
            }

            @Override
            public void initialize(Map<String, Object> metaData) {
                out.initialize(metaData);
            }

            @Override
            public void complete() {
                out.complete();
            }

            @Override
            public void close() {
                out.close();
            }
        });
        or.run();
        log.info("filterOOM wrote the file");

    }

    public static void filter(String from, String to) throws IOException {
        log.info("filter {} -> {}", from, to);
        RelationFilter f = new RelationFilter();
        Objects.requireNonNull(FilterUtils.load(from, f.loadSinkWithoutNodesOrBounds())).run();
//        f.load(from);
        log.info("filter loaded file");
        Set<Long> relationSet = new HashSet<>();
        Set<Long> waySet = new HashSet<>();
        Set<Long> nodeSet = new HashSet<>();
//        f.allRelations.parallelStream().filter(r -> r)
        for (Relation r : f.allRelations) {
            if (relationTags(r.getTags())) {
                relationSet.add(r.getId());
                for (RelationMember m : r.getMembers()) {
                    switch(m.getMemberType()) {
                        case Way:
                            waySet.add(m.getMemberId());
                            break;
                        case Node:
                            nodeSet.add(m.getMemberId());
                            break;
                        default:
//                            log.warn("Suspicious relation member: {} {} {}", m.getMemberRole(), m, r);
                    }
                }
            }
        }
        for (Way w : f.allWays) {
            if (waySet.contains(w.getId())) {
                w.getWayNodes().forEach(wayNode -> nodeSet.add(wayNode.getNodeId()));
            }
        }
        log.info("filter filtered data");
        Sink out = FilterUtils.getOutSink(to);
        OsmosisReader or = new OsmosisReader(Files.newInputStream(Paths.get(from)));
        or.setSink(new Sink() {
            @Override
            public void process(EntityContainer entityContainer) {
                Entity entity = entityContainer.getEntity();
                switch (entity.getType()) {
                    case Node:
                        if (nodeSet.contains(entity.getId())) {
                            out.process(entityContainer);
                        }
                        break;
                    case Way:
                        if (waySet.contains(entity.getId())) {
                            out.process(entityContainer);
                        }
                        break;
                    case Relation:
                        if (relationSet.contains(entity.getId())) {
                            out.process(entityContainer);
                        }
                        break;
                    case Bound:
                        out.process(entityContainer);
                        break;
                }
            }

            @Override
            public void initialize(Map<String, Object> metaData) {
                out.initialize(metaData);
            }

            @Override
            public void complete() {
                out.complete();
            }

            @Override
            public void close() {
                out.close();
            }
        });
        or.run();
        log.info("filter wrote the file");
    }

    public static boolean relationTags(Collection<Tag> tags) {
        for (Tag t : tags) {
            switch (t.getKey()) {
                case "type":
                    switch (t.getValue()) {
                        case "route":
//                            return true;
                            break;
                        default:
                            return false;
                    }
//                case "line":
                case "route":
                    switch (t.getValue()) {
                        case "train":
                        case "rail":
                        case "subway":
                        case "light_rail":

                        case "bus":
                        case "trolleybus":
                        case "funicular":
                        case "ferry":

                            return true;
                        default:
                            return false;

                    }
            }
        }
        return false;
//        r.getTags().parallelStream()
//                .anyMatch(tag -> {
//                    return "type".equals(tag.getKey()) &&
//                            ("route".equals(tag.getValue()) || "public_transport".equals(tag.getValue()));
//                    switch (tag.getKey()) {
//                        case "type":
//
//                    }
//                         return "type".equals(tag.getKey()) && "route".equals(tag.getValue());
//                }
    }

    public void clear() {
        allBounds.clear();
        allRelations.clear();
        allWays.clear();
        allNodes.clear();
    }

    private Sink loadSinkWithoutNodesOrBounds() {
        return new IgnorantSink() {
            @Override
            public void process(EntityContainer entityContainer) {
                Entity entity = entityContainer.getEntity();
                switch (entity.getType()) {
                    case Way:
                        allWays.add((Way) entity);
                        break;
                    case Relation:
                        allRelations.add((Relation) entity);
                        break;
                }
            }
        };
    }

    private Sink loadSink() {
        return new IgnorantSink() {
            @Override
            public void process(EntityContainer entityContainer) {
                Entity entity = entityContainer.getEntity();
                switch (entity.getType()) {
                    case Node:
                        allNodes.add((Node) entity);
                        break;
                    case Way:
                        allWays.add((Way) entity);
                        break;
                    case Relation:
                        allRelations.add((Relation) entity);
                        break;
                    case Bound:
                        allBounds.add((Bound) entity);
                        break;
                }
            }
        };
    }

    public void load(String filename) throws IOException {
        Objects.requireNonNull(FilterUtils.load(filename, loadSink())).run();
    }

//    private void load(String filename, Sink sink) throws IOException {
//
//    }


}
