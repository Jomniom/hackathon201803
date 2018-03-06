package com.superaligator.konferencja.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.util.List;
import java.util.ListIterator;

@Table(name = "Form")
public class Form extends Model {
    @Column(name = "eventId")
    String eventId;
    @Column(name = "formId")
    public String formId;
    @Column(name = "title")
    public String title;
    @Column(name = "completed")
    boolean completed = false;

    public List<FormQuestion> getQuestions(){
        return FormQuestion.getFormQuestionsByFormId(formId);
    }

    //gson - parse respnse
    public  List<FormQuestion> formQuestions;

    public Form synchroDb() {
        Form f = new Select()
                .from(Form.class)
                .where("formId=?", formId)
                .executeSingle();
        if (f == null) {
            save();

            //zapisz pytania
            for (ListIterator<FormQuestion> iter = formQuestions.listIterator(); iter.hasNext(); ) {
                FormQuestion formQuestion = iter.next();
                formQuestion.formId = formId;
                formQuestion.save();

                //mozliwe odpowiedzi
                for (ListIterator<FormAnswer> answerIter = formQuestion.formAnswers.listIterator(); answerIter.hasNext(); ) {
                    FormAnswer formAnswer = answerIter.next();
                    formAnswer.questionId = formQuestion.questionId;
                    formAnswer.save();
                }
            }

            return this;
        }
        return f;
    }
}

