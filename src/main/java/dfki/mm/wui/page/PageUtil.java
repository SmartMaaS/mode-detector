package dfki.mm.wui.page;

import dfki.mm.DataHolder;
import dfki.mm.request.MyApiField;
import dfki.mm.tracks.GpsTrack;
import org.jsoup.nodes.Element;

import javax.swing.text.html.HTML;

public class PageUtil {
    public static void updateTracks(Element root, String id) {
        Element element = root.getElementById(id);
        element.children().remove();
        for (GpsTrack track : DataHolder.INSTANCE.trackManager.tracks) {
            element.appendChild(new Element(HTML.Tag.OPTION.toString())
                    .attr("value", String.valueOf(track.id)).appendText(track.name));
        }

    }
    public static  void updateTracks(Element root) {
        updateTracks(root, MyApiField.track.name());
    }
}
