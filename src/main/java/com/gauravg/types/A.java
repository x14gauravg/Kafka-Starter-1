package com.gauravg.types;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "key", "value" })
public class A {

    @JsonProperty("key")
    private String key;

    @JsonProperty("value")
    private int value;

    @JsonProperty("key")
    public String getKey() {
        return key;
    }

    @JsonProperty("key")
    public void setKey(String key) {
        this.key = key;
    }

    @JsonProperty("value")
    public int getValue() {
        return value;
    }

    @JsonProperty("value")
    public void setValue(int value) {
        this.value = value;
    }
}
