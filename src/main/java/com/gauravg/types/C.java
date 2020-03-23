package com.gauravg.types;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "key", "total" })
public class C {

    @JsonProperty("key")
    private String key;

    @JsonProperty("total")
    private int total;

    @JsonProperty("key")
    public String getKey() {
        return key;
    }

    @JsonProperty("key")
    public void setKey(String key) {
        this.key = key;
    }

    @JsonProperty("total")
    public int getTotal() {
        return total;
    }

    @JsonProperty("total")
    public void setTotal(int value) {
        this.total = value;
    }
}
