package com.example.flight_app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class FlightsAdapter extends ArrayAdapter<Flights> {
        private final Context context;
        private final ArrayList<Flights> elementos;
        public FlightsAdapter(Context context, ArrayList<Flights> elementos) {
            super(context, R.layout.linha, elementos);
            this.context = context;
            this.elementos = elementos;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.linha, parent, false);
            TextView voo = (TextView) rowView.findViewById(R.id.txtVoo);
            TextView linhaAerea = (TextView) rowView.findViewById(R.id.txtLinhaAerea);
            /*TextView autor = (TextView) rowView.findViewById(R.id.txtAutor);*/
            voo.setText(elementos.get(position).getVoo());
            linhaAerea.setText(elementos.get(position).getLinhaAerea());
            /*ano.setText(Integer.toString(elementos.get(position).getAno()));*/
            return rowView;
        }
    }
