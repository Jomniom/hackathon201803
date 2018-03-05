package com.superaligator.konferencja.activity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.superaligator.konferencja.R;
import com.superaligator.konferencja.adapters.EventsAdapter;
import com.superaligator.konferencja.dbmodels.Event;
import com.superaligator.konferencja.interfaces.SynchoEventsListener;
import com.superaligator.konferencja.models.ChatQuestion;
import com.superaligator.konferencja.models.EventsResponse;
import com.superaligator.konferencja.network.Comunicator;

import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/***
 * - lista wydarzeń: historyczne, oferowane, moje
 *
 * - pobranie danych wydarzenia: informacje, materiały, konkursy,
 *  zadawanie pytań, głosowania uchwał
 * - generowanie qrkoda
 */

public class DashboardActivity extends BaseUserActivity implements AdapterView.OnItemClickListener {

    Call<EventsResponse> eventsCall;
    ListView listView;
    EventsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        Button btnEnroll = findViewById(R.id.button);
        btnEnroll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DashboardActivity.this, EnrollActivity.class);
                startActivity(intent);
            }
        });

        Cursor cursor = Event.fetchResultCursor();
        adapter = new EventsAdapter(this, cursor);

        listView = findViewById(R.id.listView);
        listView.setClickable(true);
        listView.setOnItemClickListener(this);
        listView.setAdapter(adapter);

        updateEvents();
        chatTest();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (eventsCall != null) {
            eventsCall.cancel();
            eventsCall = null;
        }
    }

    private void chatTest() {
        if (eventsCall != null) {
            eventsCall.cancel();
            eventsCall = null;
        }
        ChatQuestion q = new ChatQuestion();
        q.setDate(new Date());
        q.setEventId(1L);
        q.setParticipantId(1L);
        q.setQuestion("o co chodzi?");

        Comunicator.getInstance().getApiService().sendQuestion(q);

    }


    private void updateEvents() {
        if (eventsCall != null) {
            eventsCall.cancel();
            eventsCall = null;
        }
        showLoading();
        eventsCall = Comunicator.getInstance().getApiService().events();
        eventsCall.enqueue(new Callback<EventsResponse>() {
            @Override
            public void onResponse(Call<EventsResponse> call, Response<EventsResponse> response) {
                DashboardActivity.this.hideLoading();
                if (response.isSuccessful() == false) {
                    return;
                }
                Log.w("x", "liczba pobranych wydarzeń: " + response.body().events.size());
                Event.synchroEvents(response.body(), new SynchoEventsListener() {
                    @Override
                    public void OnSynchroEnd() {
                        DashboardActivity.this.refreshList();
                    }
                });
            }

            @Override
            public void onFailure(Call<EventsResponse> call, Throwable t) {
                DashboardActivity.this.hideLoading();
                if (call.isCanceled()) {
                    Log.e("x", "request was cancelled");
                } else {
                    Log.w("x", "błąd pobierania wydarzeń");
                    t.printStackTrace();
                }
            }
        });
    }

    void refreshList() {
        if (adapter == null)
            return;
        Cursor cursor = Event.fetchResultCursor();
        adapter.changeDate(cursor);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        String eventId = ((EventsAdapter.ViewHolder) view.getTag()).eventId;
        Intent intent = new Intent(this, EventActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(EventActivity.EXTRA_EVENT_ID, eventId);
        startActivity(intent);
    }
}
