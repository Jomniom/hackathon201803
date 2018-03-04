package com.superaligator.konferencja.dbmodels;

import android.database.Cursor;

import com.activeandroid.Cache;
import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.util.Date;

@Table(name = "ChatQuestions")
public class ChatQuestion extends Model {
    @Column(name = "eventId")
    public String eventId;
    @Column(name = "question")
    public String question;
    @Column(name = "dateQuestion")
    public Date dateQuestion;

    public static Cursor fetchResultCursor(String eventId) {
        String tableName = Cache.getTableInfo(ChatQuestion.class).getTableName();
        String resultRecords = new Select(tableName + ".*, " + tableName + ".Id as _id")
                .from(ChatQuestion.class)
                .where("eventId = ?")
                .toSql();

        Cursor resultCursor = Cache.openDatabase().rawQuery(resultRecords, new String[]{eventId});
        return resultCursor;
    }
}
