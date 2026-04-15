package com.parkinglot.model;

import com.parkinglot.model.enums.AccountStatus;

public abstract class Account {
    private String userName;
    private String password;
    private AccountStatus status;
    private Person person;

    public Account(String userName, String password, Person person) {
        this.userName = userName;
        this.password = password;
        this.status = AccountStatus.ACTIVE;
        this.person = person;
    }

    public boolean authenticate(String userName, String password) {
        return this.userName.equals(userName) && this.password.equals(password)
                && this.status == AccountStatus.ACTIVE;
    }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public AccountStatus getStatus() { return status; }
    public void setStatus(AccountStatus status) { this.status = status; }
    public Person getPerson() { return person; }
    public void setPerson(Person person) { this.person = person; }

    public abstract String getRole();
}
