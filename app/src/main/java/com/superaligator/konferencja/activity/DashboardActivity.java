package com.superaligator.konferencja.activity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.google.gson.Gson;
import com.superaligator.konferencja.R;
import com.superaligator.konferencja.adapters.EventsAdapter;
import com.superaligator.konferencja.dbmodels.Event;
import com.superaligator.konferencja.interfaces.SynchoEventsListener;
import com.superaligator.konferencja.models.EventsResponse;
import com.superaligator.konferencja.network.Comunicator;

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

    }

    @Override
    protected void onResume() {
        super.onResume();
        updateEvents();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (eventsCall != null) {
            eventsCall.cancel();
            eventsCall = null;
        }
    }

    private String hardJson = "{\"events\":[{\"eventId\":2012,\"title\":\"Konferencja lotnicza\",\"type\":\"CONFERENCE\",\"description\":\"Spotkanie dla lotnikow\",\"accessCode\":\"394fd9\"},{\"eventId\":1203,\"title\":\"Gala Drwali 2018\",\"type\":\"CONFERENCE\",\"description\":\"Poczestunek dla drwali\",\"accessCode\":\"h8alw32\"},{\"eventId\":1653,\"title\":\"Nagrody Ornitologiczne 2018\",\"type\":\"CONFERENCE\",\"description\":\"Spotkanie wielbicieli ptakow\",\"accessCode\":\"bo0932s\"},{\"eventId\":16111,\"title\":\"Doroczne Zebranie Piekarzy\",\"type\":\"CONFERENCE\",\"description\":\"Spotkanie piekarzy i smakoszy pieczywa\",\"accessCode\":\"dn5m321\"}]}";

    private void updateEvents() {
        if (eventsCall != null) {
            eventsCall.cancel();
            eventsCall = null;
        }
        //showLoading();
        eventsCall = Comunicator.getInstance().getApiService().events();
        eventsCall.enqueue(new Callback<EventsResponse>() {
            @Override
            public void onResponse(Call<EventsResponse> call, Response<EventsResponse> response) {
                DashboardActivity.this.hideLoading();
                if (response.isSuccessful() == false) {

                    //hardkod wydarzen
                    EventsResponse ob = (new Gson()).fromJson( hardJson, EventsResponse.class);
                    Event.synchroEvents(ob, new SynchoEventsListener() {
                        @Override
                        public void OnSynchroEnd() {
                            DashboardActivity.this.refreshList();
                        }
                    });
                    return;
                }
                Log.w("x", "liczba pobranych wydarzeń: " + response.body().events.size());



//                Event.synchroEvents(response.body(), new SynchoEventsListener() {
//                    @Override
//                    public void OnSynchroEnd() {
//                        DashboardActivity.this.refreshList();
//                    }
//                });
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
