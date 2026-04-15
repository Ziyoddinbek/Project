package com.parkinglot.model;

public class Person {
    private String name;
    private Location address;
    private String email;
    private String phone;

    public Person(String name, Location address, String email, String phone) {
        this.name = name;
        this.address = address;
        this.email = email;
        this.phone = phone;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Location getAddress() { return address; }
    public void setAddress(Location address) { this.address = address; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
}
