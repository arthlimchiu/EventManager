package limchiu.eventmanager.activities;

import android.content.Intent;
import android.database.Cursor;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import limchiu.eventmanager.EventsAdapter;
import limchiu.eventmanager.R;
import limchiu.eventmanager.database.EventsTable;
import limchiu.eventmanager.provider.EventManagerProvider;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private int EVENTS_LOADER = 1;

    private ListView mEventsList;
    private FloatingActionButton mFab;

    private EventsAdapter mAdapter;
    private CursorLoader mCursorLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mEventsList = (ListView) findViewById(R.id.listView);
        mEventsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(MainActivity.this, EventDetailsActivity.class);
                i.putExtra(EventsTable.COLUMN_ID, id);
                startActivity(i);
            }
        });
        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, AddEventActivity.class);
                startActivity(i);
            }
        });

        getSupportLoaderManager().initLoader(EVENTS_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = new String[]{
                EventsTable.COLUMN_ID,
                EventsTable.COLUMN_NAME,
                EventsTable.COLUMN_DATE,
                EventsTable.COLUMN_ADDRESS
        };

        mCursorLoader = new CursorLoader(this, EventManagerProvider.CONTENT_URI_EVENTS, projection, null, null, EventsTable.COLUMN_ID + " desc");
        return mCursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter = new EventsAdapter(this, data);
        mEventsList.setAdapter(mAdapter);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }
}
