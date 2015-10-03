package limchiu.eventmanager;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import limchiu.eventmanager.database.EventsTable;

/**
 * Created by Clarence on 10/3/2015.
 */
public class EventsAdapter extends CursorAdapter {

    public EventsAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_event, parent, false);

        ViewHolder holder = new ViewHolder();
        holder.mEventNameText = (TextView) v.findViewById(R.id.event_name);
        holder.mEventDateText = (TextView) v.findViewById(R.id.event_date);
        holder.mEventAddressText = (TextView) v.findViewById(R.id.event_address);

        v.setTag(holder);

        return v;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder holder = (ViewHolder) view.getTag();

        String name = cursor.getString(cursor.getColumnIndex(EventsTable.COLUMN_NAME));
        String date = cursor.getString(cursor.getColumnIndex(EventsTable.COLUMN_DATE));
        String address = cursor.getString(cursor.getColumnIndex(EventsTable.COLUMN_ADDRESS));

        holder.mEventNameText.setText(name);
        holder.mEventDateText.setText(date);
        holder.mEventAddressText.setText(address);
    }

    class ViewHolder {
        TextView mEventNameText;
        TextView mEventDateText;
        TextView mEventAddressText;
    }
}
