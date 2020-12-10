package dfki.mm.util;

import org.eclipse.jetty.client.util.MultiPartContentProvider;
import org.eclipse.jetty.client.util.StringContentProvider;

import java.nio.charset.Charset;
import java.util.Collection;
import java.util.List;

public class HttpJettyUtil {
    public static MultiPartContentProvider createMultiPart(Collection<String> data, List<String> filenames) {
        MultiPartContentProvider multiPart = new MultiPartContentProvider();
        int i = 0;
        for (String f : data) {
            multiPart.addFilePart("file", filenames == null ? "file-" + i : filenames.get(i),
                    new StringContentProvider("text/csv", f, Charset.defaultCharset()), null);
            i++;
        }
        multiPart.close();
        return multiPart;
    }
}
