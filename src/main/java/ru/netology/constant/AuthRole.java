package ru.netology.constant;

public enum AuthRole {

    TOKEN("VALID_TOKEN");

    public final String role;

    AuthRole(String role) {
        this.role = role;
    }
}
