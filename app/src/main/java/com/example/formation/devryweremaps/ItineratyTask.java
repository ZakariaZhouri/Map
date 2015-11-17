package com.example.formation.devryweremaps;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Created by formation on 17/11/15.
 */
public class ItineratyTask extends AsyncTask<Void, Integer, Boolean> {

    private Context context;

    private GoogleMap gMap;

    private final ArrayList<LatLng> lstLatLng = new ArrayList<LatLng>();

    private String mDeparture;

    private String mArrival;

    public ItineratyTask(Context context, GoogleMap map, String departure, String arrival){
        this.context = context;
        this.gMap = map;
        this.mDeparture = departure;
        this.mArrival = arrival;
    }

    @Override
    protected Boolean doInBackground(Void... params)
    {
        try {
            // Construction de l'url à appeler
            final StringBuilder url = new StringBuilder(
                    "http://maps.googleapis.com/maps/api/directions/xml?sensor=false&language=fr");
            url.append("&origin=");
            url.append(mDeparture.replace(' ', '+'));
            url.append("&destination=");
            url.append(mArrival.replace(' ', '+'));

            final InputStream stream = new URL(url.toString()).openStream();

            final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            documentBuilderFactory.setIgnoringComments(true);

            final DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

            final Document document = documentBuilder.parse(stream);
            document.getDocumentElement().normalize();


            final String status = document.getElementsByTagName("status").item(0).getTextContent();
            if (!"OK".equals(status)) {
                return false;
            }


            final Element elementLeg = (Element) document.getElementsByTagName("leg").item(0);
            final NodeList nodeListStep = elementLeg.getElementsByTagName("step");
            final int length = nodeListStep.getLength();

            for (int i = 0; i < length; i++) {
                final Node nodeStep = nodeListStep.item(i);

                if (nodeStep.getNodeType() == Node.ELEMENT_NODE) {
                    final Element elementStep = (Element) nodeStep;


                    decodePolylines(elementStep.getElementsByTagName("points").item(0).getTextContent());
                }
            }

            return true;
        } catch (final Exception e) {
            return false;
        }
    }

    private void decodePolylines(final String encodedPoints)
    {
        int index = 0;
        int lat = 0, lng = 0;

        while (index < encodedPoints.length()) {
            int b, shift = 0, result = 0;

            do {
                b = encodedPoints.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);

            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;
            shift = 0;
            result = 0;

            do {
                b = encodedPoints.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);

            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            lstLatLng.add(new LatLng((double) lat / 1E5, (double) lng / 1E5));
        }
    }

    @Override
    protected void onPostExecute(final Boolean result)
    {
        if (!result) {
            Toast.makeText(context, "EROOR", Toast.LENGTH_SHORT).show();
        } else {

            final PolylineOptions polylines = new PolylineOptions();
            polylines.color(Color.BLUE);

            for (final LatLng latLng : lstLatLng) {
                polylines.add(latLng);
            }


            final MarkerOptions markerA = new MarkerOptions();
            markerA.position(lstLatLng.get(0));
            markerA.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));


            final MarkerOptions markerB = new MarkerOptions();
            markerB.position(lstLatLng.get(lstLatLng.size() - 1));
            markerB.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));


            gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lstLatLng.get(0), 10));
            gMap.addMarker(markerA);
            gMap.addPolyline(polylines);
            gMap.addMarker(markerB);
        }
    }

}
