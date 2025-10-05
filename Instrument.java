package com.quinpoint.model;

public class Instrument {
    private String id;
    private String ticker;
    private String name;

    public Instrument(String id, String ticker, String name) {
        this.id = id;
        this.ticker = ticker;
        this.name = name;
    }

    public String getId() { return id; }
    public String getTicker() { return ticker; }
    public String getName() { return name; }

    @Override
    public String toString() {
        return ticker + " - " + name;
    }
}
