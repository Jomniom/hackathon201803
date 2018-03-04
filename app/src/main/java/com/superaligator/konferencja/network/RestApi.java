package com.superaligator.konferencja.network;

import com.superaligator.konferencja.models.EnrollResponse;
import com.superaligator.konferencja.models.EventsResponse;
import com.superaligator.konferencja.models.Form;
import com.superaligator.konferencja.models.LoginResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface RestApi {
    @POST("login.php")
    Call<LoginResponse> login();

    @GET("events.php")
    Call<EventsResponse> events();

    @GET("register_event.php")
    Call<EnrollResponse> registerEvent();

    @FormUrlEncoded
    @POST("chat.php")
    Call<Void> sendQuestion(@Field("message") String message, @Field("eventId") String eventId);

    @GET("forms.php")
    Call<Form> getForms(@Header("eventId") String eventId);

//    @GET("forms.php/{eventId}")
//    Call<Form> getForms(@Path("eventId") String eventId);
}
