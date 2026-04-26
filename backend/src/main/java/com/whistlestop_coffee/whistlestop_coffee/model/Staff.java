package com.whistlestop_coffee.whistlestop_coffee.model;

public class Staff {
    private int id;
    private String name;
    private String email;
    private String password;

    public Staff(int id, String name, String email, String password){
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public String getEmail(){
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }
}
