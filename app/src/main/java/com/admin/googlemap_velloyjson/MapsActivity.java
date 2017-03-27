package com.admin.googlemap_velloyjson;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private String TAG = "JSONParking";
    private Spinner mSpnLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

       initView();

    }

    private void initView() {

        mSpnLocation = (Spinner)this.findViewById(R.id.spnLocation);
        mSpnLocation.setOnItemSelectedListener(spnLocationOnItemSelected);
    }

    private AdapterView.OnItemSelectedListener spnLocationOnItemSelected =
            new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            };

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        getData();

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(25.0339640,121.5644720);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));


    }
    private void getData() {
        final String urlParkingArea = "http://data.taipei/opendata/datalist/apiAccess?scope=resourceAquire&rid=a880adf3-d574-430a-8e29-3192a41897a5";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                urlParkingArea,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "response = " + response.toString());
                        parserJson(response);
                        //updateparserJson();

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "error : " + error.toString());
                    }
                }
        );
        Volley.newRequestQueue(this).add(jsonObjectRequest);

    }
    private void parserJson(JSONObject jsonObject) {
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this,android.R.layout.simple_spinner_item);

        try {
            JSONArray data = jsonObject.getJSONObject("result").getJSONArray("results");
            for (int i = 0; i < data.length(); i++) {
                JSONObject o = data.getJSONObject(i);
                arrayAdapter.add(o.getString("停車場名稱"));

                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(o.getDouble("緯度(WGS84)"), o.getDouble("經度(WGS84)")))
                        .title(o.getString("停車場名稱"))
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.parking01))

                );

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpnLocation.setAdapter(arrayAdapter);
    }
    private void updateparserJson() {

        try {
            JSONObject jsonObject = new JSONObject();
            JSONArray data = jsonObject.getJSONObject("result").getJSONArray("results");
            for (int i = 0; i < data.length(); i++) {
                JSONObject o = data.getJSONObject(i);
              Double mLat =  o.getDouble("緯度(WGS84)");
                Double mLng = o.getDouble("經度(WGS84)");
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(
              mLat,mLng ),14));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}
