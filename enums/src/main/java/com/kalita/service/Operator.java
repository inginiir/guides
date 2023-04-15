package com.kalita.service;

import java.util.Arrays;

public enum Operator {

    EQUALS("=="),
    NOT_EQUALS("!="),
    GREATER_EQUALS(">="),
    LESS_EQUALS("<="),
    GREATER(">"),
    LESS("<"),
    REGEX("LIKE") {
        @Override
        String buildRequest(String name, String value) {
            return String.format("%s %s '%%%s%%'", name,  sign, value);
        }
    };

    protected final String sign;

    Operator(String sign) {
        this.sign = sign;
    }

    public String getSign() {
        return sign;
    }

    public static Operator parseOperator(String operatorString) {
        return Arrays.stream(Operator.values())
                .filter(operator -> operator.name().equals(operatorString))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Operator not supported: " + operatorString));
    }

    String buildRequest(String name, String value) {
        return String.format("%s %s %s", name, getSign(), value);
    }
}
