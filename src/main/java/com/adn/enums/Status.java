package com.adn.enums;

public enum Status {
    ACTIVE("Active"), INACTIVE("Inactive");

    private String value;

    private Status(String value) { this.value = value; } // cant instantiate (does not expose info)

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
}
