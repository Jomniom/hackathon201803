package com.superaligator.konferencja.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.lzyzsd.randomcolor.RandomColor;
import com.google.gson.Gson;
import com.superaligator.konferencja.R;
import com.superaligator.konferencja.dbmodels.Event;
import com.superaligator.konferencja.dbmodels.Quiz;
import com.superaligator.konferencja.dbmodels.QuizQuestion;
import com.superaligator.konferencja.dbmodels.QuizQuestionAnswer;
import com.superaligator.konferencja.interfaces.onMessageWS;
import com.superaligator.konferencja.managers.QuizWebSocketListener;
import com.superaligator.konferencja.network.Comunicator;
import com.superaligator.konferencja.utils.OtherUtils;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import okio.ByteString;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QuizActivity extends BaseUserActivity implements onMessageWS {

    public static final String EXTRA_EVENT_ID = "eventId";
    Event event = null;
    Call<Quiz> quizCall;
    TextView name, description, textView11;
    Quiz quiz;
    LinearLayout llNoQuizzes, llQuiz, llquestionContainer;
    private OkHttpClient client;
    WebSocket webSocket;
    Request request;
    QuizWebSocketListener socketListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);


        Intent i = getIntent();
        String eventId = i.getStringExtra(EXTRA_EVENT_ID);
        if (eventId != null) {
            event = Event.findByEventId(eventId);
            name = (TextView) findViewById(R.id.textView9);
            description = (TextView) findViewById(R.id.textView10);
            textView11 = (TextView) findViewById(R.id.textView11);
            llNoQuizzes = (LinearLayout) findViewById(R.id.llNoQuizzes);
            llNoQuizzes.setVisibility(View.GONE);
            llquestionContainer = (LinearLayout) findViewById(R.id.llquestionContainer);
            llQuiz = (LinearLayout) findViewById(R.id.llQuiz);
            llQuiz.setVisibility(View.GONE);
            client = new OkHttpClient();
        }

        request = new Request.Builder().url("ws://echo.websocket.org").build();
        socketListener = new QuizWebSocketListener(this);
