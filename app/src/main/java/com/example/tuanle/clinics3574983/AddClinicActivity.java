package com.example.tuanle.clinics3574983;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Objects;

public class AddClinicActivity extends AppCompatActivity {

    private Clinic clinic;
    TextView latitudeText;
    TextView longitudeText;
    EditText nameText;
    EditText addressText;
    EditText ratingText;
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
        latitudeText = findViewById(R.id.latitudeClinicC);
        longitudeText = findViewById(R.id.longitudeClinicC);
        nameText = findViewById(R.id.nameClinicC);
        addressText = findViewById(R.id.addressClinicC);
        ratingText = findViewById(R.id.ratingClinicC);
        impressionText = findViewById(R.id.impressionClinicC);
        leadPhysicianText = findViewById(R.id.leadPhysicianClinicC);
        specializationText = findViewById(R.id.specializationClinicC);
        averagePriceText = findViewById(R.id.averagePriceClinicC);
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
                || ratingText.getText().toString().equals("") || impressionText.getText().toString().equals("")
                || leadPhysicianText.getText().toString().equals("") || specializationText.getText().toString().equals("")
                || averagePriceText.getText().toString().equals("")) {
            Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
        } else {
            int rating = Integer.parseInt(ratingText.getText().toString());
            if (rating < 0 || rating > 10) {
                Toast.makeText(this, "Rating should be in range between 0 and 10", Toast.LENGTH_SHORT).show();
            } else {
                clinic.latitude = Double.parseDouble(latitudeText.getText().toString());
                clinic.longitude = Double.parseDouble(longitudeText.getText().toString());
                clinic.name = nameText.getText().toString();
                clinic.address = addressText.getText().toString();
                clinic.rating = rating;
                clinic.impression = impressionText.getText().toString();
                clinic.leadPhysician = leadPhysicianText.getText().toString();
                clinic.specialization = specializationText.getText().toString();
                clinic.averagePrice = Integer.parseInt(averagePriceText.getText().toString());
                new PostClinic().execute();
            }
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
