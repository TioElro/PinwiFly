package com.example.pinwifly.Model;

public class Producto {
    private String Name,Image,Price,Discount,Categoria,Description;

    public Producto() {
    }

    public Producto(String name, String image, String price, String discount, String categoria, String description) {
        Name = name;
        Image = image;
        Price = price;
        Discount = discount;
        Categoria = categoria;
        Description = description;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public String getDiscount() {
        return Discount;
    }

    public void setDiscount(String discount) {
        Discount = discount;
    }

    public String getCategoria() {
        return Categoria;
    }

    public void setCategoria(String categoria) {
        Categoria = categoria;
    }
}
