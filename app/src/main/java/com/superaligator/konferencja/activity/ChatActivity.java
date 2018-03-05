package com.superaligator.konferencja.activity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.superaligator.konferencja.R;
import com.superaligator.konferencja.adapters.ChatAdapter;
import com.superaligator.konferencja.dbmodels.Event;
import com.superaligator.konferencja.dbmodels.ChatQuestion;
import com.superaligator.konferencja.models.ChatQuestionReq;
import com.superaligator.konferencja.network.Comunicator;

import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends BaseUserActivity {

    public static final String EXTRA_EVENT_ID = "eventId";
    boolean canSend = false;
    EditText editTextMessage;
    Event event = null;
    Call<Void> questionCall;
    ListView listView;
    ChatAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Intent i = getIntent();
        String eventId = i.getStringExtra(EXTRA_EVENT_ID);
        if (eventId != null) {
            event = Event.findByEventId(eventId);
            canSend = true;
        }

        listView = (ListView) findViewById(R.id.chatList);
        Cursor cursor = ChatQuestion.fetchResultCursor(event.eventId);
        adapter = new ChatAdapter(this, cursor);
        listView.setClickable(true);
        // listView.setOnItemClickListener(this);
        listView.setAdapter(adapter);
        refreshList(event.eventId);
        editTextMessage = (EditText) findViewById(R.id.editText);
        ((Button) findViewById(R.id.button5)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (questionCall != null) {
            questionCall.cancel();
            questionCall = null;
        }
    }

    private void messageFail() {
        Toast.makeText(ChatActivity.this, "Błąd wysyłania wiadomości", Toast.LENGTH_SHORT);
        canSend = true;
    }

    private void messageSuccess() {
        canSend = true;
        String txt = editTextMessage.getText().toString();
        editTextMessage.setText("");
        ChatQuestion q = new ChatQuestion();
        q.question = txt;
        q.eventId = event.eventId;
        q.dateQuestion = new Date();
        q.save();
        refreshList(event.eventId);
    }

    void refreshList(String eventId) {
        if (adapter == null)
            return;
        Cursor cursor = ChatQuestion.fetchResultCursor(eventId);
        adapter.changeDate(cursor);
        scrollToBottom();
    }


    private void sendMessage() {
        if (!canSend) {
            return;
        }
        dismissKeyboard();
        if (questionCall != null) {
            questionCall.cancel();
            questionCall = null;
        }
        showLoading();
        ChatQuestionReq q = new ChatQuestionReq();
        //q.setDate(new Date());
        q.setEventId(Long.valueOf(event.eventId));
        q.setParticipantId(1001L);
        q.setQuestion(editTextMessage.getText().toString());
        questionCall = Comunicator.getInstance().getApiService().sendQuestion(q);
        questionCall.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                ChatActivity.this.hideLoading();
                if (response.isSuccessful() == false) {
                    messageFail();
                    return;
                }
                messageSuccess();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                ChatActivity.this.hideLoading();
                messageFail();
            }
        });
    }

    private void scrollToBottom() {
        listView.post(new Runnable() {
            @Override
            public void run() {
                listView.setSelection(listView.getCount() - 1);
            }
        });
    }

    public void dismissKeyboard() {
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (null != this.getCurrentFocus())
            imm.hideSoftInputFromWindow(this.getCurrentFocus().getApplicationWindowToken(), 0);
    }
}
