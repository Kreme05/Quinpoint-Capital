package com.quinpoint.model;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * PricePoint holds a date and close price, optionally linked to an instrumentId.
 * Two constructors: one with instrumentId (used by the loader), one without (older code).
 */
public class PricePoint {
    private final String instrumentId; // may be null if not provided
    private final LocalDate date;
    private final BigDecimal close;

    // Constructor used by the loader: (instrumentId, date, close)
    public PricePoint(String instrumentId, LocalDate date, BigDecimal close) {
        this.instrumentId = instrumentId;
        this.date = date;
        this.close = close;
    }

    // Backwards-compatible constructor: (date, close)
    public PricePoint(LocalDate date, BigDecimal close) {
        this.instrumentId = null;
        this.date = date;
        this.close = close;
    }

    public String getInstrumentId() { return instrumentId; }
    public LocalDate getDate() { return date; }
    public BigDecimal getClose() { return close; }

    // alias for older code that expects getPrice()
    public BigDecimal getPrice() { return close; }

    @Override
    public String toString() {
        return "PricePoint[" + (instrumentId != null ? instrumentId + "," : "") + date + "," + close + "]";
    }
}
