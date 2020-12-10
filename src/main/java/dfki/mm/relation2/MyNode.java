package dfki.mm.relation2;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

public class MyNode {

    public static final String DELIMITER = ";";

    public long id;
    public Double lat, lon;
    public boolean isBusStop;
    public boolean isBusLine;
    public boolean isRailStop;
    public boolean isRailLine;
    public List<MyNode> busNeighbors;
    public List<MyNode> railNeighbors;


    public MyNode(long id) {
        this.id = id;
    }

    public MyNode setLatLon(Double lat, Double lon) {
        this.lon = lon;
        this.lat = lat;
        return this;
    }

    public MyNode setBusStop() {
        isBusStop = true;
        setBusLine();
        return this;
    }

    public MyNode setBusLine() {
        isBusLine = true;
        return this;
    }

    public MyNode setRailStop() {
        isRailStop = true;
        setRailLine();
        return this;
    }


    public MyNode setRailLine() {
        isRailLine = true;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MyNode myNode = (MyNode) o;
        return id == myNode.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public static String header() {
        return String.join(DELIMITER,
                "id",
                "lat",
                "lon"
        );
    }
    public String toCSV() {
        return String.join(DELIMITER,
                String.valueOf(id),
                String.valueOf(lat),
                String.valueOf(lon)//,
//                isBusLine ? "1" : "",
//                isBusStop ? "1" : "",
//                isRailLine ? "1" : "",
//                isRailStop ? "1" : ""
        );
    }

    public static MyNode fromCSV(String line) {
        String[] vals = line.trim().split(DELIMITER, -1);
        if (vals.length != 3) {
            throw new RuntimeException(vals.length + ": " + line);
        }
        try {
            MyNode n =  new MyNode(Long.parseLong(vals[0]));
            if (!"null".equals(vals[1]) && !"null".equals(vals[2])) {
                n.lat  = Double.parseDouble(vals[1]);
                n.lon  = Double.parseDouble(vals[2]);
            }
//            n.isBusLine = vals[3].length() > 0;
//            n.isBusStop = vals[4].length() > 0;
//            n.isRailLine = vals[5].length() > 0;
//            n.isRailStop = vals[6].length() > 0;
            return n;
        } catch (Exception e) {
            throw new RuntimeException(line, e);
        }
    }

    public static void writeStops(String filename, Collection<MyNode> nodes) throws IOException {
        try (BufferedWriter bw = Files.newBufferedWriter(Paths.get(filename))) {
            bw.write(MyNode.header());
            bw.newLine();
            Set<MyNode> nodesSet = new TreeSet<>(Comparator.comparingLong(o -> o.id));
            nodesSet.addAll(nodes);
            for (MyNode n : nodesSet) {
                bw.write(n.toCSV());
                bw.newLine();
            }
        }
    }

    public static Stream<MyNode> readCSV(String filename) throws IOException {
        return FilterUtils.readCSV(filename).stream().map(MyNode::fromCSV);
//
//        boolean isZip = filename.endsWith(".gz");
//        try (BufferedReader br = isZip
//                ? new BufferedReader(new InputStreamReader(new GZIPInputStream(Files.newInputStream(Paths.get(filename)))))
//                : Files.newBufferedReader(Paths.get(filename))) {
//            String line = br.readLine();
//            //header
//            return br.lines()
//                    .map(String::trim)
//                    .filter(e -> e.length() > 0)
//                    .map(MyNode::fromCSV);
//        }
    }

    public Double getLon() {
        return lon;
    }

    public Double getLat() {
        return lat;
    }
}
