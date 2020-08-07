package com.example.pinwifly.Model;

import java.util.List;

public class Peticion {
    private String phone;
    private String name;
    private String address;
    private String total;
    private String status;
    private String paymentState;
    private String latLng;
    private List<Pedido> products; //Lista de productos pedidos

    public Peticion() {
    }

    public Peticion(String phone, String name, String address, String total, String status, String paymentState, String latLng, List<Pedido> products) {
        this.phone = phone;
        this.name = name;
        this.address = address;
        this.total = total;
        this.status = status;
        this.paymentState = paymentState;
        this.latLng = latLng;
        this.products = products;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPaymentState() {
        return paymentState;
    }

    public void setPaymentState(String paymentState) {
        this.paymentState = paymentState;
    }

    public String getLatLng() {
        return latLng;
    }

    public void setLatLng(String latLng) {
        this.latLng = latLng;
    }

    public List<Pedido> getProducts() {
        return products;
    }

    public void setProducts(List<Pedido> products) {
        this.products = products;
    }
}
