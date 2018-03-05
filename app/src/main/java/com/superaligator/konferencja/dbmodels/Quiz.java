package com.superaligator.konferencja.dbmodels;


import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.util.List;

@Table(name = "Quiz")
public class Quiz extends Model {
    @Column(name = "quidId")
    public Long id;
    @Column(name = "name")
    public String name;
    @Column(name = "description")
    public String description;
    @Column(name = "eventId")
    public Long eventId;
    @Column(name = "closed")
    public boolean closed = false;
    //gson
    public List<QuizQuestion> questions;


    public Quiz synchroDb() {
        return null;
    }
}
