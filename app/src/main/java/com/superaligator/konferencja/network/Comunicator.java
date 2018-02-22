package com.superaligator.konferencja.network;

import com.superaligator.konferencja.Config;
import com.superaligator.konferencja.managers.UserManager;
import com.superaligator.konferencja.utils.OtherUtils;
import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Comunicator {
    public static final String KEY_HEADER_X_APP = "x-y";
    public static String APP_KEY = "";
    public static final int[] APP_ASCCII_KEY = {34,78,45,54,32,32};
    private static Comunicator ourInstance = new Comunicator();
    private Retrofit retrofit;
    private RestApi service;

    public static Comunicator getInstance() {
        APP_KEY = OtherUtils.getGluePass(APP_ASCCII_KEY);
        return ourInstance;
    }

    private String prepareUserAgentName() {
        String orgUserAgent = System.getProperty("http.agent");
        String fakeVersion = "fa." + OtherUtils.randomInt(10, 900) + "." + OtherUtils.randomInt(100, 900);
        return orgUserAgent + " " + fakeVersion;
    }

    private Comunicator() {
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();

        /**
         * Headers interceptor
         */
        clientBuilder.addInterceptor(new HeaderInterceptor());

        /**
         * Logging intercptor
         */
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        clientBuilder.addInterceptor(loggingInterceptor);

        retrofit = new Retrofit.Builder()
                .baseUrl(Config.BASE_URL)
                .client(clientBuilder.build())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

    }

    public RestApi getApiService() {
        service = retrofit.create(RestApi.class);
        return service;
    }

    class HeaderInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {

            /**
             * User agent
             */
            String userAgent = prepareUserAgentName();
            String xApp = OtherUtils.getXApp(userAgent);

            Request originalRequest = chain.request();
            Request.Builder newBuilder = originalRequest.newBuilder();
            newBuilder.header("User-Agent", userAgent);
            newBuilder.addHeader(KEY_HEADER_X_APP, xApp);
            if(UserManager.getInstance().isLoggedIn()){
                newBuilder.addHeader("token", UserManager.getInstance().getApiKey());
                newBuilder.addHeader("user_id", UserManager.getInstance().getUserId());
            }

            newBuilder.build();
            Request newRequest = newBuilder.build();

            return chain.proceed(newRequest);
        }


    }
}
