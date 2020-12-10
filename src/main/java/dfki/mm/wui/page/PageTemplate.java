package dfki.mm.wui.page;

import dfki.mm.request.MyApiField;
import dfki.mm.request.MyHttpRequest;
import dfki.mm.wui.ResourceLoader;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class PageTemplate {

    private static final Logger log = LoggerFactory.getLogger(PageTemplate.class);

    public static final String HEADER_LIST = "head-existing";
    public static final String HEADER_CREATE = "head-create";
    public static final String HIDDEN_PAGE = "form-page";


    private final String templateFilename;
    private String pageTitle;
    private String pageName;
    protected Document doc;

    protected PageTemplate(String templateFilename) {
        this.templateFilename = templateFilename;
    }

    public PageTemplate(String filename, String pageTitle, String pageName) {
        this.templateFilename = filename;
        this.pageTitle = pageTitle;
        this.pageName = pageName;
        reload();
    }

    public void reload() {
        String string = ResourceLoader.readString(templateFilename);
        doc = Jsoup.parse(string);
    }

    public abstract void init();

    public abstract void handle(MyHttpRequest http);

    /**
     * You don't need it.
     */
    public String getPageTitle() {
        return pageTitle;
    }

    public String getPageUrlString() {
        return "/?" + MyApiField.page.toString() + "=" + pageName;
    }

    /**
     * Gets the page address (?page=XXX)
     * @return XXX
     */
    public String getPageName() {
        return pageName;
    }

    /**
     * Gets the root document
     */
    public Document getDoc() {
        return doc;
    }

    protected Document getDocCopy() {
        return doc.clone();
    }


    /**
     * Get the page contents as a String.
     * @return Generated page html
     */
    protected String getPageString() {
        return doc.toString();
    }

    public String getUrl() {
        return "/?page=" + pageName;
    }
}