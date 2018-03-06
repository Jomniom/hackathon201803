package com.superaligator.konferencja.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.superaligator.konferencja.R;
import com.superaligator.konferencja.dbmodels.Event;
import com.superaligator.konferencja.models.Form;
import com.superaligator.konferencja.models.FormAnswer;
import com.superaligator.konferencja.models.FormAnswersX;
import com.superaligator.konferencja.models.FormQuestion;
import com.superaligator.konferencja.network.Comunicator;
import com.superaligator.konferencja.utils.OtherUtils;

import java.util.ArrayList;
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
    boolean finished = false;

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
            ((Button) findViewById(R.id.button9)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendForm();
                }
            });
            getForms();
        }
    }


    private void setupView() {
        title.setText(form.title);


        LinearLayout mainView = (LinearLayout) findViewById(R.id.llForm);
        LayoutInflater inflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);





//        List<FormQuestion> questions = form.getQuestions();
//        for (ListIterator<FormQuestion> iter = questions.listIterator(); iter.hasNext(); ) {
//            final FormQuestion question = iter.next();
//            LinearLayout v = (LinearLayout) inflater.inflate(R.layout.question, null);
//            ((TextView) v.findViewById(R.id.textViewQuestion)).setText(question.question);

//            final List<FormAnswer> answers = question.formAnswers;
//            final List<TextView> aaa = new ArrayList<TextView>();
//            for (ListIterator<FormAnswer> answersI = answers.listIterator(); answersI.hasNext(); ) {
//                final FormAnswer ans = answersI.next();
//                LinearLayout q = (LinearLayout) inflater.inflate(R.layout.q, null);
//                TextView t = q.findViewById(R.id.textVieAnswer);
//                t.setText(ans.answer);
//                aaa.add(t);
//                t.setBackgroundColor(Color.parseColor("#cdcdcd"));
//                t.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        if (finished) {
//                            return;
//                        }
//
//                        Log.w("x", "wybrano odp " + ans.answerId);
//                        for (TextView a : aaa) {
//                            a.setBackgroundColor(Color.parseColor("#cdcdcd"));
//                        }
//                        v.setBackgroundColor(Color.parseColor("#888888"));
//                        question.userAnswerId = ans.answerId;
//                        question.save();
//                        //selectQuestion(question.formQuestionId, ans.formQustionAnswerId);
//                    }
//                });
//                v.addView(q);
//            }
            //-----------------------------------------------------
            //List<FormQuestion> questions = form.formQuestions
            for (final FormQuestion question : form.formQuestions) {
                //final FormQuestion question = iter.next();
                LinearLayout v = (LinearLayout) inflater.inflate(R.layout.question, null);
                ((TextView) v.findViewById(R.id.textViewQuestion)).setText(question.question);

                final List<FormAnswer> answers = question.formAnswers;
                final List<TextView> aaa = new ArrayList<TextView>();
                for (ListIterator<FormAnswer> answersI = answers.listIterator(); answersI.hasNext(); ) {
                    final FormAnswer ans = answersI.next();
                    LinearLayout q = (LinearLayout) inflater.inflate(R.layout.q, null);
                    TextView t = q.findViewById(R.id.textVieAnswer);
                    t.setText(ans.answer);
                    aaa.add(t);
                    t.setBackgroundColor(Color.parseColor("#cdcdcd"));
                    t.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (finished) {
                                return;
                            }

                            Log.w("x", "wybrano odp " + ans.answerId);
                            for (TextView a : aaa) {
                                a.setBackgroundColor(Color.parseColor("#cdcdcd"));
                            }
                            v.setBackgroundColor(Color.parseColor("#888888"));
                            question.userAnswerId = ans.answerId;
                            //question.save();
                            //selectQuestion(question.formQuestionId, ans.formQustionAnswerId);
                        }
                    });
                    v.addView(q);
                }
            //-----------------------------------------------------





            mainView.addView(v);
        }
    }

    private void sendForm() {
        if (finished) {
            return;
        }
        List<FormQuestion> questions = form.getQuestions();
        List<FormAnswersX> bbb = new ArrayList<>();
        for (FormQuestion q : questions) {
            if (q.userAnswerId > 0) {
                Log.w("x", "quest:" + q.questionId + ", answer:" + q.userAnswerId);
                FormAnswersX aaa = new FormAnswersX();
                aaa.formQustionAnswerId = q.userAnswerId;
                aaa.formQuestionId = OtherUtils.strToInt(q.questionId, -1);
                bbb.add(aaa);
            }

        }
        if (bbb.size() == 0) {
            return;
        }

        showLoading();
        Call<Void> saveCall = Comunicator.getInstance().getApiService().saveForm(bbb);
        saveCall.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                FormActivity.this.hideLoading();
                if (response.isSuccessful() == false) {
                    saveSuccess();
                    return;
                }
                saveSuccess();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                FormActivity.this.hideLoading();
                saveSuccess();
            }
        });
    }

    private void saveSuccess() {
        Toast.makeText(this, "Zapisano ankietę brawo!", Toast.LENGTH_LONG).show();
        finished = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (formsCall != null) {
            formsCall.cancel();
            formsCall = null;
        }
    }


    private String jsonHard = "{\"eventId\":21,\"formId\":2421,\"title\":\"Ankieta Powitalna\",\"formQuestions\":[{\"question\":\"Czy lubisz kury?\",\"formQuestionId\":333,\"formAnswers\":[{\"answer\":\"tak\",\"formQustionAnswerId\":2031},{\"answer\":\"nie\",\"formQustionAnswerId\":1221}]},{\"question\":\"Ile uszu ma kura?\",\"formQuestionId\":444,\"formAnswers\":[{\"answer\":\"Ma duzo\",\"formQustionAnswerId\":431},{\"answer\":\"Ma ich wiele\",\"formQustionAnswerId\":5421},{\"answer\":\"Kura ma tylko jedno ucho\",\"formQustionAnswerId\":4321}]}]}";

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

                    //hard
                    Form ob = (new Gson()).fromJson(jsonHard, Form.class);
                    form = ob;
                    //form = form.synchroDb();
                    setupView();
                    return;
                }
                form = response.body();
                //form = form.synchroDb();
                setupView();
            }

            @Override
            public void onFailure(Call<Form> call, Throwable t) {
                FormActivity.this.hideLoading();

                //hard
                Form ob = (new Gson()).fromJson(jsonHard, Form.class);
                form = ob;
                //form = form.synchroDb();
                setupView();
                return;
            }
        });
    }
}
