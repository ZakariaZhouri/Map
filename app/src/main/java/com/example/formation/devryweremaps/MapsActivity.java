package com.example.formation.devryweremaps;

import java.util.ArrayList;
import java.util.List;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    public static final int MAP_TYPE_REQUEST = 1;

    @Bind(R.id.departure_adress)
    EditText mDepartureAddressEditText;

    @Bind(R.id.arrival_adress)
    EditText mArrivalAddressEditText;

    @Bind(R.id.buttonsearch)
    ImageButton mButtonAdresse;

    @Bind(R.id.zoomin)
    Button mButtonZoomIn;

    @Bind(R.id.zoomout)
    Button mZoomOut;

    private final int bestPadding = 144;

    private float mZoom;

    private LatLng mMyLatLng;

    private ItineraryInformation mItineraryInformation;

    private GoogleMap mMap;

    private AddressToCoordinated mAddressToCoordinate;

    private Marker mArrivalMarker;

    private Marker mDepartureMarker;

    private GoogleApiClient mGoogleApiClient;

    private Polyline mPolyline;

    private int mMapType;

    private List<Marker> mMarkerList = new ArrayList<Marker>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        ButterKnife.bind(this);
        mAddressToCoordinate = new AddressToCoordinated(getApplicationContext());
        mItineraryInformation = savedInstanceState != null ? savedInstanceState.<ItineraryInformation> getParcelable(ItineraryInformation.ITINERARY_INFORMATION)
                : new ItineraryInformation();
        mGoogleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putParcelable(ItineraryInformation.ITINERARY_INFORMATION, mItineraryInformation);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MAP_TYPE_REQUEST) {
            if (resultCode == RESULT_OK) {
                final int type = data.getIntExtra(MapTypeActivity.Map_Type, GoogleMap.MAP_TYPE_NORMAL);
                mMap.setMapType(type);
                mMapType = mMap.getMapType();
            }
        }
    }

    /**
     * Override Map Manipulates the map once available. This callback is triggered when the map is ready to be used. This is where
     * we can add markers or lines, add listeners or move the camera. In this case, we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install it inside the
     * SupportMapFragment. This method will only be triggered once the user has installed Google Play services and returned to the
     * app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mMap = googleMap;
    }

    @Override
    public void onConnected(Bundle bundle)
    {
        geoloc();
        if (mItineraryInformation.getDepartureLatLng() != null && mItineraryInformation.getArrivalLatLng() != null) {
            removeAllMarkers();
            addMarker(mItineraryInformation.getDepartureLatLng(), BitmapDescriptorFactory.HUE_ORANGE, true);
            addMarker(mItineraryInformation.getArrivalLatLng(), BitmapDescriptorFactory.HUE_GREEN, false);
            addPolyline(mItineraryInformation.getDepartureLatLng(), mItineraryInformation.getArrivalLatLng());
        }
        mMap.setMyLocationEnabled(true);
    }

    @Override
    public void onConnectionSuspended(int i)
    {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult)
    {

    }

    /**
     * Listeners
     */
    @OnClick(R.id.buttonsearch)
    public void OnButtonSerchClicked()
    {
        removeAllMarkers();
        final String departureAddress = String.valueOf(mDepartureAddressEditText.getText());
        final String arrivalAddress = String.valueOf(mArrivalAddressEditText.getText());
        if (arrivalAddress.equals("")) {
            ErrorsUtil.poupUpError(MapsActivity.this, getString(R.string.alert));
            return;
        }
        final LatLng departureLatLng = (mAddressToCoordinate.getLatLng(departureAddress) != null) ? mAddressToCoordinate.getLatLng(departureAddress)
                : mMyLatLng;
        mItineraryInformation.setDepartureLatLng(departureLatLng);
        final LatLng arrivalLatLng = mAddressToCoordinate.getLatLng(arrivalAddress);
        mItineraryInformation.setArrivalLatLng(arrivalLatLng);
        addMarker(departureLatLng, BitmapDescriptorFactory.HUE_GREEN, true);

        addMarker(arrivalLatLng, BitmapDescriptorFactory.HUE_ORANGE, false);
        centerTheMapWithBestZoom(departureLatLng);
        addPolyline(departureLatLng, arrivalLatLng);
        mMarkerList.clear();
    }

    @OnClick(R.id.zoomout)
    public void OnZommOutClicked()
    {
        mZoom = mMap.getCameraPosition().zoom;
        if (mZoom > mMap.getMinZoomLevel()) {
            mZoom -= 1;
            mMap.animateCamera(CameraUpdateFactory.zoomTo(mZoom), 2000, null);
        }

    }

    @OnClick(R.id.zoomin)
    public void OnZommInClicked()
    {
        mZoom = mMap.getCameraPosition().zoom;
        if (mZoom > mMap.getMinZoomLevel()) {
            mZoom += 1;
            mMap.animateCamera(CameraUpdateFactory.zoomTo(mZoom), 2000, null);
        }

    }

    @OnClick(R.id.button_geoloc)
    public void OnGeolocInClicked()
    {
        new ItineratyTask(this, mMap, String.valueOf(mDepartureAddressEditText.getText()), String.valueOf(mArrivalAddressEditText.getText())).execute();

    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @OnClick(R.id.map_type_button)
    public void OnMapTypeButtonClicked()
    {
        Intent intent = new Intent(this, MapTypeActivity.class);
        startActivityForResult(intent, MAP_TYPE_REQUEST, null);
    }

    /**
     * The Map Logic
     *
     * @param latLng
     * @param colorOfMarker
     * @param isDeparture
     */
    private void addMarker(LatLng latLng, final float colorOfMarker, boolean isDeparture)
    {

        if (isDeparture) {
            mDepartureMarker = mMap.addMarker(new MarkerOptions().position(latLng)
                    .title("Marker")
                    .snippet("snippet")
                    .icon(BitmapDescriptorFactory.defaultMarker(colorOfMarker)));
            mMarkerList.add(mDepartureMarker);
        } else {
            mArrivalMarker = mMap.addMarker(new MarkerOptions().position(latLng)
                    .title("Marker")
                    .snippet("snippet")
                    .icon(BitmapDescriptorFactory.defaultMarker(colorOfMarker)));
            mMarkerList.add(mArrivalMarker);
        }

        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
    }

    private void removeAllMarkers()
    {
        if (mArrivalMarker != null) {
            mArrivalMarker.remove();
        }
        if (mDepartureMarker != null) {
            mDepartureMarker.remove();
        }
    }

    private void addPolyline(LatLng departureLocation, LatLng arrivalLocation)
    {
        if (mPolyline != null) {
            mPolyline.remove();
        }
        mPolyline = mMap.addPolyline(new PolylineOptions().add(departureLocation, arrivalLocation).width(5).color(Color.RED));
    }

    private void centerMap()
    {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Marker marker : mMarkerList) {
            builder.include(marker.getPosition());
        }
        LatLngBounds bounds = builder.build();
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, bestPadding);
        mMap.animateCamera(cu);
    }

    private void setProperZoomLevel(LatLng loc, int radius, int nbPoi)
    {
        // [1] init mZoom & move camera & result
        float currentZoomLevel = mMap.getMaxZoomLevel();
        int currentFoundPoi = 0;
        LatLngBounds bounds = null;
        List<Marker> foundMarkers = new ArrayList<Marker>();
        Location location = latlngToLocation(loc);

        boolean keepZoomingOut = true;
        boolean keepSearchingForWithinRadius = true;// this is true if we keep looking
        // within a radius of 100km for ex:

        while (keepZoomingOut) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, currentZoomLevel--));
            bounds = mMap.getProjection().getVisibleRegion().latLngBounds;
            keepSearchingForWithinRadius = (Math.round(location.distanceTo(latlngToLocation(bounds.northeast)) / 1000) > radius) ? false
                    : true;
            for (Marker k : mMarkerList) {
                if (bounds.contains(k.getPosition())) {
                    if (!foundMarkers.contains(k)) {
                        currentFoundPoi++;
                        foundMarkers.add(k);
                    }
                }
                if (keepSearchingForWithinRadius) {
                    if (currentFoundPoi > nbPoi) {
                        keepZoomingOut = false;
                        break;

                    }// else keep looking

                } else if (currentFoundPoi > 0) {
                    keepZoomingOut = false;
                    break;

                } else if (currentZoomLevel < mMap.getMinZoomLevel()) {// [5] keep looking but

                    keepZoomingOut = false;
                    break;
                }
            }
            keepZoomingOut = ((currentZoomLevel > 0) && keepZoomingOut) ? true : false;

        }
    }

    private Location latlngToLocation(LatLng dest)
    {
        Location loc = new Location("");
        loc.setLatitude(dest.latitude);
        loc.setLongitude(dest.longitude);
        return loc;
    }

    private void centerTheMapWithBestZoom(final LatLng latLng)
    {
        centerMap();

        Runnable runnable = new Runnable() {

            public void run()
            {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run()
                    {
                        setProperZoomLevel(latLng, 7, 2);
                    }
                });

            }
        };
    }

    private void geoloc()
    {
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (location != null) {
            mMyLatLng = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mMyLatLng, 15));
        }
    }

    /**
     * Getter needed for mMapType
     *
     * @return
     */
    public ItineraryInformation getItineraryInformation()
    {
        return mItineraryInformation;
    }

    public float getZoom()
    {
        return mZoom;
    }

    public int getMapType()
    {
        return mMapType;
    }
}
