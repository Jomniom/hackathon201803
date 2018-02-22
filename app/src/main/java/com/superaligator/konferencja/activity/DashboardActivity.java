package com.superaligator.konferencja.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.superaligator.konferencja.R;
import com.superaligator.konferencja.dbmodels.Event;
import com.superaligator.konferencja.models.EventsResponse;
import com.superaligator.konferencja.network.Comunicator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/***
 * - lista wydarzeń: historyczne, oferowane, moje
 *
 * - rejestracja wydarzenia
 * - pobranie danych wydarzenia: informacje, materiały, konkursy,
 *  zadawanie pytań, głosowania uchwał
 * - generowanie qrkoda
 */

public class DashboardActivity extends BaseUserActivity {

    Call<EventsResponse> eventsCall;

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
                Event.synchroEvents(response.body());
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
}
