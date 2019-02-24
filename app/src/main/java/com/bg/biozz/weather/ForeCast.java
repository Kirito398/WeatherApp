package com.bg.biozz.weather;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ForeCast {
    @SerializedName("list")
    private List<Message> items = null;

    public List<Message> getItems() {
        return items;
    }

    public void setItems(List<Message> items) {
        this.items = items;
    }
}