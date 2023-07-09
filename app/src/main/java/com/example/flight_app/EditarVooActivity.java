package com.example.flight_app;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

public class EditarVooActivity extends AppCompatActivity {
    private BDSQLiteHelper bd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_voo);
        Intent intent = getIntent();
        final int id = intent.getIntExtra("ID", 0);
        bd = new BDSQLiteHelper(this);
        Flights voos = bd.getVoo(id);
        final TextInputEditText voo = (TextInputEditText) findViewById(R.id.etVoo);
        final TextInputEditText linhaAerea = (TextInputEditText) findViewById(R.id.etLinhaAerea);
        /*final EditText ano = (EditText) findViewById(R.id.etAno);*/
        voo.setText(voos.getVoo());
        linhaAerea.setText(voos.getLinhaAerea());
        /*ano.setText(String.valueOf(livro.getAno()));*/
        final Button alterar = (Button) findViewById(R.id.btnAlterar);
        alterar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Flights flight = new Flights();
                flight.setId(id);
                flight.setVoo(voo.getText().toString());
                flight.setLinhaAerea(linhaAerea.getText().toString());
                /*livro.setAno(Integer.parseInt(ano.getText().toString()));*/
                bd.updateVoo(flight);
                Intent intent = new Intent(EditarVooActivity.this,

                        MainActivity.class);
                startActivity(intent);
            }
        });

        final Button remover = (Button) findViewById(R.id.btnRemover);
        remover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(EditarVooActivity.this)
                        .setMessage("Tem certeza de que quer apagar?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes,
                                new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        Flights voo = new Flights();
                                        voo.setId(id);
                                        bd.deleteVoo(voo);
                                        Intent intent = new Intent(EditarVooActivity.this,

                                                MainActivity.class);
                                        startActivity(intent);
                                    }
                                })
                        .setNegativeButton(android.R.string.no, null).show();
            }
        });
    }
}
