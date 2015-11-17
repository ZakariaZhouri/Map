package com.example.formation.devryweremaps;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by formation on 06/11/15.
 */
public class AddressToCoordinated {

    private final String LOG_TAG = AddressToCoordinated.class.getSimpleName();
    private Context context;
    private LatLng latLng ;

    public AddressToCoordinated(Context context)
    {
        this.context = context;
    }


    protected LatLng getLatLng(String... params)
    {
        if (params.length == 0) {
            return null;
        }
        Geocoder geocoder = new Geocoder(context, Locale.FRANCE);
        try {
            List<Address> addressList = geocoder.getFromLocationName(params[0], 1);
            if (addressList != null && addressList.size() > 0) {
                Address address = addressList.get(0);
                latLng =new LatLng(address.getLatitude(), address.getLongitude());
                return latLng;
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
        }
        return null;
    }

}
