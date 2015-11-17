package com.example.formation.devryweremaps;

import android.os.SystemClock;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.ActivityInstrumentationTestCase2;

import com.google.android.gms.maps.GoogleMap;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.example.formation.devryweremaps.OrientationChangeAction.orientationLandscape;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
@RunWith(AndroidJUnit4.class)
public class ApplicationTest extends ActivityInstrumentationTestCase2<MapsActivity> {

    private MapsActivity mapsActivity;

    public ApplicationTest()
    {
        super(MapsActivity.class);
    }

    @Before
    public void setUp() throws Exception
    {
        super.setUp();
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
        mapsActivity = getActivity();
    }

    @Test
    public void testDepartureAndArrival()
    {
        String departure = "Paris";
        String arrival = "Marseille";
        AddressToCoordinated addressToCoordinated = new AddressToCoordinated(getActivity());

        onView(withId(R.id.departure_adress)).perform(typeText(departure), closeSoftKeyboard());
        assertEquals(addressToCoordinated.getLatLng(departure).longitude, 2.3522219);
        assertEquals(addressToCoordinated.getLatLng(departure).latitude, 48.856614);

        onView(withId(R.id.arrival_adress)).perform(typeText(arrival), closeSoftKeyboard());
        assertEquals(addressToCoordinated.getLatLng(arrival).longitude, 5.36978);
        assertEquals(addressToCoordinated.getLatLng(arrival).latitude, 43.296482);

        onView(withId(R.id.buttonsearch)).perform(click());
        onView(isRoot()).perform(orientationLandscape());

        SystemClock.sleep(3000);

        assertNotNull(mapsActivity.getItineraryInformation());
        assertNotNull(mapsActivity.getItineraryInformation());
        assertNotNull(mapsActivity.getItineraryInformation());

    }

    @Test
    public void testZoomIn()
    {
        float lastZoom = mapsActivity.getZoom();
        onView(withId(R.id.zoomin)).perform(click());
        assertEquals(mapsActivity.getZoom(), lastZoom + 1);
    }

    @Test
    public void testZoomOut()
    {
        float lastZoom = mapsActivity.getZoom();
        onView(withId(R.id.zoomout)).perform(click());

        assertEquals(mapsActivity.getZoom(), lastZoom - 1);
    }

    @Test
    public void testGeoloc()
    {
        onView(withId(R.id.button_geoloc)).perform(click());
    }

    @Test
    public void testMapType()
    {
        onView(withId(R.id.map_type_button)).perform(click());
        onView(withId(R.id.checkbox_hybrid_type)).perform(click());
        onView(withId(R.id.button_ok)).perform(click());
        assertEquals(GoogleMap.MAP_TYPE_HYBRID, mapsActivity.getMapType());

    }

    @After
    public void tearDown() throws Exception
    {
        super.tearDown();
    }

}