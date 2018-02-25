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

public class EventsAdapter extends CursorAdapter {

    public EventsAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        final View itemLayout = LayoutInflater.from(context).inflate(R.layout.row_event, viewGroup, false);
        final ViewHolder holder = new ViewHolder();
        holder.rowEventLayout = (LinearLayout) itemLayout.findViewById(R.id.rowEventLayout);
        holder.rowEventName = (TextView) itemLayout.findViewById(R.id.rowEventName);
        itemLayout.setTag(holder);
        return itemLayout;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        final ViewHolder holder = (ViewHolder) view.getTag();
        final int position = cursor.getPosition();

        String giftName = cursor.getString(cursor.getColumnIndexOrThrow("title"));
        holder.rowEventName.setText(giftName);
        holder.eventId = cursor.getString(cursor.getColumnIndexOrThrow("eventId"));
        Log.w("x", "" + holder.eventId);

    }

    public void changeDate(Cursor cursor) {
        changeCursor(cursor);
    }

    public static class ViewHolder {
        public String eventId;
        TextView rowEventName;
        LinearLayout rowEventLayout;
    }
}
