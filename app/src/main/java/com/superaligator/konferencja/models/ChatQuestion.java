package com.superaligator.konferencja.models;

import java.util.Date;

/**
 * Created by CI_KMANKA on 2018-03-05.
 */

public class ChatQuestion {

    private String question;
    private Long eventId;
    private Long participantId;
    private Date date;


    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public Long getParticipantId() {
        return participantId;
    }

    public void setParticipantId(Long participantId) {
        this.participantId = participantId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
