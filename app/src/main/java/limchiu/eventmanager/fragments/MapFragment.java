package limchiu.eventmanager.fragments;


import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import limchiu.eventmanager.R;
import limchiu.eventmanager.activities.AddEventActivity;
import limchiu.eventmanager.database.EventsTable;
import limchiu.eventmanager.provider.EventManagerProvider;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends SupportMapFragment implements OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private GoogleMap mMap;
    private MarkerOptions mMarker;

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    private LatLng mLatLng;

    private long id = -1;
    private boolean canEdit;

    public MapFragment() {
        // Required empty public constructor
    }

    public static MapFragment newInstance(long id) {
        Bundle args = new Bundle();
        args.putLong(EventsTable.COLUMN_ID, id);

        MapFragment fragment = new MapFragment();
        fragment.setArguments(args);

        return fragment;
    }

    public static MapFragment newInstance(long id, boolean canEdit) {
        Bundle args = new Bundle();
        args.putLong(EventsTable.COLUMN_ID, id);
        args.putBoolean("canEdit", canEdit);

        MapFragment fragment = new MapFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getMapAsync(this);

        Bundle args = getArguments();
        if (args != null) {
            id = args.getLong(EventsTable.COLUMN_ID);
            canEdit = args.getBoolean("canEdit", false);
        }

        buildGoogleApiClient();

        mMarker = new MarkerOptions();

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_location:
                Intent i = new Intent();
                i.putExtra(AddEventActivity.LATITUDE_EXTRA, mLatLng.latitude);
                i.putExtra(AddEventActivity.LONGITUDE_EXTRA, mLatLng.longitude);
                getActivity().setResult(Activity.RESULT_OK, i);
                getActivity().finish();
                return true;
            case android.R.id.home:
                getActivity().finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mGoogleApiClient.isConnected()) {
            startLocationUpdates();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            stopLocationUpdates();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapClickListener(this);
        if (id != -1) {

            String[] projection = new String[]{
                    EventsTable.COLUMN_LATITUDE,
                    EventsTable.COLUMN_LONGITUDE
            };

            Cursor cursor = getActivity().getContentResolver().query(Uri.withAppendedPath(EventManagerProvider.CONTENT_URI_EVENTS, String.valueOf(id)), projection, null, null, null);

            if (cursor.moveToNext()) {
                double latitude = cursor.getDouble(cursor.getColumnIndex(EventsTable.COLUMN_LATITUDE));
                double longitude = cursor.getDouble(cursor.getColumnIndex(EventsTable.COLUMN_LONGITUDE));

                mLatLng =  new LatLng(latitude, longitude);
                mMarker.position(mLatLng);
                mMap.addMarker(mMarker);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mLatLng, 16));
            }

            cursor.close();
        }
    }


    @Override
    public void onMapClick(LatLng latLng) {
        if (canEdit || id == -1) {
            mMap.clear();
            mMarker.position(latLng);
            mMap.addMarker(mMarker);
            mLatLng = latLng;
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        if (id == -1) {
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            mLatLng = latLng;
            mMarker.position(latLng);
            mMap.addMarker(mMarker);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
        }
        stopLocationUpdates();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    private void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        createLocationRequest();
    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    private void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }
}
