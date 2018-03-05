package com.superaligator.konferencja.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.superaligator.konferencja.R;
import com.superaligator.konferencja.dbmodels.Event;
import com.superaligator.konferencja.models.EnrollResponse;
import com.superaligator.konferencja.network.Comunicator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EnrollActivity extends BaseUserActivity {

    Call<EnrollResponse> eventCall;
    View enrollFormLay, enrollInfoLay;
    TextView enrollInfoText;
    Button btnEnrollAction;
    EditText tvAccessCode;
    IntentIntegrator integrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enroll);
        if (2 == 1) {
            throw new IllegalStateException("krasz testowy");
        }
        enrollFormLay = findViewById(R.id.enrollFormLay);
        enrollInfoLay = findViewById(R.id.enrollInfoLay);
        enrollInfoText = (TextView) findViewById(R.id.enrollInfoText);

        enrollFormLay.setVisibility(View.VISIBLE);
        enrollInfoLay.setVisibility(View.INVISIBLE);

        tvAccessCode = (EditText) findViewById(R.id.tvAccessCode);

        Button btnEnrollEvent = findViewById(R.id.btnEnrollEvent);
        btnEnrollEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enrollEvent(tvAccessCode.getText().toString());
            }
        });

        Button btnRunScanner = findViewById(R.id.btnRunScanner);
        btnRunScanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                runScanner();
            }
        });

        btnEnrollAction = findViewById(R.id.btnEnrollAction);
        btnEnrollAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enrollAction();
            }
        });

        integrator = new IntentIntegrator(this);
        integrator.setPrompt("Panie zrób mnie Pan zdjęcie");
        integrator.setCameraId(0);
        //integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
        integrator.setBeepEnabled(false);
        integrator.setBarcodeImageEnabled(true);
    }

    private void runScanner() {
        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Nie znalezionon kodu", Toast.LENGTH_LONG).show();
                //info.setText("nie znaleziono kodu");
            } else {
                Toast.makeText(this, "Znaleziono kod: " + result.getContents(), Toast.LENGTH_SHORT).show();
                enrollEvent(result.getContents());
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    boolean actionSuccess = false;

    public void enrollAction() {
        if (actionSuccess) {
            //goto widok wydarzenia
            return;
        }
        tvAccessCode.setText("");
        enrollFormLay.setVisibility(View.VISIBLE);
        enrollInfoLay.setVisibility(View.INVISIBLE);
    }

    private void enrollEvent(String code) {
        if (eventCall != null) {
            eventCall.cancel();
            eventCall = null;
        }
        showLoading();
        eventCall = Comunicator.getInstance().getApiService().registerEvent(code);
        eventCall.enqueue(new Callback<EnrollResponse>() {
            @Override
            public void onResponse(Call<EnrollResponse> call, Response<EnrollResponse> response) {
                EnrollActivity.this.hideLoading();
                if (response.isSuccessful() == false) {
                    enrollFail();
                    return;
                }
                enroll(response.body().eventId, response.body().accessCode);
                enrollFormLay.setVisibility(View.INVISIBLE);
                enrollInfoLay.setVisibility(View.VISIBLE);
                enrollInfoText.setText(R.string.event_success_info);
                actionSuccess = true;
                btnEnrollAction.setText(R.string.btn_event_success);
                btnEnrollAction.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //todo intent do wydarzenia z finish
                    }
                });
            }

            @Override
            public void onFailure(Call<EnrollResponse> call, Throwable t) {
                enrollFail();
                if (call.isCanceled()) {
                    Log.e("x", "request was cancelled");
                } else {
                    Log.w("x", "błąd rejestracji w wydarzeniu");
                    t.printStackTrace();
                }
            }
        });
    }

    private void enrollFail() {
        EnrollActivity.this.hideLoading();
        actionSuccess = false;
        enrollInfoText.setText(R.string.event_fail_info);
        btnEnrollAction.setText(R.string.btn_event_fail);
        enrollFormLay.setVisibility(View.INVISIBLE);
        enrollInfoLay.setVisibility(View.VISIBLE);
    }

    private void enroll(String eventId, String accessCode) {
        Event ev = Event.findByEventId(eventId);
        ev.access = 1;
        ev.accessCode = accessCode;
        ev.save();
        //todo pokazać dialog ok i przy zamykaniu przjście na stronę wydarzenia
        //....
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (eventCall != null) {
            eventCall.cancel();
            eventCall = null;
        }
    }
}
