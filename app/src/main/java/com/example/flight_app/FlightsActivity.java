package com.example.flight_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class FlightsActivity extends AppCompatActivity {
    private BDSQLiteHelper bd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voo);
        bd = new BDSQLiteHelper(this);
        final EditText voo = (EditText) findViewById(R.id.edVoo);
        final EditText linhaAerea = (EditText) findViewById(R.id.edLinhaAerea);
        /*final EditText ano = (EditText) findViewById(R.id.edAno);*/
        Button novo = (Button) findViewById(R.id.btnAdd);
        novo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Flights voo = new Flights();
                voo.setVoo(voo.getVoo().toString());
                voo.setLinhaAerea(voo.getLinhaAerea().toString());
                /*voo.setAno(Integer.parseInt(ano.getText().toString()));*/
                bd.addVoo(voo);
                Intent intent = new Intent(FlightsActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });
    }
}