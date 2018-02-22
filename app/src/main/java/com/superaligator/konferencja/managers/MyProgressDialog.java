package com.superaligator.konferencja.managers;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.superaligator.konferencja.App;
import com.superaligator.konferencja.R;


public class MyProgressDialog {

    public static ProgressDialog show(Context ctx) {
        String message = "Poczekaj...";
        return show(ctx, message, null);
    }

    public static ProgressDialog show(Context ctx, DialogInterface.OnCancelListener cancelListener) {
        String message = "Poczekaj...";
        return show(ctx, message, cancelListener);
    }

    public static ProgressDialog show(Context ctx, String message, DialogInterface.OnCancelListener cancelListener) {
        ProgressDialog dialog = new ProgressDialog(ctx);
        dialog.setMessage(message);
        dialog.setIndeterminate(true);
        dialog.setCanceledOnTouchOutside(false);

        if (cancelListener != null) {
            dialog.setOnCancelListener(cancelListener);
            dialog.setCancelable(true);
        }
        dialog.show();
        return dialog;
    }

}
