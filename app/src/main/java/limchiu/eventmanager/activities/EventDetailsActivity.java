package limchiu.eventmanager.activities;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.SupportMapFragment;

import limchiu.eventmanager.R;
import limchiu.eventmanager.database.EventsTable;
import limchiu.eventmanager.fragments.MapFragment;
import limchiu.eventmanager.provider.EventManagerProvider;

public class EventDetailsActivity extends AppCompatActivity {

    private long id = -1;
    private int EDIT_EVENT = 1;

    private TextView mEventNameText;
    private TextView mAddressText;
    private TextView mDateText;
    private TextView mTimeStartText;
    private TextView mTimeEndText;
    private FloatingActionButton mFab;

    private String name;
    private String date;
    private String timeStart;
    private String timeEnd;
    private String address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        setTitle("");

        mEventNameText = (TextView) findViewById(R.id.event_details_name);
        mDateText = (TextView) findViewById(R.id.event_details_date);
        mTimeStartText = (TextView) findViewById(R.id.event_details_time_start);
        mTimeEndText = (TextView) findViewById(R.id.event_details_time_end);
        mAddressText = (TextView) findViewById(R.id.event_details_address);
        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(EventDetailsActivity.this, AddEventActivity.class);
                i.putExtra(EventsTable.COLUMN_ID, id);
                i.putExtra(EventsTable.COLUMN_NAME, name);
                i.putExtra(EventsTable.COLUMN_DATE, date);
                i.putExtra(EventsTable.COLUMN_TIME_START, timeStart);
                i.putExtra(EventsTable.COLUMN_TIME_END, timeEnd);
                i.putExtra(EventsTable.COLUMN_ADDRESS, address);
                startActivityForResult(i, EDIT_EVENT);
            }
        });

        Intent i = getIntent();
        if (i != null) {
            id = i.getLongExtra(EventsTable.COLUMN_ID, 0);
            populateViews();
        }

        FragmentManager fm = getSupportFragmentManager();
        SupportMapFragment fragment = (SupportMapFragment) fm.findFragmentById(R.id.event_details_map);

        if (fragment == null) {
            fragment = MapFragment.newInstance(id);
            fm.beginTransaction()
                    .add(R.id.event_details_map, fragment)
                    .commit();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_event_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.delete_event:
                getContentResolver().delete(Uri.withAppendedPath(EventManagerProvider.CONTENT_URI_EVENTS, String.valueOf(id)), null, null);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            populateViews();
        }
    }

    private void populateViews() {

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.event_details_map, MapFragment.newInstance(id))
                .commit();

        String[] projection = new String[]{
                EventsTable.COLUMN_NAME,
                EventsTable.COLUMN_ADDRESS,
                EventsTable.COLUMN_DATE,
                EventsTable.COLUMN_TIME_START,
                EventsTable.COLUMN_TIME_END
        };

        Cursor cursor = getContentResolver().query(Uri.withAppendedPath(EventManagerProvider.CONTENT_URI_EVENTS, String.valueOf(id)), projection, null, null, null);

        if (cursor.moveToNext()) {
            name = cursor.getString(cursor.getColumnIndex(EventsTable.COLUMN_NAME));
            date = cursor.getString(cursor.getColumnIndex(EventsTable.COLUMN_DATE));
            timeStart = cursor.getString(cursor.getColumnIndex(EventsTable.COLUMN_TIME_START));
            timeEnd = cursor.getString(cursor.getColumnIndex(EventsTable.COLUMN_TIME_END));
            address = cursor.getString(cursor.getColumnIndex(EventsTable.COLUMN_ADDRESS));

            mEventNameText.setText(name);
            mDateText.setText(date);
            mTimeStartText.setText(timeStart);
            mTimeEndText.setText(timeEnd);
            mAddressText.setText(address);
        }

        cursor.close();
    }
}
