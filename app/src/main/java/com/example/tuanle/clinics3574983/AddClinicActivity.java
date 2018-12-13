package com.example.tuanle.clinics3574983;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Objects;

public class AddClinicActivity extends AppCompatActivity {
    private Clinic clinic;
    EditText nameText;
    EditText addressText;
    EditText ratingText;
    EditText latitudeText;
    EditText longitudeText;
    EditText impressionText;
    EditText leadPhysicianText;
    EditText specializationText;
    EditText averagePriceText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_clinic);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        clinic = new Clinic();
        nameText = findViewById(R.id.nameClinic);
        addressText = findViewById(R.id.addressClinic);
        ratingText = findViewById(R.id.ratingClinic);
        latitudeText = findViewById(R.id.latitudeClinic);
        longitudeText = findViewById(R.id.longitudeClinic);
        impressionText = findViewById(R.id.impressionClinic);
        leadPhysicianText = findViewById(R.id.leadPhysicianClinic);
        specializationText = findViewById(R.id.specializationClinic);
        averagePriceText = findViewById(R.id.averagePriceClinic);
        latitudeText.setText(getIntent().getDoubleExtra("lat", 0) + "");
        longitudeText.setText(getIntent().getDoubleExtra("long", 0) + "");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }

    public void onCreateClicked(View view) {
        if (nameText.getText().toString().equals("") || addressText.getText().toString().equals("")
                || ratingText.getText().toString().equals("") || latitudeText.getText().toString().equals("")
                || longitudeText.getText().toString().equals("") || impressionText.getText().toString().equals("")
                || leadPhysicianText.getText().toString().equals("") || specializationText.getText().toString().equals("")
                || averagePriceText.getText().toString().equals("")) {
            Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
        } else {
            clinic.name = nameText.getText().toString();
            clinic.address = addressText.getText().toString();
            clinic.rating = Integer.parseInt(ratingText.getText().toString());
            clinic.latitude = Double.parseDouble(latitudeText.getText().toString());
            clinic.longitude = Double.parseDouble(longitudeText.getText().toString());
            clinic.impression = impressionText.getText().toString();
            clinic.leadPhysician = leadPhysicianText.getText().toString();
            clinic.specialization = specializationText.getText().toString();
            clinic.averagePrice = Integer.parseInt(averagePriceText.getText().toString());
            new PostClinic().execute();
        }
    }

    public void onCancelClicked(View view) {
        finish();
    }

    private class PostClinic extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            HttpHandler.postRequest(MapsActivity.MAP_API, clinic);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            finish();
        }
    }
}