//        byte[] decodedString = Base64.decode(strBase64, Base64.DEFAULT);
//        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
//        image.setImageBitmap(decodedByte);
        ///ImageView iv = new ImageView(this)
    }

    @Override
    protected void onResume() {
        super.onResume();
        getQuiz();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (quizCall != null) {
            quizCall.cancel();
            quizCall = null;
        }
        if (saveQuestionCall != null) {
            saveQuestionCall.cancel();
            saveQuestionCall = null;
        }
        socketClose();
    }

    private void getQuiz() {
        if (quizCall != null) {
            quizCall.cancel();
            quizCall = null;
        }
        showLoading();
        quizCall = Comunicator.getInstance().getApiService().getQuiz(event.eventId);
        quizCall.enqueue(new Callback<Quiz>() {
            @Override
            public void onResponse(Call<Quiz> call, Response<Quiz> response) {
                QuizActivity.this.hideLoading();
                if (response.isSuccessful() == false) {
                    QuizActivity.this.noQuiz();
                    return;
                }
                quiz = response.body();
                //quiz.save();
                //QuizActivity.this.quiz = quiz.synchroDb();
                QuizActivity.this.setupView();
            }

            @Override
            public void onFailure(Call<Quiz> call, Throwable t) {
                QuizActivity.this.hideLoading();
                QuizActivity.this.noQuiz();
            }
        });
    }

    private void noQuiz() {
        llNoQuizzes.setVisibility(View.VISIBLE);
        llQuiz.setVisibility(View.GONE);
    }

    private void socketOpen() {

        webSocket = client.newWebSocket(request, socketListener);
    }

    private void socketClose() {
        //client.dispatcher().executorService().shutdown();
        webSocket.cancel();
    }

    private void setupView() {
        name.setText(quiz.name);
        description.setText(quiz.description);
        llQuiz.setVisibility(View.VISIBLE);
        if (quiz.closed) {
            name.setAlpha(0.2f);
            description.setAlpha(0.2f);
            textView11.setVisibility(View.VISIBLE);
            return;
        }
        showQuestion(1);//test
        socketOpen();
    }


    @Override
    public void onMessage(WebSocket webSocket, String text) {
        if (text == null) {
            return;
        }
        //pokaz pytanie n..
        int questionId = OtherUtils.strToInt(text, -1);
        questionId = 1;//-test
        //showQuestion(questionId);
    }

    private void showQuestion(int questionId) {
        //wyczyść pojemnik na pytanie
        int c = llquestionContainer.getChildCount();
        Log.w("w", "" + c);
        if (llquestionContainer.getChildCount() > 0) {
            llquestionContainer.removeAllViews();
            //llquestionContainer.chil
        }
        //wyszukaj pytanie
        for (QuizQuestion quizQuestion : quiz.questions) {
            if (quizQuestion.id == questionId) {
                View v = buildQuestionView(quizQuestion);
                llquestionContainer.addView(v);
                //llquestionContainer.invalidate();
                // llquestionContainer.setVisibility(View.GONE);
                // llquestionContainer.setVisibility(View.VISIBLE);
                //llquestionContainer.removeAllViews();
                llquestionContainer.refreshDrawableState();

                llquestionContainer.postInvalidate();
                llquestionContainer.requestLayout();

                return;
            }
        }
    }

    RandomColor randomColor = new RandomColor();

    private View buildQuestionView(final QuizQuestion quizQuestion) {
        LayoutInflater inflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout v = (LinearLayout) inflater.inflate(R.layout.quiz_question, null);
        ((TextView) v.findViewById(R.id.textQuizQuestion)).setText(quizQuestion.text);
        for (final QuizQuestionAnswer answer : quizQuestion.answers) {
            View q = (View) inflater.inflate(R.layout.quiz_answer, null);
            TextView t = q.findViewById(R.id.textQuizAnswer);
            t.setText(answer.text);
            int color = randomColor.randomColor();
            t.setBackgroundColor(color);
            //t.setBackgroundColor(Color.parseColor("#33ffaa"))
            q.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.w("x", "wybrano odp " + answer.id);
                    //showQuestion(2);
                    saveQuizQuestion(quizQuestion, answer);
                }
            });
            v.addView(q);
        }
        return v;
    }

    Call<Void> saveQuestionCall;

    private void saveQuizQuestion(QuizQuestion quizQuestion, QuizQuestionAnswer answer) {
        if (saveQuestionCall != null) {
            saveQuestionCall.cancel();
            saveQuestionCall = null;
        }
        //showQuestion(2);
        showLoading();
        saveQuestionCall = Comunicator.getInstance().getApiService().saveQuizQuestion(event.eventId, quizQuestion.quizId, answer.quizQuestionId, answer.id);
        saveQuestionCall.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                QuizActivity.this.hideLoading();
                if (response.isSuccessful() == false) {
                    saveQuizQuestionFail();
                    return;
                }

                QuizActivity.this.saveQuizQuestionSuccess();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                QuizActivity.this.hideLoading();
                saveQuizQuestionFail();
            }
        });
    }

    private void saveQuizQuestionSuccess() {
        //todo pokaz wkranu czekania na kolejne pytanie
        //showQuestion(2);
        LayoutInflater inflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout waitView = (LinearLayout) inflater.inflate(R.layout.wait_for_question, null);
        if (llquestionContainer.getChildCount() > 0) {
            llquestionContainer.removeAllViews();
            //llquestionContainer.chil
        }
        llquestionContainer.addView(waitView);
    }

    private void saveQuizQuestionFail() {
        Toast.makeText(this, "Błąd zapisywania odpowiedzi", Toast.LENGTH_LONG);
    }

    @Override
    public void onMessage(WebSocket webSocket, ByteString bytes) {

    }

    @Override
    public void onOpen(WebSocket webSocket, okhttp3.Response response) {
        //webSocket.send(
        for (QuizQuestion quizQuestion : quiz.questions) {
            if (quizQuestion.answerId > 0) {
                //odpowiedz zostala udzielona
                continue;
            }
            //(new Gson).
            webSocket.send("{quizId:" + quiz.eventId + "}");
            return;
        }
    }


}
