package dfki.mm.tracks.extension;

import dfki.mm.functional.PostprocessUtilOriginal;
import dfki.mm.tracks.Field;
import dfki.mm.tracks.GpsTrack;
import dfki.mm.tracks.stat.FieldStatistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class PostExtension extends Extension {

    private static final Logger log = LoggerFactory.getLogger(PostExtension.class);


    private final List<Field<?>> fields;
    private final List<Extension> requires;
    private final Field.ModeField originalField;
    private final String name;

    public final Field.ModeField predictionPost;
    public final FieldStatistics fieldStatistics;

    @Override
    public List<Extension> requires() {
        return requires;
    }

    @Override
    public String getInfo() {
        return "prediction data";
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<Field<?>> getFields() {
        return fields;
    }


    public PostExtension(Field.ModeField requires) {
        super();
        this.name = "post40-" + requires.extension.getName();
        this.originalField = requires;
        this.requires = List.of(requires.extension);
        this.fields = List.of(this.predictionPost = Field.newModeField(this, "p"));
        this.fieldStatistics = new FieldStatistics(predictionPost);
    }

    @Override
    public void compute(GpsTrack track) {
        try {
            PostprocessUtilOriginal.process(track, this.originalField, this.predictionPost, 40);
            fieldStatistics.compute(track);
        } catch (Exception e) {
            log.error("", e);
//            track.points.forEach(point -> predictionPost.set(point, null));
        }
    }

}
