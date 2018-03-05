package com.superaligator.konferencja.dbmodels;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table(name = "QuizQuestionAnswer")
public class QuizQuestionAnswer extends Model {
    @Column(name = "answerId")
    public Long id;
    @Column(name = "quizQuestionId")
    public Long quizQuestionId;
    @Column(name = "text")
    public String text;
    @Column(name = "correct")
    public boolean correct;

}