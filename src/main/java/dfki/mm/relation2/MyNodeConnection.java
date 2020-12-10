package dfki.mm.relation2;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Objects;
import java.util.stream.Stream;

public class MyNodeConnection {

    public static final String DELIMITER = ";";


    long node1, node2;
    boolean isBus, isRail;

    public MyNodeConnection(long node1, long node2, boolean isBus, boolean isRail) {
        long n1 = Math.min(node1, node2);
        long n2 = Math.max(node1, node2);
        this.node1 = n1;
        this.node2 = n2;
        this.isBus = isBus;
        this.isRail = isRail;
    }

    private MyNodeConnection(long node1, long node2) {
        this.node1 = node1;
        this.node2 = node2;
    }

    public MyNodeConnection setBus(boolean bus) {
        isBus = bus;
        return this;
    }

    public MyNodeConnection setRail(boolean rail) {
        isRail = rail;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MyNodeConnection that = (MyNodeConnection) o;
        return node1 == that.node1 &&
                node2 == that.node2 &&
                isBus == that.isBus &&
                isRail == that.isRail;
    }

    @Override
    public int hashCode() {
        return Objects.hash(node1, node2, isBus, isRail);
    }

    public static String header() {
        return String.join(DELIMITER,
                "id1",
                "id2"//,
//                "bus",
//                "rail"
        );
    }

    public String toCSV() {
        return String.join(DELIMITER,
                String.valueOf(node1),
                String.valueOf(node2)//,
//                isBus ? "1" : "",
//                isRail ? "1" : ""
        );
    }

    public static MyNodeConnection fromCSV(String line) {
        String[] vals = line.trim().split(DELIMITER, -1);
        if (vals.length != 2) {
            throw new RuntimeException(vals.length + ": " + line);
        }
        try {
            MyNodeConnection n =  new MyNodeConnection(
                    Long.parseLong(vals[0]),
                    Long.parseLong(vals[1])
                    );
            return n;
        } catch (Exception e) {
            throw new RuntimeException(line, e);
        }
    }

    public static void writeLines(String filename, Stream<MyNodeConnection> nodes) throws IOException {
        try (BufferedWriter bw = Files.newBufferedWriter(Paths.get(filename))) {
            bw.write(MyNodeConnection.header());
            bw.newLine();
            Iterator<MyNodeConnection> i = nodes.iterator();
            while (i.hasNext()) {
                bw.write(i.next().toCSV());
                bw.newLine();
            }
        }
    }

    public static Stream<MyNodeConnection> readCSV(String filename) throws IOException {
        return FilterUtils.readCSV(filename).stream().map(MyNodeConnection::fromCSV);
    }

}
