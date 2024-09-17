package com.spedire.Spedire.enums;

public enum PaymentType {

    BANK_TRANSFER("BANK_TRANSFER"), USSD("USSD"), QR_CODE("QR_CODE"), CARD("CARD");

    private final String name;

    PaymentType(String name) {
        this.name = name;
    }

    private String getName() {
        return name;
    }


}
