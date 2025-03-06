package com.geonlee.spring.shared.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class VoidResponse {
    private static final VoidResponse INSTANCE = new VoidResponse();

    private VoidResponse() {}

    public static VoidResponse getInstance() {
        return INSTANCE;
    }

    @Override
    public String toString() {
        return "{}";
    }
}