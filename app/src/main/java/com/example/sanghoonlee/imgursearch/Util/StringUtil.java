package com.example.sanghoonlee.imgursearch.Util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by sanghoonlee on 2015-12-11.
 */
public class StringUtil {

    public static String makeUrlEncoded(String input) throws UnsupportedEncodingException {
        return URLEncoder.encode(input, "UTF-8");
    }

}
