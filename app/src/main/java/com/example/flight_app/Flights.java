package com.example.flight_app;


public class Flights {

    private int id;
    private String linhaAerea;
    private String aeroportoChegada;
    private String aeroportoPartida;
    private String voo;
    private String latitude;
    private String longitude;

    public Flights() { }

    public Flights(String linhaAerea, String aeroportoChegada, String aeroportoPartida, String voo, String latitude, String longitude) {
        super();
        this.linhaAerea = linhaAerea;
        this.aeroportoChegada = aeroportoChegada;
        this.aeroportoPartida = aeroportoPartida;
        this.voo = voo;
        this.latitude = latitude;
        this.longitude = longitude;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getLinhaAerea() {
        return linhaAerea;
    }
    public void setLinhaAerea(String linhaAerea) {
        this.linhaAerea = linhaAerea;
    }

    public String getAeroportoChegada() {
        return aeroportoChegada;
    }

    public void setAeroportoChegada(String aeroportoChegada) {
        this.aeroportoChegada = aeroportoChegada;
    }

    public String getAeroportoPartida() {
        return aeroportoPartida;
    }

    public void setAeroportoPartida(String aeroportoPartida) {
        this.aeroportoPartida = aeroportoPartida;
    }

    public String getVoo() {
        return voo;
    }

    public void setVoo(String voo) {
        this.voo = voo;
    }
    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

}
