package com.superaligator.konferencja.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.superaligator.konferencja.managers.MyProgressDialog;
import com.superaligator.konferencja.managers.UserManager;

public class BaseUserActivity extends AppCompatActivity implements DialogInterface.OnCancelListener {

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!UserManager.getInstance().isLoggedIn()) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    public void showLoading() {
        if (progressDialog == null)
            progressDialog = MyProgressDialog.show(this);
        progressDialog.setCancelable(true);
        progressDialog.setOnCancelListener(this);
        progressDialog.show();
    }

    public void hideLoading(){
        if(progressDialog == null)
            return;
        progressDialog.dismiss();
    }

    @Override
    public void onCancel(DialogInterface dialogInterface) {

    }
}
