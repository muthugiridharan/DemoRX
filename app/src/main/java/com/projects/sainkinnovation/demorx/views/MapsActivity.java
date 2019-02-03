package com.projects.sainkinnovation.demorx.views;

import android.Manifest;
import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.SquareCap;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.projects.sainkinnovation.demorx.R;
import com.projects.sainkinnovation.demorx.network.NetworkInterface;
import com.projects.sainkinnovation.demorx.utlity.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.google.android.gms.maps.model.JointType.ROUND;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLoadedCallback,
        GoogleMap.OnCameraIdleListener, GoogleMap.OnCameraMoveListener {

    private GoogleMap mMap;
    private TextView tvAddress;
    private LatLng currentLatlng;
    private List<LatLng> listLatLng = new ArrayList<>();
    private Polyline blackPolyLine, greyPolyLine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        tvAddress=findViewById(R.id.tvAddress);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        Objects.requireNonNull(mapFragment).getMapAsync(this);
    }


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
        mMap.setOnMapLoadedCallback(this);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.setOnCameraIdleListener(this);
        mMap.setOnCameraMoveListener(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        setAction();
    }

    void setAction(){
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                mMap.clear();
                listLatLng.clear();
                mMap.addMarker(new MarkerOptions().position(point));
                drawPolyline(point);
            }
        });
    }

    private void drawPolyline(LatLng destination) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com/maps/api/directions/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        NetworkInterface networkInterface=retrofit.create(NetworkInterface.class);
        networkInterface.getPolylineData(String.valueOf(currentLatlng.latitude+","+currentLatlng.longitude),String.valueOf(destination.latitude+","+destination.longitude))
        .enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                Log.i("Map", "onResponse: "+response);
                JsonObject gson = new JsonParser().parse(response.body().toString()).getAsJsonObject();
                try {

                    Single.just(parse(new JSONObject(gson.toString())))
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Consumer<List<List<HashMap<String, String>>>>() {
                                @Override
                                public void accept(List<List<HashMap<String, String>>> lists) throws Exception {
                                    Log.i("Map", "working");
                                    createPolyline(lists);
                                }
                            });

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {

            }
        });
    }

    void createPolyline(List<List<HashMap<String, String>>> result){
        ArrayList<LatLng> points = null;
        PolylineOptions lineOptions = new PolylineOptions();;

        // Traversing through all the routes
        for (int i = 0; i < result.size(); i++) {
            points = new ArrayList<LatLng>();
           // lineOptions = new PolylineOptions();

            // Fetching i-th route
            List<HashMap<String, String>> path = result.get(i);

            // Fetching all the points in i-th route
            for (int j = 0; j < path.size(); j++) {
                HashMap<String, String> point = path.get(j);

                double lat = Double.parseDouble(point.get("lat"));
                double lng = Double.parseDouble(point.get("lng"));
                LatLng position = new LatLng(lat, lng);

                points.add(position);
            }

            listLatLng.addAll(points);
        }

        lineOptions.width(10);
        lineOptions.color(Color.BLACK);
        lineOptions.startCap(new SquareCap());
        lineOptions.endCap(new SquareCap());
        lineOptions.jointType(ROUND);
        blackPolyLine = mMap.addPolyline(lineOptions);

        PolylineOptions greyOptions = new PolylineOptions();
        greyOptions.width(10);
        greyOptions.color(Color.GRAY);
        greyOptions.startCap(new SquareCap());
        greyOptions.endCap(new SquareCap());
        greyOptions.jointType(ROUND);
        greyPolyLine = mMap.addPolyline(greyOptions);

        animatePolyLine();
    }

    private void animatePolyLine() {
        ValueAnimator animator = ValueAnimator.ofInt(0, 100);
        animator.setDuration(1000);
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                Log.i("map", "onAnimationUpdate: working");
                List<LatLng> latLngList = blackPolyLine.getPoints();
                int initialPointSize = latLngList.size();
                int animatedValue = (int) animator.getAnimatedValue();
                int newPoints = (animatedValue * listLatLng.size()) / 100;

                if (initialPointSize < newPoints ) {
                    latLngList.addAll(listLatLng.subList(initialPointSize, newPoints));
                    blackPolyLine.setPoints(latLngList);
                }


            }
        });

        animator.addListener(polyLineAnimationListener);
        animator.start();

    }

    Animator.AnimatorListener polyLineAnimationListener = new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animator) {

           // addMarker(listLatLng.get(listLatLng.size()-1));
        }

        @Override
        public void onAnimationEnd(Animator animator) {

            List<LatLng> blackLatLng = blackPolyLine.getPoints();
            List<LatLng> greyLatLng = greyPolyLine.getPoints();

            greyLatLng.clear();
            greyLatLng.addAll(blackLatLng);
            blackLatLng.clear();

            blackPolyLine.setPoints(blackLatLng);
            greyPolyLine.setPoints(greyLatLng);

            blackPolyLine.setZIndex(2);

            animator.start();
        }

        @Override
        public void onAnimationCancel(Animator animator) {

        }

        @Override
        public void onAnimationRepeat(Animator animator) {


        }
    };

    public List<List<HashMap<String, String>>> parse(JSONObject jObject) {

        List<List<HashMap<String, String>>> routes = new ArrayList<List<HashMap<String, String>>>();
        JSONArray jRoutes = null;
        JSONArray jLegs = null;
        JSONArray jSteps = null;

        try {

            jRoutes = jObject.getJSONArray("routes");

            /** Traversing all routes */
            for (int i = 0; i < jRoutes.length(); i++) {
                jLegs = ((JSONObject) jRoutes.get(i)).getJSONArray("legs");
                List path = new ArrayList<HashMap<String, String>>();

                /** Traversing all legs */
                for (int j = 0; j < jLegs.length(); j++) {
                    jSteps = ((JSONObject) jLegs.get(j)).getJSONArray("steps");

                    /** Traversing all steps */
                    for (int k = 0; k < jSteps.length(); k++) {
                        String polyline = "";
                        polyline = (String) ((JSONObject) ((JSONObject) jSteps.get(k)).get("polyline")).get("points");
                        List<LatLng> list = decodePoly(polyline);

                        /** Traversing all points */
                        for (int l = 0; l < list.size(); l++) {
                            HashMap<String, String> hm = new HashMap<String, String>();
                            hm.put("lat", Double.toString(((LatLng) list.get(l)).latitude));
                            hm.put("lng", Double.toString(((LatLng) list.get(l)).longitude));
                            path.add(hm);
                        }
                    }
                    routes.add(path);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception ignored) {
        }

        return routes;
    }

    private List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }

    private void checkPermission(){
        Dexter.withActivity(this).withPermission(Manifest.permission.ACCESS_FINE_LOCATION).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse response) {
                displayLocationSettingsRequest(getApplicationContext());
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse response) {

            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

            }
        }).check();
    }

    private void displayLocationSettingsRequest(Context context) {
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API).build();
        googleApiClient.connect();

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(10000 / 2);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        //PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        Task<LocationSettingsResponse> result =
                LocationServices.getSettingsClient(this).checkLocationSettings(builder.build());
        result.addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                getLocatonInfo();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                int statusCode = ((ApiException) e).getStatusCode();
                switch (statusCode) {
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            // Show the dialog by calling startResolutionForResult(), and check the result
                            // in onActivityResult().
                            ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                            resolvableApiException.startResolutionForResult(MapsActivity.this, 5);
                        } catch (IntentSender.SendIntentException e1) {
                            Log.e("Map", "PendingIntent unable to execute request."+e1);
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        String errorMessage = "Location settings are inadequate, and cannot be " +
                                "fixed here. Fix in Settings.";
                        Toast.makeText(getApplicationContext(),errorMessage,Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==5){
            switch (resultCode) {
                case Activity.RESULT_OK:
                    Log.i("Map", "User agreed to make required location settings changes.");
                    getLocatonInfo();
                    break;
                case Activity.RESULT_CANCELED:
                    Log.i("Map", "User chose not to make required location settings changes.");
                    break;
            }
        }
    }

    private void getLocatonInfo() {

        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MapsActivity.this);
        try {

            if (Utils.checkLocation(getApplicationContext())) {
                Task location = fusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            final Location currentlocation = (Location) task.getResult();
                            if(currentlocation!=null) {
                                mMap.setMyLocationEnabled(true);
                                currentLatlng=new LatLng(currentlocation.getLatitude(), currentlocation.getLongitude());
                                moveCamera(new LatLng(currentlocation.getLatitude(), currentlocation.getLongitude()));
                            }
                        } else {
                            Log.i("Map", "onComplete: error while geting currentlocation==>" + task.getException());
                        }
                    }
                });
            }

        } catch (SecurityException e) {
            Log.e("Map", "getLocatonInfo: security excaption==>", e.getCause());
        }
    }

    private void moveCamera(LatLng latLng) {
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        CameraUpdate location = CameraUpdateFactory.newLatLngZoom(  latLng, 15f);
        mMap.animateCamera(location);

    }

    @Override
    public void onMapLoaded() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            checkPermission();
        }else{
            displayLocationSettingsRequest(getApplicationContext());
        }
    }

    @Override
    public void onCameraIdle() {
        tvAddress.setVisibility(View.VISIBLE);
        getAddress(mMap.getCameraPosition().target);
    }

    private void getAddress(final LatLng location){
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
           tvAddress.setText(addresses.get(0).getAddressLine(0));
//            cityName=city;
//            getAdminlocation(city,location);
//            String state = addresses.get(0).getAdminArea();
//            String country = addresses.get(0).getCountryName();
//            String postalCode = addresses.get(0).getPostalCode();
//            String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL
        } catch (IOException e) {
            e.printStackTrace();
            tvAddress.setText("No Address found");
        } catch (IndexOutOfBoundsException e){
            e.printStackTrace();
            tvAddress.setText("No Address found");
        }catch (Exception e){
            e.printStackTrace();
            tvAddress.setText("No Address found");
        }
    }

    @Override
    public void onCameraMove() {
        tvAddress.setVisibility(View.GONE);
    }
}
