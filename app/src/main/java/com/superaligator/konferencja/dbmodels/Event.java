package com.superaligator.konferencja.dbmodels;

import android.database.Cursor;
import android.util.Log;

import com.activeandroid.Cache;
import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.superaligator.konferencja.interfaces.SynchoEventsListener;
import com.superaligator.konferencja.BuildConfig;
import com.superaligator.konferencja.definitions.EventType;
import com.superaligator.konferencja.models.EventsResponse;
import com.superaligator.konferencja.utils.OtherUtils;

import java.util.ArrayList;
import java.util.List;

@Table(name = "Events")
public class Event extends Model {
    @Column(name = "eventId")
    public String eventId;
    @Column(name = "title")
    public String title;
    @Column(name = "type")
    public String description;
    @Column(name = "description")
    public EventType type;
    /**
     * informacja czy gościu jest zapisany
     */
    @Column(name = "access")
    public int access = 0;
    /**
     * qr code do zeskanowania
     */
    @Column(name = "accessCode")
    public String accessCode;

    public static void synchroEvents(EventsResponse eventsResponse, SynchoEventsListener listener) {
        List<String> exists = new ArrayList<>();
        for (EventsResponse.RawEvent rawEvent : eventsResponse.events) {
            Event ev = findByEventId(rawEvent.eventId);
            if (ev == null)
                newEvent(rawEvent);
            exists.add(rawEvent.eventId);
        }
        if (exists.size() > 0) {
            String[] ex = exists.toArray(new String[0]);
            String param = OtherUtils.prepareNotIn(exists);
            new Delete().from(Event.class).where("eventId not in " + param, ex).execute();
        } else {
            new Delete().from(Event.class).execute();
        }

        if (BuildConfig.DEBUG) {
            List<Event> list = getEvents();
            for (Event el : list) {
                Log.w("x", "Jest eventId:" + el.eventId);
            }
        }

        if (listener != null)
            listener.OnSynchroEnd();
    }

    public static Event newEvent(EventsResponse.RawEvent rawEvent) {
        Event item = new Event();
        item.eventId = rawEvent.eventId;
        item.title = rawEvent.title;
        item.description = rawEvent.description;
        item.accessCode = rawEvent.accessCode;
        item.type = OtherUtils.eventTypeFromString(rawEvent.type);
        item.save();
        return item;
    }

    public static Event findByEventId(String eventId) {
        return new Select()
                .from(Event.class)
                .where("eventId = ?", eventId)
                .executeSingle();

    }

    public static List<Event> getEvenstByType(EventType type) {
        return new Select()
                .from(Event.class)
                .where("type LIKE ?", type.name())
                .execute();
    }

    public static List<Event> getEvents() {
        return new Select()
                .from(Event.class)
                .execute();
    }

    public static Cursor fetchResultCursor() {
        String tableName = Cache.getTableInfo(Event.class).getTableName();
        String resultRecords = new Select(tableName + ".*, " + tableName + ".Id as _id").from(Event.class).toSql();
        Cursor resultCursor = Cache.openDatabase().rawQuery(resultRecords, null);
        return resultCursor;
    }
}
