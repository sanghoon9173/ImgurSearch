package com.example.sanghoonlee.imgursearch.Controller.Rest;

/**
 * Created by sanghoonlee on 2015-12-10.
 */
public class RestConfig {
    public static final int IMGUR_API   = 0;

    public static final String IMGUR_BASE_URL   = "https://api.imgur.com";
    public static final String IMGUR_CLIENT_ID   = "79f3c001877edb4";
    public static final String RETURN_TYPE      = "json";

    public static String getBaseUrl(int api_type) {
        switch(api_type) {
            case RestConfig.IMGUR_API:
            default:
                return IMGUR_BASE_URL;
        }
    }

}
