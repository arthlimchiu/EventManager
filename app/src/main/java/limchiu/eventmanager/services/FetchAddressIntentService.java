package limchiu.eventmanager.services;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import limchiu.eventmanager.activities.AddEventActivity;


public class FetchAddressIntentService extends IntentService {

    private ResultReceiver mReceiver;

    public FetchAddressIntentService() {
        super("FetchAddressIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        mReceiver = intent.getParcelableExtra(AddEventActivity.ADDRESS_RECEIVER);

        if (mReceiver == null) {
            Log.e("FetchAddressService", "No receiver received. There is nowhere to send results");
            return;
        }

        double latitude = intent.getDoubleExtra(AddEventActivity.LATITUDE_EXTRA, 0);
        double longitude = intent.getDoubleExtra(AddEventActivity.LONGITUDE_EXTRA, 0);

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        List<Address> addresses = null;

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
        } catch (IOException | IllegalArgumentException e) {
            Log.e("FetchAddressService", e.toString());
        }

        if (addresses == null || addresses.size() == 0) {
            deliverResultToReceiver(AddEventActivity.FAILURE_RESULT, "Failed to fetch address");
        } else {
            Address address = addresses.get(0);
            ArrayList<String> addressFragments = new ArrayList<String>();

            for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                addressFragments.add(address.getAddressLine(i));
            }
            Log.i("FetchAddressService", "Address found.");
            deliverResultToReceiver(AddEventActivity.SUCCESS_RESULT, TextUtils.join(", ", addressFragments));
        }
    }

    private void deliverResultToReceiver(int resultCode, String message) {
        Bundle bundle = new Bundle();
        bundle.putString(AddEventActivity.RESULT_DATA_KEY, message);
        mReceiver.send(resultCode, bundle);
    }

}
