package com.superaligator.konferencja.network;

import com.superaligator.konferencja.dbmodels.Quiz;
import com.superaligator.konferencja.models.ChatQuestionReq;
import com.superaligator.konferencja.models.EnrollResponse;
import com.superaligator.konferencja.models.EventsResponse;
import com.superaligator.konferencja.models.Form;
import com.superaligator.konferencja.models.LoginRequest;
import com.superaligator.konferencja.models.LoginResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface RestApi {

    //    @FormUrlEncoded
    @POST("api/authenticate")
    Call<LoginResponse> login(@Body LoginRequest request);

    @GET("api/events")
    Call<EventsResponse> events();

    @FormUrlEncoded
    @POST("api/confirmParticipant")
    Call<EnrollResponse> registerEvent(@Field("code") String code);

//    @FormUrlEncoded
    @POST("/api/chat-question")
    Call<Void> sendQuestion(@Body ChatQuestionReq chatQuestionReq);

    @GET("forms.php")
    Call<Form> getForms(@Header("eventId") String eventId);

    @GET("forms2.php")
    Call<Quiz> getQuiz(@Header("eventId") String eventId);

    @FormUrlEncoded
    @POST("forms2.php")
    Call<Void> saveQuizQuestion(@Field("eventId") String eventId, @Field("quizId") long quizId, @Field("questionId") long questionId, @Field("answerId") long answerId);

    //    @GET("forms.php/{eventId}")
//    Call<Form> getForms(@Path("eventId") String eventId);
}
