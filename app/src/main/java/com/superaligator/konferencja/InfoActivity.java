package com.superaligator.konferencja;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.superaligator.konferencja.activity.BaseUserActivity;
import com.superaligator.konferencja.dbmodels.Event;

public class InfoActivity extends BaseUserActivity {

    public static final String EXTRA_EVENT_ID = "eventId";
    Event event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        Intent i = getIntent();
        String eventId = i.getStringExtra(EXTRA_EVENT_ID);
        if (eventId != null) {
            event = Event.findByEventId(eventId);
            setupView();
        }
    }

    private void setupView() {
        ((TextView)findViewById(R.id.textView5)).setText(event.title);
        ((TextView)findViewById(R.id.textView6)).setText(event.description);
    }
}
