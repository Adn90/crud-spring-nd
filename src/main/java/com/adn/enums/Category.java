package com.adn.enums;

public enum Category {
    BACK_END("Back-end"), FRONT_END("Front-end"), GAMES("Games");

    private String value;

    private Category(String value) { this.value = value; } // cant instantiate (does not expose info)

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
}
