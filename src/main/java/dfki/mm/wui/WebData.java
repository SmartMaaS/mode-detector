package dfki.mm.wui;

import dfki.mm.Constants;
import dfki.mm.map.OSMMap;
import dfki.mm.wui.page.*;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.text.html.HTML;
import java.util.*;

public class WebData {

    private static final Logger log = LoggerFactory.getLogger(WebData.class);


    public LinkedHashMap<String, PageTemplate> pages = new LinkedHashMap<>();
//    public List<String> pagesList = new ArrayList<>();

    public final List<OSMMap> osmMapTemplates = new ArrayList<>();


    //    public AtomicBoolean isMapLoaded = new AtomicBoolean(false);
//    public AtomicBoolean isMapLoading = new AtomicBoolean(false);
    public StaticPage staticPage;
    public final TracksPage tracksPage;
    public final ModelistPage modelistPage;
    public final ExtPostPage extPostPage;
    public final ExtPcaPage extPcaPage;
    public final ExtScalePage extScalePage;

//    public final ModelPage modelPage;
    public final BasicPage basicPage;


    public WebData() {
        loadOsmTemplates();

        staticPage = new StaticPage(null, null, null);

        basicPage = new BasicPage("/base.html", null, null);

        addPage(new BasicPage("/index.html", "Main", "main"));
        addPage(tracksPage = new TracksPage("/tracks.html", "Tracks", Constants.PAGE_TRACKS));
        addPage(modelistPage = new ModelistPage("/modelist.html", "Models", Constants.PAGE_MODELIST));
        addPage(extPostPage = new ExtPostPage("/modelist.html", "Post-processing", Constants.PAGE_EXT_POST));
        addPage(extPcaPage = new ExtPcaPage("/modelist.html", "PCA", Constants.PAGE_EXT_PCA));
        addPage(extScalePage = new ExtScalePage("/modelist.html", "Normalize", Constants.PAGE_EXT_SCALE));
//        pages.put("model", modelPage = new ModelPage("/model.html", "Model", Constants.PAGE_MODEL));
//        addPage(new SavePage("/save.html", "Save", "save"));
        addPage(new MapPage("/index.html", "Map", "map"));

        Element elementNav = new Element(HTML.Tag.DIV.toString()).attr("class", "topnav");
//        elementNav.append("<div>t02 mode detector</div>\n");
        elementNav.appendElement(HTML.Tag.DIV.toString()).text("t02 mode detector");
        elementNav.appendText("\n");
//        Map<PageTemplate, String> pp = new HashMap<>();
//        int i = 0;
        for (PageTemplate value : pages.values()) {
            String id = "nav-" + value.getPageName(); //String.valueOf(i);
            Element element = new Element(HTML.Tag.A.toString())
                    .attr("id", id)
                    .attr("href", value.getPageUrlString());
            element.appendText(value.getPageTitle());
            elementNav.appendChild(element);
            elementNav.appendText("\n");
//            list.add(element);
//            elementNav.appendChild(new Element())
        }
        for (PageTemplate value : pages.values()) {
            String id = "nav-" + value.getPageName(); //String.valueOf(i);
            var nc = elementNav.clone();
            nc.getElementById(id).toggleClass("active");
            value.getDoc().body().prependChild(nc);
            value.init();
        }

//        for (Map.Entry<String, PageTemplate> stringPageTemplateEntry : pages.entrySet()) {

//        }

//            addPage(new MainPage("/index.html", "Main"), "main");
//
//            PageTemplate p = new TracksPage("/index.html", "Tracks");
//            addPage(p, "gps");
////            pages.put("uploaded", p);
//
//            addPage(new SavePage("/save.html", "Save"), "save");
//        updateNav();

//        Element element = modelPage.getDoc().getElementById("my-nav");
//        element.children().remove();
//        modelistPage.getDoc().getElementById("my-nav").children().forEach(c -> element.appendChild(c.clone()));

    }


    public void loadOsmTemplates() {
        osmMapTemplates.add(new OSMMap("/osm-clicker-template.html"));
        osmMapTemplates.add(new OSMMap("/osm-clicker-template-mapbox.html"));
        osmMapTemplates.add(new OSMMap("/osm-clicker-template-osm.html"));
    }

    public void addPage(PageTemplate page) {//}, String address) {
        pages.put(page.getPageName(), page);
//        pagesList.add(page.getAddress());
    }

//    public void updateNav() {
////        Element element = doc.getElementById("gps-list")
//        List<Element> list = new ArrayList<>();
//        for (String a : pagesList) {
//            Element element = new Element(HTML.Tag.A.toString()).attr("href", "/?page=" + a);
//            element.appendText(pages.get(a).getPageTitle());
//            list.add(element);
//        }
//        {
//            Element nav = basicPage.getDoc().getElementById("my-nav");
//            nav.children().remove();
//            list.forEach(e -> nav.appendChild(e.clone()));
//        }
//        int i = 0;
//        for (String a : pagesList) {
//            i++;
//            try {
//                Document d = pages.get(a).getDoc();
//                Element nav = d.getElementById("my-nav");
//                nav.children().forEach(Node::remove);
//                int j = 0;
//                for (Element l : list) {
//                    j++;
//                    Element child = l.clone();
//                    if (i == j) {
//                        child.addClass("active");
//                    }
//                    nav.appendChild(child);
//                }
//            } catch (RuntimeException e) {
//                log.error("Cannot update nav for {}", a, e);
//                throw e;
//            }
//        }
//    }

}
