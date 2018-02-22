package com.superaligator.konferencja.network;

import com.superaligator.konferencja.dbmodels.Event;
import com.superaligator.konferencja.models.EnrollResponse;
import com.superaligator.konferencja.models.EventsResponse;
import com.superaligator.konferencja.models.LoginResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface RestApi {
    @POST("login.php")
    Call<LoginResponse> login();

    @GET("events.php")
    Call<EventsResponse> events();

    @GET("register_event.php")
    Call<EnrollResponse> registerEvent();
//    @FormUrlEncoded
//    @POST("?c=services&m=regapp")
//    Call<RegApp> register(@Field("appId") long appId, @Field("pushToken") String pushToken);
//
//    @FormUrlEncoded
//    @POST("?c=services&m=send")
//    Call<Void> sendMessage(@Field("appId") long appId, @Field("msg") String message);
}
