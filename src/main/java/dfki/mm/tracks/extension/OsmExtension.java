package dfki.mm.tracks.extension;

import dfki.mm.DataHolder;
import dfki.mm.tracks.Field;
import dfki.mm.tracks.FieldList;
import dfki.mm.relation2.MyNodeBucket;
import dfki.mm.tracks.GpsTrack;

import java.util.EnumMap;
import java.util.List;

public class OsmExtension extends Extension {

    public final int DEFAULT_DISTANCE = 88;

    private final List<Field<?>> fields;
    private final List<Extension> requires;

    private final MainExtension mainExtension;

    @Override
    public List<Extension> requires() {
        return requires;
    }

    @Override
    public String getInfo() {
        return "osm data";
    }

    @Override
    public String getName() {
        return "osm";
    }

    @Override
    public List<Field<?>> getFields() {
        return fields;
    }

    public final Field.DoubleField distanceToTrainLine;
    public final Field.DoubleField distanceToTrainStation;
    public final Field.DoubleField distanceToBusLine;
    public final Field.DoubleField distanceToBusStop;

    public OsmExtension(MainExtension mainExtension) {
        super();
        this.requires = List.of(this.mainExtension = mainExtension);
//        updateFields(mainExtension);
        this.fields = List.of(
                this.distanceToTrainLine = Field.newDoubleField(this, "distanceToTrainLine"),
                this.distanceToTrainStation = Field.newDoubleField(this, "distanceToTrainStation"),
                this.distanceToBusLine = Field.newDoubleField(this, "distanceToBusLine"),
                this.distanceToBusStop = Field.newDoubleField(this, "distanceToBusStop")
        );
    }

    @Override
    public void compute(GpsTrack track) {
        FieldList.PointIterator p = track.getGpsPoint();
        FieldList<Double>.FieldValue lat = p.get(mainExtension.lat); //ExtensionManager.INSTANCE.
        FieldList<Double>.FieldValue lon = p.get(mainExtension.lon); //ExtensionManager.INSTANCE.
        FieldList<Double>.FieldValue distanceToBusLine = p.get(this.distanceToBusLine);
        FieldList<Double>.FieldValue distanceToBusStop = p.get(this.distanceToBusStop);
        FieldList<Double>.FieldValue distanceToTrainLine = p.get(this.distanceToTrainLine);
        FieldList<Double>.FieldValue distanceToTrainStation = p.get(this.distanceToTrainStation);
        while (p.next()) {
            EnumMap<MyNodeBucket.NodeType, MyNodeBucket.NodeWithDistance> d =
                    DataHolder.INSTANCE.mapData.findNearest(lat.get(), lon.get(), 5);

            distanceToBusLine.set(d.get(MyNodeBucket.NodeType.BUS_LINE).getDistanceOrMax(DEFAULT_DISTANCE));
            distanceToBusStop.set(d.get(MyNodeBucket.NodeType.BUS_STOP).getDistanceOrMax(DEFAULT_DISTANCE));
            distanceToTrainLine.set(d.get(MyNodeBucket.NodeType.RAIL_LINE).getDistanceOrMax(DEFAULT_DISTANCE));
            distanceToTrainStation.set(d.get(MyNodeBucket.NodeType.RAIL_STOP).getDistanceOrMax(DEFAULT_DISTANCE));

        }

    }

}
