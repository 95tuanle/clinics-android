package com.example.tuanle.clinics3574983;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ListClinicsActivity extends AppCompatActivity {

    ListView clinicsList;

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
        new GetClinics().execute();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
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
                    jsonObjectsList.add(jsonArray.getJSONObject(i));
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
