package com.example.tuanle.clinics3574983;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ListClinicsActivity extends AppCompatActivity {

    ListView clinicsList;

    private List<String> ratings;
    private List<String> specializations;
    private List<String> filteredRatings;
    private List<String> filteredSpecializations;
    private boolean filteringRating = false;
    private boolean filteringSpecialization = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_clinics);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        clinicsList = findViewById(R.id.clinicsList);

    }

    @Override
    protected void onResume() {
        super.onResume();
        ratings = new ArrayList<>();
        specializations = new ArrayList<>();
        new GetClinics().execute();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }

    public void onFilterClicked(View view) {
        AlertDialog.Builder mainBuilder = new AlertDialog.Builder(ListClinicsActivity.this);
        mainBuilder.setTitle("Pick a type or types of filter");
        mainBuilder.setNegativeButton("Rating", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ListClinicsActivity.this);
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
                        new GetClinics().execute();
                    }
                });
                builder.setNegativeButton("Clear filter", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        ratings = new ArrayList<>();
                        filteredRatings = new ArrayList<>();
                        filteringRating = false;
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
                AlertDialog.Builder builder = new AlertDialog.Builder(ListClinicsActivity.this);
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
                            specializations = new ArrayList<>();
                            filteredRatings = new ArrayList<>();
                            filteringSpecialization = false;
                        } else {
                            filteringSpecialization = true;
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

    private class GetClinics extends AsyncTask<Void, Void, Void> {
        String jsonString;
        @Override
        protected Void doInBackground(Void... voids) {
            jsonString = HttpHandler.getRequest(MapsActivity.MAP_API);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            try {
                final JSONArray jsonArray = new JSONArray(jsonString);
                final List<JSONObject> jsonObjectsList = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String rating = jsonObject.getString("rating");
                    if (!ratings.contains(rating)) {
                        ratings.add(rating);
                    }
                    String specialization = jsonObject.getString("specialization");
                    if (!specializations.contains(specialization)) {
                        specializations.add(specialization);
                    }
                    if (filteringRating && filteringSpecialization) {
                        if (filteredRatings.contains(jsonObject.getString("rating")) && filteredSpecializations.contains(jsonObject.getString("specialization"))) {
                            jsonObjectsList.add(jsonObject);
                        }
                    } else if (filteringRating) {
                        if (filteredRatings.contains(jsonObject.getString("rating"))) {
                            jsonObjectsList.add(jsonObject);
                        }
                    } else if (filteringSpecialization) {
                        if (filteredSpecializations.contains(jsonObject.getString("specialization"))) {
                            jsonObjectsList.add(jsonObject);
                        }
                    } else {
                        jsonObjectsList.add(jsonObject);
                    }
                }
                ArrayAdapter arrayAdapter = new ArrayAdapter(ListClinicsActivity.this, android.R.layout.simple_list_item_2, android.R.id.text1, jsonObjectsList) {
                    @Override
                    public View getView(int position, View convertView, ViewGroup parent ) {
                        View view = super.getView(position, convertView, parent);
                        TextView text1 = view.findViewById(android.R.id.text1);
                        TextView text2 = view.findViewById(android.R.id.text2);
                        JSONObject jsonObject = jsonObjectsList.get(position);
                        try {
                            text1.setText(jsonObject.getString("name") + " clinic at " + jsonObject.getString("address"));
                            text2.setText("Leading by " + jsonObject.getString("lead_physician")
                                    + ". Specialized at " + jsonObject.getString("specialization").toLowerCase() + ". Rating is " + jsonObject.getString("rating"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        return view;
                    }
                };
                clinicsList.setAdapter(arrayAdapter);
                clinicsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(ListClinicsActivity.this, UpdateDeleteClinicActivity.class);
                        intent.putExtra("JSONString", jsonObjectsList.get(position).toString());
                        startActivity(intent);
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
