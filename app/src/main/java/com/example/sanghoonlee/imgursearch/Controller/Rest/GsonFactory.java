package com.example.sanghoonlee.imgursearch.Controller.Rest;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.bind.DateTypeAdapter;

import java.util.Date;

/**
 * Created by sanghoonlee on 2015-12-10.
 *
 * build different GSON builder for different api servers
 * by default uses simple gson builder
 */
public final class GsonFactory {

    public GsonFactory() {
        //empty constructor
    }

    public static Gson createGson(int api_type) {
        GsonBuilder gsonBuilder;
        switch(api_type) {
            case RestConfig.IMGUR_API:
            default:
                gsonBuilder = createSimpleGsonBuilder();
                break;
        }
        return gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .registerTypeAdapter(Date.class, new DateTypeAdapter())
                .create();
    }

    private static GsonBuilder createSimpleGsonBuilder() {
        return new GsonBuilder().registerTypeAdapterFactory(new DataTypeAdapterFactory());
    }
}