package com.example.formation.devryweremaps;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by formation on 10/11/15.
 */
public class ItineraryInformation implements Parcelable {

    public static final String ITINERARY_INFORMATION = "itinerary_information";

    private LatLng mMyLatLng = null;

    private LatLng mDepartureLatLng = null;

    private LatLng mArrivalLatLng = null;

    public void setMyLatLng(LatLng mMyLatLng)
    {
        this.mMyLatLng = mMyLatLng;
    }

    public void setDepartureLatLng(LatLng mDepartureLatLng)
    {
        this.mDepartureLatLng = mDepartureLatLng;
    }

    public void setArrivalLatLng(LatLng mArrivalLatLng)
    {
        this.mArrivalLatLng = mArrivalLatLng;
    }

    public LatLng getMyLatLng()
    {
        return mMyLatLng;
    }

    public LatLng getDepartureLatLng()
    {
        return mDepartureLatLng;
    }

    public LatLng getArrivalLatLng()
    {
        return mArrivalLatLng;
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeParcelable(this.mMyLatLng, 0);
        dest.writeParcelable(this.mDepartureLatLng, 0);
        dest.writeParcelable(this.mArrivalLatLng, 0);
    }

    public ItineraryInformation()
    {
    }

    protected ItineraryInformation(Parcel in)
    {
        this.mMyLatLng = in.readParcelable(LatLng.class.getClassLoader());
        this.mDepartureLatLng = in.readParcelable(LatLng.class.getClassLoader());
        this.mArrivalLatLng = in.readParcelable(LatLng.class.getClassLoader());
    }

    public static final Parcelable.Creator<ItineraryInformation> CREATOR = new Parcelable.Creator<ItineraryInformation>() {
        public ItineraryInformation createFromParcel(Parcel source)
        {
            return new ItineraryInformation(source);
        }

        public ItineraryInformation[] newArray(int size)
        {
            return new ItineraryInformation[size];
        }
    };
}
