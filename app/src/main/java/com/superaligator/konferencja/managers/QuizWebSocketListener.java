package com.superaligator.konferencja.managers;


import android.util.Log;

import com.superaligator.konferencja.activity.QuizActivity;
import com.superaligator.konferencja.interfaces.onMessageWS;

import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class QuizWebSocketListener extends WebSocketListener {
    onMessageWS listener;

    public QuizWebSocketListener(onMessageWS l) {
        this.listener = l;
    }

    @Override
    public void onOpen(WebSocket webSocket, okhttp3.Response response) {
        super.onOpen(webSocket, response);
        Log.w("x", "onOpen");
        listener.onOpen(webSocket, response);
    }

    @Override
    public void onMessage(WebSocket webSocket, String text) {
        super.onMessage(webSocket, text);
        Log.w("x", "onMessage");
        listener.onMessage(webSocket, text);
    }

    @Override
    public void onMessage(WebSocket webSocket, ByteString bytes) {
        super.onMessage(webSocket, bytes);
        Log.w("x", "onMessage");
        listener.onMessage(webSocket, bytes);
    }

    @Override
    public void onClosed(WebSocket webSocket, int code, String reason) {
        super.onClosed(webSocket, code, reason);
        Log.w("x", "onClosed");
    }

    @Override
    public void onClosing(WebSocket webSocket, int code, String reason) {
        super.onClosing(webSocket, code, reason);
        Log.w("x", "onOclosing");
    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable t, okhttp3.Response response) {
        super.onFailure(webSocket, t, response);
        Log.w("x", "onFailure");
    }
}
