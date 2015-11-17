package com.example.formation.devryweremaps;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import com.google.android.gms.maps.GoogleMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by formation on 13/11/15.
 */
public class MapTypeActivity extends Activity {

    public final static String Map_Type = "Map_Type";
    private int mMapTypes;


    @Bind(R.id.checkbox_normal_type)
    CheckBox mNormalType;

    @Bind(R.id.checkbox_hybrid_type)
    CheckBox mHybridType;

    @Bind(R.id.checkbox_satellite_type)
    CheckBox mSatelliteType;

    @Bind(R.id.checkbox_terrain_type)
    CheckBox mTerrainType;

    @Bind(R.id.button_ok)
    Button mButtonOk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_type);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.button_ok)
    public void onButtonOkClicked() {
        Intent intent = new Intent();
        intent.putExtra(Map_Type, mMapTypes);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    public void onCheckboxClicked(View view) {

        boolean checked = ((CheckBox) view).isChecked();
        switch (view.getId()) {
            case R.id.checkbox_normal_type:
                if (checked) {
                    mMapTypes = GoogleMap.MAP_TYPE_NORMAL;
                }
            case R.id.checkbox_hybrid_type:
                if (checked) {
                    mMapTypes = GoogleMap.MAP_TYPE_HYBRID;
                }
                break;
            case R.id.checkbox_satellite_type:
                if (checked) {
                    mMapTypes = GoogleMap.MAP_TYPE_SATELLITE;
                }
                break;
            case R.id.checkbox_terrain_type:
                if (checked) {
                    mMapTypes = GoogleMap.MAP_TYPE_TERRAIN;
                }
                break;
            default:
                mMapTypes = GoogleMap.MAP_TYPE_NORMAL;
        }
    }
}
