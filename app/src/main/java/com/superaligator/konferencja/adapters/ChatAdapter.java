package com.superaligator.konferencja.adapters;


import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.superaligator.konferencja.R;

public class ChatAdapter extends CursorAdapter {

    public ChatAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        final View itemLayout = LayoutInflater.from(context).inflate(R.layout.row_chat, viewGroup, false);
        final ChatAdapter.ViewHolder holder = new ChatAdapter.ViewHolder();
        holder.rowChatLayout = (LinearLayout) itemLayout.findViewById(R.id.rowChatLayout);
        holder.rowChatName = (TextView) itemLayout.findViewById(R.id.rowChatName);
        itemLayout.setTag(holder);
        return itemLayout;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        final ChatAdapter.ViewHolder holder = (ChatAdapter.ViewHolder) view.getTag();
        final int position = cursor.getPosition();

        String questionId = cursor.getString(cursor.getColumnIndexOrThrow("question"));
        holder.rowChatName.setText(questionId);
        holder.eventId = cursor.getString(cursor.getColumnIndexOrThrow("eventId"));
        Log.w("x", "" + holder.eventId);

    }

    public void changeDate(Cursor cursor) {
        changeCursor(cursor);
    }

    public static class ViewHolder {
        public String eventId;
        TextView rowChatName;
        LinearLayout rowChatLayout;
    }
}
