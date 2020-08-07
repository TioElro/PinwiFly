package com.example.pinwifly.Model;

public class Direccion {
    private String Direccion;
    private Double Lat;
    private Double Lng;

    public Direccion() {
    }

    public Direccion(String direccion, Double lat, Double lng) {
        Direccion = direccion;
        Lat = lat;
        Lng = lng;
    }

    public String getDireccion() {
        return Direccion;
    }

    public void setDireccion(String direccion) {
        Direccion = direccion;
    }

    public Double getLat() {
        return Lat;
    }

    public void setLat(Double lat) {
        Lat = lat;
    }

    public Double getLng() {
        return Lng;
    }

    public void setLng(Double lng) {
        Lng = lng;
    }
}
