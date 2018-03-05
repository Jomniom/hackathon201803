package com.superaligator.konferencja.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.superaligator.konferencja.InfoActivity;
import com.superaligator.konferencja.R;
import com.superaligator.konferencja.adapters.EventsAdapter;
import com.superaligator.konferencja.dbmodels.Event;

public class EventActivity extends BaseUserActivity {
    public static final String EXTRA_EVENT_ID = "eventId";
    Event event = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        Intent i = getIntent();
        String eventId = i.getStringExtra(EXTRA_EVENT_ID);
        if (eventId != null) {
            event = Event.findByEventId(eventId);
            Log.w("x", "" + event.title);
            setupView();
        }
    }

    private void setupView() {
        ((TextView) findViewById(R.id.textViewEventTitle)).setText(event.title);
        ((TextView) findViewById(R.id.textViewEventDescription)).setText(event.description);
        ((Button) findViewById(R.id.button2)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCode();
            }
        });
        ((Button) findViewById(R.id.button4)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showChat();
            }
        });
        ((Button) findViewById(R.id.button8)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showInfo();
            }
        });
        ((Button) findViewById(R.id.button6)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showForms();
            }
        });
        ((Button) findViewById(R.id.button3)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showQuiz();
            }
        });
    }

    private void showQuiz() {
        Intent intent = new Intent(this, QuizActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(QuizActivity.EXTRA_EVENT_ID, event.eventId);
        startActivity(intent);
    }

    private void showForms() {
        Intent intent = new Intent(this, FormActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(FormActivity.EXTRA_EVENT_ID, event.eventId);
        startActivity(intent);
    }

    private void showInfo() {
        Intent intent = new Intent(this, InfoActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(InfoActivity.EXTRA_EVENT_ID, event.eventId);
        startActivity(intent);
    }

    private void showChat() {
        Intent intent = new Intent(this, ChatActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(ChatActivity.EXTRA_EVENT_ID, event.eventId);
        startActivity(intent);
    }

    private void showCode() {
        Intent intent = new Intent(this, QRCodeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(QRCodeActivity.EXTRA_CODE, event.accessCode);
        startActivity(intent);
    }
}