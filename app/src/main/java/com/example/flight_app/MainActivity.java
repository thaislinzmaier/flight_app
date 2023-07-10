package com.example.flight_app;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;


import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;

import androidx.core.app.ActivityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.flight_app.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONObject;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;
public class MainActivity extends AppCompatActivity{

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    private BDSQLiteHelper bd;
    ArrayList<Flights> listaVoos;

    private TextView resultTextView;

    private Button btnGps;
    private Button btnMostraVoo;

    private Button btnMostraVoosSalvos;
    private TextView txtLatitude, txtLongitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bd = new BDSQLiteHelper(this);

        txtLatitude = (TextView) findViewById(R.id.txtLatitude);
        txtLongitude = (TextView) findViewById(R.id.txtLongitude);

        btnMostraVoosSalvos = (Button) findViewById(R.id.btnMostraVoosSalvos);

        btnMostraVoosSalvos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostraListaVoosSalvos();
            }
        });

        btnMostraVoo = (Button) findViewById(R.id.btnMostraVoos);
        btnMostraVoo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostraListaVoos();
            }
        });

        btnGps = (Button) findViewById(R.id.btnGps);
        btnGps.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                pedirPermissoes();
            }
        });

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        resultTextView = findViewById(R.id.resultTextView);

        setSupportActionBar(binding.toolbar);
    }

    private void pedirPermissoes() {

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        else
            configurarServico();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    configurarServico();
                } else {
                    Toast.makeText(this, "Não vai funcionar!!!", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    public void configurarServico(){
        try {
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            LocationListener locationListener = new LocationListener() {
                public void onLocationChanged(Location location) {
                    atualizar(location);
                }

                public void onStatusChanged(String provider, int status, Bundle extras) { }

                public void onProviderEnabled(String provider) { }

                public void onProviderDisabled(String provider) { }
            };
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }catch(SecurityException ex){
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void atualizar(Location location)
    {
        Double latPoint = location.getLatitude();
        Double lngPoint = location.getLongitude();

        txtLatitude.setText(latPoint.toString());
        txtLongitude.setText(lngPoint.toString());
    }

    @Override
    protected void onStart() {
        super.onStart();

    }
    public void mostraListaVoosSalvos(){
        ArrayList<Flights> savedFlights = bd.getAllVoos();
        for (Flights flight : savedFlights) {
            Log.d("TAG", "ID: " + flight.getId() + ", Aeroporto Chegada: " + flight.getAeroportoChegada());
        }

    }

    public void mostraListaVoos(){

        new FetchFlightsTask().execute();

        ListView lista = (ListView) findViewById(R.id.lvVoos);

        listaVoos = bd.getAllVoos();

        FlightsAdapter adapter = new FlightsAdapter(this, listaVoos);
            lista.setAdapter(adapter);
            lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(MainActivity.this, EditarVooActivity.class);
                    intent.putExtra("ID", listaVoos.get(position).getId());
                    startActivity(intent);
                }
            });
    }

    private ArrayList<Flights> processJsonResponse(String jsonData) {
        ArrayList<Flights> flights = new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            JSONArray flightsArray = jsonObject.getJSONArray("flights");

            for (int i = 0; i < flightsArray.length(); i++) {
                JSONObject flightObject = flightsArray.getJSONObject(i);

                // Extrair os dados relevantes do objeto JSON e criar um objeto Flight
                String linhaAerea = flightObject.getString("airline");
                String voo = flightObject.getString("flight");
                String aeroportoPartida = flightObject.getString("departure");
                String aeroportoChegada = flightObject.getString("arrival");
                String latitude = flightObject.getString("latitude");
                String longitude = flightObject.getString("longitude");

                Flights flight = new Flights(linhaAerea, voo, aeroportoPartida, aeroportoChegada, latitude, longitude);
                flights.add(flight);
            }
        } catch (JSONException e) {
            Log.e("TAG", "Erro ao processar JSON: " + e.getMessage());
        }

        return flights;
    }

    private void saveFlightsToDatabase(ArrayList<Flights> flights) {
        for (Flights flight : flights) {
            bd.addVoo(flight);
        }
    }

    private class FetchFlightsTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url("https://flight-data4.p.rapidapi.com/get_area_flights?latitude=1.367540&longitude=103.816089")
                    .get()
                    .addHeader("X-RapidAPI-Key", "db5bf93edemsh10c1409880f2a75p167ba3jsn6aef04e5f721")
                    .addHeader("X-RapidAPI-Host", "flight-data4.p.rapidapi.com")
                    .build();

            try {
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    String resultados = response.body().string();
                    ArrayList<Flights> flights = processJsonResponse(resultados);
                    saveFlightsToDatabase(flights);
                    return resultados;

                } else {
                    Log.e("TAG", "Erro na solicitação: " + response.code() + " - " + response.message());
                }
            } catch (IOException e) {
                Log.e("TAG", "Erro na solicitação: " + e.getMessage());
            }

            return null;
        }



        @Override
        protected void onPostExecute(String resultados) {
            if (resultados != null) {
                Log.d("TAG", "Resultado da chamada à API: " + resultados);
                resultTextView.setText(resultados);
                // Processar e exibir os dados da resposta da API
            } else {
                // Tratar caso ocorra um erro na chamada à API
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}