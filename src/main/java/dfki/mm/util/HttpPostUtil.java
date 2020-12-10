package dfki.mm.util;

import dfki.mm.request.MyHttpRequest;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HttpPostUtil {


    public static List<String> readStrings(MyHttpRequest request, String name) {
        return readStrings(request, name, null, true);
    }

    public static String readString(MyHttpRequest request, String name) {
        return readString(request, name, null, true);
    }

    public static List<Number> readNumbers(MyHttpRequest request, String name) {
        return readNumbers(request, name, null, true);
    }

    public static Number readNumber(MyHttpRequest request, String name) {
        return readNumber(request, name, null, true);
    }

    public static List<String> readStrings(MyHttpRequest request, String name, List<String> defaultValue, boolean throwException) {
        String[] ret = request.baseRequest.getParameterValues(name);
        if (ret == null) {
            if (throwException) {
                throw new IllegalArgumentException("Missing " + name);
            } else {
                return defaultValue;
            }
        } else {
            return Arrays.asList(ret);
        }
    }

    public static String readString(MyHttpRequest request, String name, String defaultValue, boolean throwException) {
        String ret = request.baseRequest.getParameter(name);
        if (ret == null) {
            if (throwException) {
                throw new IllegalArgumentException("Missing " + name);
            } else {
                return defaultValue;
            }
        } else {
            return ret;
        }
    }

    public static List<Number> readNumbers(MyHttpRequest request, String name, List<Number> defaultValue, boolean throwException) {
        List<String> vals = readStrings(request, name, null, throwException);
        if (vals == null) {
            return defaultValue;
        }
        List<Number> ret = new ArrayList<>();
        for (String val : vals) {
            for (String val2 : val.split(",")) {
                ret.add(stringToNumber(name, val2));
            }
        }
        return ret;
    }

    public static Number readNumber(MyHttpRequest request, String name, Number defaultValue, boolean throwException) {
        String val = readString(request, name, null, throwException);
        //request.baseRequest.getParameter(name);
        if (val == null) {
            return defaultValue;
        }
        return stringToNumber(name, val);
    }

    /**
     * String -> Number (Long, Double etc.)
     * @param name Parameter name (for an exception)
     * @param val Value to parse
     * @return Number
     */
    public static Number stringToNumber(String name, String val) {
        try {
            Number n = NumberFormat.getInstance().parse(val);
            return n;
        } catch (ParseException e) {
            throw new IllegalArgumentException(name + " cannot be parsed: " + val);
        }
    }
}
