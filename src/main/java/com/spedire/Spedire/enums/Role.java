package com.spedire.Spedire.enums;

public enum Role {

    NEW_USER("NEW_USER"),

    SENDER("SENDER"),

    CARRIER("CARRIER"),

    ADMIN("ADMIN");

    private final String name;

    Role(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
