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

import com.superaligator.konferencja.R;
import com.superaligator.konferencja.dbmodels.Event;
import com.superaligator.konferencja.models.Form;
import com.superaligator.konferencja.models.FormAnswer;
import com.superaligator.konferencja.models.FormQuestion;
import com.superaligator.konferencja.network.Comunicator;

import java.util.List;
import java.util.ListIterator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FormActivity extends BaseUserActivity {

    public static final String EXTRA_EVENT_ID = "eventId";
    Event event = null;
    Call<Form> formsCall;
    Form form;
    TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);

        Intent i = getIntent();
        String eventId = i.getStringExtra(EXTRA_EVENT_ID);
        if (eventId != null) {
            event = Event.findByEventId(eventId);
            Log.w("x", "" + event.title);

            title = (TextView) findViewById(R.id.textView8);

            getForms();
        }
    }

    private void setupView() {
        title.setText(form.title);

        LinearLayout mainView = (LinearLayout) findViewById(R.id.llForm);

        List<FormQuestion> questions = form.getQuestions();
        for (ListIterator<FormQuestion> iter = questions.listIterator(); iter.hasNext(); ) {
            FormQuestion question = iter.next();
            LayoutInflater inflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            LinearLayout v = (LinearLayout) inflater.inflate(R.layout.question, null);
            ((TextView) v.findViewById(R.id.textViewQuestion)).setText(question.question);

            List<FormAnswer> answers = question.getFormAnswers();
            for (ListIterator<FormAnswer> answersI = answers.listIterator(); answersI.hasNext(); ) {
                final FormAnswer ans = answersI.next();
                LayoutInflater inx = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                LinearLayout q = (LinearLayout) inx.inflate(R.layout.q, null);
                TextView t = q.findViewById(R.id.textVieAnswer);
                t.setText(ans.answer);
                t.setBackgroundColor(Color.parseColor("#22aa11"));
                q.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.w("x", "wybrano odp " + ans.answerId);
                    }
                });
                v.addView(q);
            }
            mainView.addView(v);
        }


    }

    @Override
    protected void onPause() {
        super.onPause();
        if (formsCall != null) {
            formsCall.cancel();
            formsCall = null;
        }
    }

    private void getForms() {
        if (formsCall != null) {
            formsCall.cancel();
            formsCall = null;
        }
        showLoading();
        formsCall = Comunicator.getInstance().getApiService().getForms(event.eventId);
        formsCall.enqueue(new Callback<Form>() {
            @Override
            public void onResponse(Call<Form> call, Response<Form> response) {
                FormActivity.this.hideLoading();
                if (response.isSuccessful() == false) {
                    return;
                }
                form = response.body();
                form = form.synchroDb();
                setupView();
            }

            @Override
            public void onFailure(Call<Form> call, Throwable t) {
                FormActivity.this.hideLoading();
            }
        });
    }
}
