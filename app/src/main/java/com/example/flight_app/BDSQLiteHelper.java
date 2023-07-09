package com.example.flight_app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class BDSQLiteHelper extends SQLiteOpenHelper {
        private static final int DATABASE_VERSION = 1;
        private static final String DATABASE_NAME = "VooDB";
        private static final String TABELA_VOOS = "voos";
        private static final String ID = "id";
        private static final String LINHAAEREA = "linhaAerea";
        private static final String AEROPORTOCHEGADA = "aeroportoChegada";
        private static final String AEROPORTOPARTIDA = "aeroportoPartida";
        private static final String VOO = "voo";
        private static final String LATITUDE = "latitude";
        private static final String LONGITUDE = "longitude";

        private static final String[] COLUNAS = {ID, LINHAAEREA, AEROPORTOCHEGADA, AEROPORTOPARTIDA, VOO, LATITUDE, LONGITUDE};
        public BDSQLiteHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            String CREATE_TABLE = "CREATE TABLE voos ("+
                    "id INTEGER PRIMARY KEY AUTOINCREMENT,"+
                    "linhaAerea TEXT,"+
                    "aeroportoChegada TEXT,"+
                    "aeroportoPartida TEXT,"+
                    "voo TEXT,"+
                    "latitude TEXT,"+
                    "longitude TEXT)";

            db.execSQL(CREATE_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS voos");
            this.onCreate(db);
        }

        public void addVoo(Flights voo) {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(LINHAAEREA, voo.getLinhaAerea());
            values.put(AEROPORTOCHEGADA, voo.getAeroportoChegada());
            values.put(AEROPORTOPARTIDA, voo.getAeroportoPartida());
            values.put(VOO, voo.getVoo());
            values.put(LATITUDE, voo.getLatitude());
            values.put(LONGITUDE, voo.getLongitude());
            db.insert(TABELA_VOOS, null, values);
            db.close();
        }
        private Flights cursorToVoo(Cursor cursor) {
            Flights voo = new Flights();
            voo.setId(Integer.parseInt(cursor.getString(0)));
            voo.setLinhaAerea(cursor.getString(1));
            voo.setAeroportoChegada(cursor.getString(2));
            voo.setAeroportoPartida(cursor.getString(3));
            voo.setVoo(cursor.getString(4));
            voo.setLatitude(cursor.getString(5));
            voo.setLongitude(cursor.getString(6));
        return voo;
    }

        public Flights getVoo(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABELA_VOOS,
                COLUNAS,
                " id = ?",
                new String[] { String.valueOf(id) },
                null,
                null,
                null,
                null);
        if (cursor == null) {
            return null;
        } else {
            cursor.moveToFirst();
            Flights voo = cursorToVoo(cursor);
            return voo;
        }
        }

        public ArrayList<Flights> getAllVoos() {
            ArrayList<Flights> listaVoos = new ArrayList<Flights>();
            String query = "SELECT * FROM " + TABELA_VOOS

                + " ORDER BY " + VOO;

            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {
                    Flights voo = cursorToVoo(cursor);
                    listaVoos.add(voo);
                } while (cursor.moveToNext());
            }
            return listaVoos;
        }

        public int updateVoo(Flights voo) {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(LINHAAEREA, voo.getLinhaAerea());
            values.put(AEROPORTOCHEGADA, voo.getAeroportoChegada());
            values.put(AEROPORTOPARTIDA, voo.getAeroportoPartida());
            values.put(VOO, voo.getVoo());
            values.put(LATITUDE, voo.getLatitude());
            values.put(LONGITUDE, voo.getLongitude());
            int i = db.update(TABELA_VOOS, values, ID + " = ?", new String[]{ String.valueOf(voo.getId())});
            db.close();
            return i;
        }
        public int deleteVoo(Flights voo) {
            SQLiteDatabase db = this.getWritableDatabase();
            int i = db.delete(TABELA_VOOS, ID + " = ?", new String[]{ String.valueOf(voo.getId())});
            db.close();
            return i;
    }
}
