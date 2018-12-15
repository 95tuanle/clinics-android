package com.example.tuanle.clinics3574983;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class UpdateDeleteClinicActivity extends AppCompatActivity {

    private Clinic clinic;
    TextView idText;
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
        setContentView(R.layout.activity_update_delete_clinic);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        clinic = new Clinic();
        idText = findViewById(R.id.idClinicRUD);
        latitudeText = findViewById(R.id.latitudeClinicRUD);
        longitudeText = findViewById(R.id.longitudeClinicRUD);
        nameText = findViewById(R.id.nameClinicRUD);
        addressText = findViewById(R.id.addressClinicRUD);
        ratingText = findViewById(R.id.ratingClinicRUD);
        impressionText = findViewById(R.id.impressionClinicRUD);
        leadPhysicianText = findViewById(R.id.leadPhysicianClinicRUD);
        specializationText = findViewById(R.id.specializationClinicRUD);
        averagePriceText = findViewById(R.id.averagePriceClinicRUD);
        try {
            JSONObject jsonObject = new JSONObject(getIntent().getStringExtra("JSONString"));
            idText.setText(jsonObject.getString("_id"));
            latitudeText.setText(jsonObject.getString("latitude"));
            longitudeText.setText(jsonObject.getString("longitude"));
            nameText.setText(jsonObject.getString("name"));
            addressText.setText(jsonObject.getString("address"));
            ratingText.setText(jsonObject.getString("rating"));
            impressionText.setText(jsonObject.getString("impression"));
            leadPhysicianText.setText(jsonObject.getString("lead_physician"));
            specializationText.setText(jsonObject.getString("specialization"));
            averagePriceText.setText(jsonObject.getString("average_price"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }

    public void onUpdateClicked(View view) {
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
                new PutClinic().execute();
            }
        }
    }

    public void onDeleteClicked(View view) {
        new DeleteClinic().execute();
    }

    public void onCancelClicked(View view) {
        finish();
    }

    private class PutClinic extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            HttpHandler.putRequest(MapsActivity.MAP_API, clinic, (String) idText.getText());
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            finish();
        }
    }

    private class DeleteClinic extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            HttpHandler.deleteRequest(MapsActivity.MAP_API, (String) idText.getText());
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            finish();
        }
    }
}
