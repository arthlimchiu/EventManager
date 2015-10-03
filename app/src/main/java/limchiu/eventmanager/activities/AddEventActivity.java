package limchiu.eventmanager.activities;

import android.app.DialogFragment;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import limchiu.eventmanager.R;
import limchiu.eventmanager.database.EventsTable;
import limchiu.eventmanager.fragments.DatePickerFragment;
import limchiu.eventmanager.fragments.EndTimePickerFragment;
import limchiu.eventmanager.fragments.StartTimePickerFragment;
import limchiu.eventmanager.provider.EventManagerProvider;
import limchiu.eventmanager.services.FetchAddressIntentService;

public class AddEventActivity extends AppCompatActivity {

    public static final int REQUEST_LOCATION = 1;
    public static final int SUCCESS_RESULT = 2;
    public static final int FAILURE_RESULT = 3;

    public static final String ADDRESS_RECEIVER = "limchiu.eventmanager.RECEIVER";
    public static final String LATITUDE_EXTRA = "limchiu.eventmanager.LATITUDE";
    public static final String LONGITUDE_EXTRA = "limchiu.eventmanager.LONGITUDE";
    public static final String RESULT_DATA_KEY = "limchiu.eventmanager.RESULT_DATA_KEY";

    private EditText mEventNameText;
    private TextView mAddressText;
    private TextView mDateText;
    private TextView mTimeStartText;
    private TextView mTimeEndText;
    private Button mBtnTimeStart;
    private Button mBtnTimeEnd;
    private Button mBtnDate;
    private Button mBtnLocation;

    private AddressReceiver mReceiver;

    private double latitude;
    private double longitude;
    private long id = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Add Event");

        final ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        mReceiver = new AddressReceiver(new Handler());

        mEventNameText = (EditText) findViewById(R.id.add_event_name);
        mAddressText = (TextView) findViewById(R.id.add_event_address);
        mDateText = (TextView) findViewById(R.id.add_event_date);
        mTimeStartText = (TextView) findViewById(R.id.add_event_time_start);
        mTimeEndText = (TextView) findViewById(R.id.add_event_time_end);

        Intent i = getIntent();
        if (i != null) {
            id = i.getLongExtra(EventsTable.COLUMN_ID, -1);
            mEventNameText.setText(i.getStringExtra(EventsTable.COLUMN_NAME));
            mAddressText.setText(i.getStringExtra(EventsTable.COLUMN_ADDRESS));
            mDateText.setText(i.getStringExtra(EventsTable.COLUMN_DATE));
            mTimeStartText.setText(i.getStringExtra(EventsTable.COLUMN_TIME_START));
            mTimeEndText.setText(i.getStringExtra(EventsTable.COLUMN_TIME_END));
        }

        mBtnTimeStart = (Button) findViewById(R.id.btn_time_start);
        mBtnTimeStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment timeStartFragment = new StartTimePickerFragment();
                timeStartFragment.show(getFragmentManager(), "Time Start");
            }
        });
        mBtnTimeEnd = (Button) findViewById(R.id.btn_time_end);
        mBtnTimeEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment timeEndFragment = new EndTimePickerFragment();
                timeEndFragment.show(getFragmentManager(), "Time Start");
            }
        });
        mBtnDate = (Button) findViewById(R.id.btn_date);
        mBtnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment dateFragment = new DatePickerFragment();
                dateFragment.show(getFragmentManager(), "Date of Event");
            }
        });
        mBtnLocation = (Button) findViewById(R.id.btn_location);
        mBtnLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AddEventActivity.this, MapsActivity.class);
                i.putExtra(EventsTable.COLUMN_ID, id);
                startActivityForResult(i, REQUEST_LOCATION);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_event, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.save_event:
                if (hasErrors()) {
                    Toast.makeText(this, "Please fill out all fields.", Toast.LENGTH_SHORT).show();
                } else {
                    if (id == -1) {
                        addEvent();
                    } else {
                        editEvent();
                    }
                    finish();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            mAddressText.setText("Fetching Address...");
            latitude = data.getDoubleExtra(LATITUDE_EXTRA, 0);
            longitude = data.getDoubleExtra(LONGITUDE_EXTRA, 0);
            startFetchingAddress();
        }
    }

    private void startFetchingAddress() {
        Intent intent = new Intent(this, FetchAddressIntentService.class);

        intent.putExtra(ADDRESS_RECEIVER, mReceiver);
        intent.putExtra(LATITUDE_EXTRA, latitude);
        intent.putExtra(LONGITUDE_EXTRA, longitude);

        startService(intent);
    }

    private boolean hasErrors() {
        if (mEventNameText.getText().toString().isEmpty()) {
            return true;
        } else if (mDateText.getText().toString().isEmpty()) {
            return true;
        } else if (mTimeStartText.getText().toString().isEmpty()) {
            return true;
        } else if (mTimeEndText.getText().toString().isEmpty()) {
            return true;
        } else if (mAddressText.getText().toString().equals("Failed to fetch address") || mAddressText.getText().toString().isEmpty()) {
            return true;
        }
        return false;
    }

    private void addEvent() {
        ContentValues cv = new ContentValues();
        cv.put(EventsTable.COLUMN_NAME, mEventNameText.getText().toString());
        cv.put(EventsTable.COLUMN_ADDRESS, mAddressText.getText().toString());
        cv.put(EventsTable.COLUMN_DATE, mDateText.getText().toString());
        cv.put(EventsTable.COLUMN_LATITUDE, latitude);
        cv.put(EventsTable.COLUMN_LONGITUDE, longitude);
        cv.put(EventsTable.COLUMN_TIME_START, mTimeStartText.getText().toString());
        cv.put(EventsTable.COLUMN_TIME_END, mTimeEndText.getText().toString());

        getContentResolver().insert(EventManagerProvider.CONTENT_URI_EVENTS, cv);
    }

    private void editEvent() {
        ContentValues cv = new ContentValues();
        cv.put(EventsTable.COLUMN_NAME, mEventNameText.getText().toString());
        cv.put(EventsTable.COLUMN_ADDRESS, mAddressText.getText().toString());
        cv.put(EventsTable.COLUMN_DATE, mDateText.getText().toString());
        cv.put(EventsTable.COLUMN_LATITUDE, latitude);
        cv.put(EventsTable.COLUMN_LONGITUDE, longitude);
        cv.put(EventsTable.COLUMN_TIME_START, mTimeStartText.getText().toString());
        cv.put(EventsTable.COLUMN_TIME_END, mTimeEndText.getText().toString());

        getContentResolver().update(Uri.withAppendedPath(EventManagerProvider.CONTENT_URI_EVENTS, String.valueOf(id)), cv, null, null);

        setResult(RESULT_OK);
    }

    public class AddressReceiver extends ResultReceiver {

        public AddressReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            mAddressText.setText(resultData.getString(RESULT_DATA_KEY));
            mBtnLocation.setText("Edit Location");
        }
    }
}
