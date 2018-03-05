package com.superaligator.konferencja.models;


import android.view.View;
import android.widget.TextView;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.util.List;

@Table(name = "FormAnswer")
public class FormAnswer extends Model {
    @Column(name = "answer")
    public String answer;
    @Column(name = "answerId")
    public int answerId;

    //relacja do question
    @Column(name = "questionId")
    String questionId;

    //public TextView view;

    public static List<FormAnswer> getAnsewrsByQuestionId(String questionId) {
        return new Select()
                .from(FormAnswer.class)
                .where("questionId = ?", questionId)
                .execute();
    }
}
