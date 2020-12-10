package dfki.mm.request;

import dfki.mm.util.HttpPostUtil;
import org.eclipse.jetty.server.Request;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

public class MyHttpRequest {
    public Request baseRequest;
    HttpServletRequest request;
    public HttpServletResponse response;
    public List<String> errors = new ArrayList<>();

    public MyHttpRequest(Request baseRequest, HttpServletRequest request, HttpServletResponse response) {
        this.baseRequest = baseRequest;
        this.request = request;
        this.response = response;
    }

    public interface RequestReader {
        List<String> readStrings(MyHttpRequest request, String name);
        String readString(MyHttpRequest request, String name);
        Number readNumber(MyHttpRequest request, String name);
        List<Number> readNumbers(MyHttpRequest request, String name);

        boolean isNull(MyHttpRequest myHttpRequest, String name);
//        byte[][] readData(MyHttpRequest request, String name);
    }

    static final RequestReader postRequestReader = new RequestReader() {
        @Override
        public List<String> readStrings(MyHttpRequest request, String name) {
            return HttpPostUtil.readStrings(request, name);
        }

        @Override
        public String readString(MyHttpRequest request, String name) {
            return HttpPostUtil.readString(request, name);
        }

        @Override
        public List<Number> readNumbers(MyHttpRequest request, String name) {
            return HttpPostUtil.readNumbers(request, name);
        }

        @Override
        public Number readNumber(MyHttpRequest request, String name) {
            return HttpPostUtil.readNumber(request, name);
        }

        @Override
        public boolean isNull(MyHttpRequest myHttpRequest, String name) {
            return "null".equals(readString(myHttpRequest, name));
        }

        //        @Override
//        public byte[][] readData(MyHttpRequest request, String name) {
//            return new byte[0][];
//        }
    };

    public static final RequestReader getRequestReader = postRequestReader;

}
