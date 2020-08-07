package com.example.pinwifly.Model;

public class User {
    private String Name;
    private String Email;
    private String Password;
    private String Phone;
    private String Repartidor;
    private String Foto;

    public User() {
    }

    public User(String name, String email, String password,String phone, String foto) {
        Name = name;
        Email = email;
        Password = password;
        Phone = phone;
        Repartidor = "false";
        Foto = foto;
    }

    public String getFoto() {
        return Foto;
    }

    public void setFoto(String foto) {
        Foto = foto;
    }

    public String getRepartidor() {
        return Repartidor;
    }

    public void setRepartidor(String repartidor) {
        Repartidor = repartidor;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }
}
