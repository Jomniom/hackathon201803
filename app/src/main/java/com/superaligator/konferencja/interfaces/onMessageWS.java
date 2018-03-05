package com.superaligator.konferencja.interfaces;


import okhttp3.WebSocket;
import okio.ByteString;

public interface onMessageWS {
    void onMessage(WebSocket webSocket, String text);

    void onMessage(WebSocket webSocket, ByteString bytes);

    void onOpen(WebSocket webSocket, okhttp3.Response response);
}
