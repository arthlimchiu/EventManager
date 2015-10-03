package limchiu.eventmanager.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;

import com.google.android.gms.maps.SupportMapFragment;

import limchiu.eventmanager.R;
import limchiu.eventmanager.database.EventsTable;
import limchiu.eventmanager.fragments.MapFragment;

public class MapsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        Intent i = getIntent();
        long id = i.getLongExtra(EventsTable.COLUMN_ID, -1);

        FragmentManager fm = getSupportFragmentManager();
        SupportMapFragment fragment = (SupportMapFragment) fm.findFragmentById(R.id.map);

        if (fragment == null) {
            if (id == -1) {
                fragment = new MapFragment();
            } else {
                fragment = MapFragment.newInstance(id, true);
            }

            fm.beginTransaction()
                    .add(R.id.map, fragment)
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_maps, menu);
        return true;
    }
}
