package com.example.sanghoonlee.imgursearch.Controller.Rest;

import com.squareup.okhttp.OkHttpClient;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

/**
 * Created by sanghoonlee on 2015-12-10.
 * ServiceGenerator creates the rest services for specified endpoint with the specified api server
 *
 * Side: OKHttpClient allows to add interceptors
 */
public class ServiceGenerator {

    public static <S> S createService(Class<S> serviceClass, int api_type) {

        OkHttpClient client = new OkHttpClient();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(RestConfig.getBaseUrl(api_type))
                .addConverterFactory(GsonConverterFactory.create(GsonFactory.createGson(api_type)))
                .client(client)
                .build();

        return retrofit.create(serviceClass);
    }
}
