package com.example.pinwifly.Model;

import java.util.List;

public class Peticion {
    private String phone;
    private String name;
    private String address;
    private String total;
    private String status;
    private List<Pedido> products; //Lista de productos pedidos

    public Peticion() {
    }

    public Peticion(String phone, String name, String address, String total, List<Pedido> products) {
        this.phone = phone;
        this.name = name;
        this.address = address;
        this.total = total;
        this.products = products;
        this.status = "0"; //Default es 0 , 0 es Pedido realizado, 1 es Pedido en curso, 2 es Pedido entregado;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public List<Pedido> getProducts() {
        return products;
    }

    public void setProducts(List<Pedido> products) {
        this.products = products;
    }
}
