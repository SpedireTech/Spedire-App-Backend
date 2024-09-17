package com.spedire.Spedire.enums;

public enum PaymentStatus {

    PENDING("PENDING"), PAID("PAID"), ABANDONED("ABANDONED"), FAILED("FAILED"), PROCESSING("PROCESSING"), REVERSED("REVERSED"), UNKNOWN("UNKNOWN");

    private final String name;

    PaymentStatus(String name) {
        this.name = name;
    }

    private String getName() {
        return name;
    }

}
