package com.example.android.parkhere;


import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.Arrays;
import java.util.List;


public class MapSearchPage extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnMarkerClickListener,
        GoogleMap.OnMapLongClickListener
       // GoogleMap.OnPolylineClickListener
  {

        private GoogleMap map;
        private GoogleApiClient googleApiClient;
        private Location currentLocation;



        @Override
        protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_map_search_page);

                SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.map);
                mapFragment.getMapAsync(this);

                googleApiClient = new GoogleApiClient.Builder(this)
                        .addConnectionCallbacks(this)
                        .addOnConnectionFailedListener(this)
                        .addApi(LocationServices.API)
                        .build();
        }

        @Override
        public void onConnected(@Nullable Bundle bundle) {
                if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION )
                        != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(MapSearchPage.this, "Please allow ACCESS_COARSE_LOCATION permission.",
                                Toast.LENGTH_LONG).show();
                        return;
                }

                currentLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

                map.setMyLocationEnabled(true);
                map.getUiSettings().setZoomControlsEnabled(true);
                map.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
                        @Override
                        public boolean onMyLocationButtonClick() {
                                moveToMyLocation();
                                return false;
                        }
                });

                moveToMyLocation();
        }

        @Override
        public void onConnectionSuspended(int i) {}

        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {}

        @Override
        public void onMapLongClick(LatLng latLng) {
                MarkerOptions options = new MarkerOptions().position(latLng);
                options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                options.title("Parking Space Status");
                options.snippet("Red Unavailable !");
                map.addMarker(options);
        }


        @Override
        public void onMapReady(GoogleMap googleMap) {
                map = googleMap;
                map.getUiSettings().setMyLocationButtonEnabled(true);
                map.getUiSettings().setZoomControlsEnabled(true);

               LatLng latLng = new LatLng(40.7484, -73.9857);

                // add a marker to current location and move the camera
                MarkerOptions marker = new MarkerOptions()
                        .position(latLng)
                        .title("Current Location")
                        .snippet("This is me !!!")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                map.addMarker(marker);

            Circle circle = map.addCircle(new CircleOptions()
                    .center(marker.getPosition())
                    .radius(100)
                    .strokeColor(Color.RED)
                    //.fillColor(Color.GREEN)
            );


                map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14));

                map.setOnMarkerClickListener(this);
                map.setOnMapLongClickListener(this);
        }



        @Override
        public boolean onMarkerClick(Marker marker) {

            double longitude = marker.getPosition().longitude;
            double latitude = marker.getPosition().latitude;
            Polyline polyline1 = map.addPolyline((new PolylineOptions())
                    .clickable(true)
                    .add(new LatLng(40.7484, -73.9857),
                            new LatLng(latitude, longitude))
                    .color(Color.BLUE)
            );

                if (marker.getTitle().equals("Parking Space Status")) {

                        if (marker.getSnippet().equals("Red Unavailable !")){
                                marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                                marker.setSnippet("Space Available !");
                                marker.showInfoWindow();
                        }
                        else if (marker.getSnippet().equals("Space Available !")){
                            polyline1.setVisible(false);
                            marker.remove();
                        }

                }
                else
                        marker.showInfoWindow();

                return true;
        }


        @Override
        protected void onStart() {
                super.onStart();
                googleApiClient.connect();
        }

        @Override
        protected void onStop() {
                super.onStop();

                if( googleApiClient != null && googleApiClient.isConnected() ) {
                        googleApiClient.disconnect();
                }
        }

        public void moveToMyLocation() {


                if (currentLocation != null) {
                       // LatLng latLng = new LatLng(49.2400, 28.4811);
                       // map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                       // map.animateCamera(CameraUpdateFactory.zoomTo(5));
                        CameraPosition position = CameraPosition.builder()
                                .target(new LatLng(currentLocation.getLatitude(),
                                        currentLocation.getLongitude()))
                                .zoom(14)
                                .build();

                        map.animateCamera(CameraUpdateFactory.newCameraPosition(position), null);

                } else {
                        Toast.makeText(this, "Can not get user location!", Toast.LENGTH_LONG).show();
                }
        }

        public boolean onCreateOptionsMenu(Menu menu) {
                MenuInflater inflater = getMenuInflater();
                inflater.inflate(R.menu.menu_layout, menu);
                return true;
                // return super.onCreateOptionsMenu(menu);
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
                // if (item.getItemId() == R.id.menu_item_id) {
                //  Toast.makeText(getApplicationContext(), "Helllooo!", Toast.LENGTH_SHORT).show();
                //    return true;
                // }
                if (item.getItemId() == R.id.logout) {
                        Intent intent = new Intent(this, LoginPage.class);
                        startActivity(intent);

                        return  true;
                }
                return super.onOptionsItemSelected(item);
        }


}