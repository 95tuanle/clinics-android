package com.example.tuanle.clinics3574983;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    public static final String MAP_API = "https://clinicandroidasn2v2.herokuapp.com/clinics";
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private List<String> ratings;
    private List<String> specializations;
    private List<String> filteredRatings;
    private List<String> filteredSpecializations;
    private boolean filteringRating = false;
    private boolean filteringSpecialization = false;

//    private static final long UPDATE_INTERVAL = 10000;
//    private static final long FASTEST_INTERVAL = 2000;
//    private LocationRequest locationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MapsActivity.this);


        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Intent intent = new Intent(MapsActivity.this, AddClinicActivity.class);
                intent.putExtra("lat", latLng.latitude);
                intent.putExtra("long", latLng.longitude);
                startActivity(intent);
            }
        });
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (marker.getTag() != null) {
                    Intent intent = new Intent(MapsActivity.this, UpdateDeleteClinicActivity.class);
                    intent.putExtra("JSONString", marker.getTag().toString());
                    startActivity(intent);
                }
                return false;
            }
        });


        requestLocationPermission();
//        startLocationUpdate();
    }

//    @SuppressLint({"RestrictedApi", "MissingPermission"})
//    private void startLocationUpdate() {
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
//                == PackageManager.PERMISSION_GRANTED) {
//            locationRequest = new LocationRequest();
//            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//            locationRequest.setInterval(UPDATE_INTERVAL);
//            locationRequest.setFastestInterval(FASTEST_INTERVAL);
//            fusedLocationProviderClient.requestLocationUpdates(locationRequest, new LocationCallback() {
//                @Override
//                public void onLocationResult(LocationResult locationResult) {
//                    super.onLocationResult(locationResult);
//                    mMap.clear();
//                    LatLng latLng = new LatLng(locationResult.getLastLocation().getLatitude(), locationResult.getLastLocation().getLongitude());
//                    mMap.addMarker(new MarkerOptions().position(latLng).title("You are here"));
//                    new GetClinics().execute();
//                }
//            }, null);
//        } else {
//            requestLocationPermission();
//        }
//    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mMap != null) {
            mMap.clear();
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                            mMap.addMarker(new MarkerOptions().position(latLng).title("You are here"));
                        }
                    }
                });
            } else {
                requestLocationPermission();
            }
        }
        ratings = new ArrayList<>();
        specializations = new ArrayList<>();
        new GetClinics().execute();
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 101);
    }

    public void getCurrentLocationClicked(View view) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    mMap.clear();
                    if (location != null) {
                        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                        mMap.addMarker(new MarkerOptions().position(latLng).title("You are here"));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, (float) 15.0));
                    } else {
                        Toast.makeText(MapsActivity.this, "Fetching location, please wait and try again", Toast.LENGTH_SHORT).show();
                    }
                    new GetClinics().execute();
                }
            });
        } else {
            requestLocationPermission();
        }
    }

    public void listViewClicked(View view) {
        Intent intent = new Intent(MapsActivity.this, ListClinicsActivity.class);
        startActivity(intent);
    }

    public void onFilterClicked(View view) {
        AlertDialog.Builder mainBuilder = new AlertDialog.Builder(MapsActivity.this);
        mainBuilder.setTitle("Pick a type or types of filter");
        mainBuilder.setNegativeButton("Rating", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
                builder.setTitle("Pick rating filter");
                if (filteredRatings == null) {
                    filteredRatings = new ArrayList<>();
                }

                final String[] ratingArray = ratings.toArray(new String[ratings.size()]);
                boolean[] checkedRatings = new boolean[ratingArray.length];

                for (int i = 0; i < ratingArray.length; i++) {
                    if (filteredRatings.contains(ratingArray[i])) {
                        checkedRatings[i] = true;
                    }

                }
                builder.setMultiChoiceItems(ratingArray, checkedRatings, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        String rating = ratingArray[which];
                        if (isChecked) {
                            if (!filteredRatings.contains(rating)) {
                                filteredRatings.add(rating);
                            }
                        } else if (filteredRatings.contains(rating)) {
                            filteredRatings.remove(rating);
                        }
                    }
                });
                builder.setPositiveButton("Filter", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK button
                        if (filteredRatings.size() == 0) {
                            ratings = new ArrayList<>();
                            filteredRatings = new ArrayList<>();
                            filteringRating = false;
                        } else {
                            filteringRating = true;
                        }
                        mMap.clear();
                        if (ContextCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                                == PackageManager.PERMISSION_GRANTED) {
                            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                                @Override
                                public void onSuccess(Location location) {
                                    if (location != null) {
                                        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                                        mMap.addMarker(new MarkerOptions().position(latLng).title("You are here"));
                                    }
                                }
                            });
                        } else {
                            requestLocationPermission();
                        }
                        new GetClinics().execute();
                    }
                });
                builder.setNegativeButton("Clear filter", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        ratings = new ArrayList<>();
                        filteredRatings = new ArrayList<>();
                        filteringRating = false;
                        mMap.clear();
                        if (ContextCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                                == PackageManager.PERMISSION_GRANTED) {
                            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                                @Override
                                public void onSuccess(Location location) {
                                    if (location != null) {
                                        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                                        mMap.addMarker(new MarkerOptions().position(latLng).title("You are here"));
                                    }
                                }
                            });
                        } else {
                            requestLocationPermission();
                        }
                        new GetClinics().execute();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
        mainBuilder.setPositiveButton("Specialization", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
                builder.setTitle("Pick specialization filter");
                if (filteredSpecializations == null) {
                    filteredSpecializations = new ArrayList<>();
                }

                final String[] specializationArray = specializations.toArray(new String[specializations.size()]);
                boolean[] checkedSpecializations = new boolean[specializationArray.length];

                for (int i = 0; i < specializationArray.length; i++) {
                    if (filteredSpecializations.contains(specializationArray[i])) {
                        checkedSpecializations[i] = true;
                    }

                }
                builder.setMultiChoiceItems(specializationArray, checkedSpecializations, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        String specialization = specializationArray[which];
                        if (isChecked) {
                            if (!filteredSpecializations.contains(specialization)) {
                                filteredSpecializations.add(specialization);
                            }
                        } else if (filteredSpecializations.contains(specialization)) {
                            filteredSpecializations.remove(specialization);
                        }
                    }
                });
                builder.setPositiveButton("Filter", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK button
                        if (filteredSpecializations.size() == 0) {
                            ratings = new ArrayList<>();
                            filteredRatings = new ArrayList<>();
                            filteringSpecialization = false;
                        } else {
                            filteringSpecialization = true;
                        }
                        mMap.clear();
                        if (ContextCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                                == PackageManager.PERMISSION_GRANTED) {
                            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                                @Override
                                public void onSuccess(Location location) {
                                    if (location != null) {
                                        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                                        mMap.addMarker(new MarkerOptions().position(latLng).title("You are here"));
                                    }
                                }
                            });
                        } else {
                            requestLocationPermission();
                        }
                        new GetClinics().execute();

                    }
                });
                builder.setNegativeButton("Clear filter", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        specializations = new ArrayList<>();
                        filteredSpecializations = new ArrayList<>();
                        filteringSpecialization = false;
                        mMap.clear();
                        if (ContextCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                                == PackageManager.PERMISSION_GRANTED) {
                            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                                @Override
                                public void onSuccess(Location location) {
                                    if (location != null) {
                                        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                                        mMap.addMarker(new MarkerOptions().position(latLng).title("You are here"));
                                    }
                                }
                            });
                        } else {
                            requestLocationPermission();
                        }
                        new GetClinics().execute();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
        AlertDialog alertDialog = mainBuilder.create();
        alertDialog.show();
    }

    @SuppressLint("StaticFieldLeak")
    private class GetClinics extends AsyncTask<Void, Void, Void> {
        String jsonString;
        @Override
        protected Void doInBackground(Void... voids) {
            jsonString = HttpHandler.getRequest(MAP_API);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            try {
                JSONArray jsonArray = new JSONArray(jsonString);
                BitmapDrawable bitmapDrawable = (BitmapDrawable) getResources().getDrawable(R.drawable.clinic);
                Bitmap rawBitmap = bitmapDrawable.getBitmap();
                Bitmap resizedBitmap = Bitmap.createScaledBitmap(rawBitmap, 100, 100, false);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    if (filteringRating && filteringSpecialization) {
                        if (filteredRatings.contains(jsonObject.getString("rating")) && filteredSpecializations.contains(jsonObject.getString("specialization"))) {
                            Marker marker = mMap.addMarker(new MarkerOptions().position(new LatLng(jsonObject.getDouble("latitude")
                                    , jsonObject.getDouble("longitude"))).icon(BitmapDescriptorFactory.fromBitmap(resizedBitmap))
                                    .title(jsonObject.getString("name")).snippet("clinic"));
                            marker.setTag(jsonObject);
                        }
                    } else if (filteringRating) {
                        if (filteredRatings.contains(jsonObject.getString("rating"))) {
                            Marker marker = mMap.addMarker(new MarkerOptions().position(new LatLng(jsonObject.getDouble("latitude")
                                    , jsonObject.getDouble("longitude"))).icon(BitmapDescriptorFactory.fromBitmap(resizedBitmap))
                                    .title(jsonObject.getString("name")).snippet("clinic"));
                            marker.setTag(jsonObject);
                        }
                    } else if (filteringSpecialization) {
                        if (filteredSpecializations.contains(jsonObject.getString("specialization"))) {
                            Marker marker = mMap.addMarker(new MarkerOptions().position(new LatLng(jsonObject.getDouble("latitude")
                                    , jsonObject.getDouble("longitude"))).icon(BitmapDescriptorFactory.fromBitmap(resizedBitmap))
                                    .title(jsonObject.getString("name")).snippet("clinic"));
                            marker.setTag(jsonObject);
                        }
                    } else {
                        Marker marker = mMap.addMarker(new MarkerOptions().position(new LatLng(jsonObject.getDouble("latitude")
                                , jsonObject.getDouble("longitude"))).icon(BitmapDescriptorFactory.fromBitmap(resizedBitmap))
                                .title(jsonObject.getString("name")).snippet("clinic"));
                        marker.setTag(jsonObject);
                    }

                    String rating = jsonObject.getString("rating");
                    if (!ratings.contains(rating)) {
                        ratings.add(rating);
                    }
                    String specialization = jsonObject.getString("specialization");
                    if (!specializations.contains(specialization)) {
                        specializations.add(specialization);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
