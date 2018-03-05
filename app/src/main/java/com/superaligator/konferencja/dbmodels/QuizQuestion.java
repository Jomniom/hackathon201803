package com.superaligator.konferencja.dbmodels;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.util.List;

@Table(name = "QuizQuestion")
public class QuizQuestion extends Model {
    @Column(name = "questionId")
    public Long id;
    @Column(name = "quizId")
    public Long quizId;
    @Column(name = "text")
    public String text;

    //odpowiedź usera 0-jeszce nei udzielona
    @Column(name = "answer")
    public long answerId = 0;

    //gson
    public List<QuizQuestionAnswer> answers;

}
