package dfki.mm.wui.builder;

import org.jsoup.nodes.Element;

import javax.swing.text.html.HTML;
import java.util.LinkedList;

public class Table {
    public LinkedList<Element> rows = new LinkedList<>();
    public Element lastCell;

    public Element newRow() {
        Element ret = new Element(HTML.Tag.TR.toString());
        rows.addLast(ret);
        return ret;
    }

    public Element newCell() {
        Element ret = new Element(HTML.Tag.TD.toString());
        rows.getLast().appendChild(ret);
        lastCell = ret;
        return ret;
    }

    public Element lastRow() {
        return rows.getLast();
    }

    public Element lastCell() {
        return lastCell;
    }

    public Element build() {
        Element ret = new Element(HTML.Tag.TABLE.toString());
        rows.forEach(ret::appendChild);
        return ret;
    }
}
