package com.fun_picks.fpphoto;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;


public class MapActivity extends FragmentActivity implements OnMapReadyCallback {
    public static final String MAP_PARAMETER_LONGITUDE = "MAP_PARAMETER_LONGITUDE";
    public static final String MAP_PARAMETER_LATITUDE = "MAP_PARAMETER_LATITUDE";
    final private static int TIP_MAP_ZOOM = 15;  // between 2 and 21, increase to zoom in
    final private static int TIP_MAP_ANIMATION_DURATION = 2000;
    float latitude;
    float longitude;
    GoogleMap googleMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_map);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            latitude = extras.getFloat(MAP_PARAMETER_LATITUDE);
            longitude = extras.getFloat(MAP_PARAMETER_LONGITUDE);
        }

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.tip_map);
        mapFragment.getMapAsync(this);
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     */
    @Override
    public void onMapReady(GoogleMap map) {
       //map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 4));
        googleMap =  map;
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), TIP_MAP_ZOOM),
                TIP_MAP_ANIMATION_DURATION,
                new GoogleMap.CancelableCallback() {

                    @Override
                    public void onFinish() {
                    }
                    @Override
                    public void onCancel() {
                    }
                }
        );
        googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        googleMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title("Camera Location"));
    }


}