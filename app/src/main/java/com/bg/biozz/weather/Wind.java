package com.bg.biozz.weather;

import java.util.HashMap;
import java.util.Map;

public class Wind {

    private float speed;
    private float deg;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public float getDeg() {
        return deg;
    }

    public void setDeg(float deg) {
        this.deg = deg;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}