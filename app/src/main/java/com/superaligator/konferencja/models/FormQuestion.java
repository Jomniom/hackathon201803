package com.superaligator.konferencja.models;


import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.superaligator.konferencja.dbmodels.Event;

import java.util.List;

@Table(name = "FormQuestions")
public class FormQuestion extends Model {
    @Column(name = "questionId", unique = true)
    public String questionId;
    //relacja do form
    @Column(name = "formId")
    public String formId;
    @Column(name = "question")
    public String question;
    //odpowiedz uzytkownika
    @Column(name = "userAnswerId")
    public int userAnswerId;

    //gson
    List<FormAnswer> formAnswers;

    public static List<FormQuestion> getFormQuestionsByFormId(String formId) {
        return new Select()
                .from(FormQuestion.class)
                .where("formId = ?", formId)
                .execute();
    }

    public List<FormAnswer> getFormAnswers() {
        return FormAnswer.getAnsewrsByQuestionId(questionId);
    }
}
