package com.example.flight_app;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;

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
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONObject;
public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    private BDSQLiteHelper bd;
    ArrayList<Flights> listaVoos;

    private TextView resultTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new FetchFlightsTask().execute();

        bd = new BDSQLiteHelper(this);

        ArrayList<Flights> savedFlights = bd.getAllVoos();
        for (Flights flight : savedFlights) {
            Log.d(TAG, "ID: " + flight.getId() + ", Aeroporto Chegada: " + flight.getAeroportoChegada());
        }

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, FlightsActivity.class);
                startActivity(intent);
            }
        });*/

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        resultTextView = findViewById(R.id.resultTextView);

        setSupportActionBar(binding.toolbar);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        /*binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
    }

    @Override
    protected void onStart() {
        super.onStart();

        ListView lista = (ListView) findViewById(R.id.lvVoos);

        listaVoos = bd.getAllVoos();

        FlightsAdapter adapter = new FlightsAdapter(this, listaVoos);
        lista.setAdapter(adapter);
        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent intent = new Intent(MainActivity.this, EditarVooActivity.class);
                intent.putExtra("ID", listaVoos.get(position).getId());
                startActivity(intent);
            }
        });
    }
    private static final String TAG = "MainActivity";

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
            Log.e(TAG, "Erro ao processar JSON: " + e.getMessage());
        }

        return flights;
    }

    private void saveFlightsToDatabase(ArrayList<Flights> flights) {
        for (Flights flight : flights) {
            // Salvar o objeto Flight no banco de dados
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
                    Log.e(TAG, "Erro na solicitação: " + response.code() + " - " + response.message());
                }
            } catch (IOException e) {
                Log.e(TAG, "Erro na solicitação: " + e.getMessage());
            }

            return null;
        }



        @Override
        protected void onPostExecute(String resultados) {
            if (resultados != null) {
                Log.d(TAG, "Resultado da chamada à API: " + resultados);
                resultTextView.setText(resultados);
                // Processar e exibir os dados da resposta da API
            } else {
                // Tratar caso ocorra um erro na chamada à API
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }
}