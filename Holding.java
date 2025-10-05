package com.quinpoint.model;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Flexible Holding model:
 * - primary constructor used by the CSV loader: (clientId, instrumentId, quantity)
 * - older code can still use the 4-arg constructor (portfolioId/clientId, instrumentId, buyDate, quantity)
 */
public class Holding {
    private final String clientId;        // could be portfolioId in some variants
    private final String instrumentId;
    private final LocalDate datePurchased;     // may be null
    private final BigDecimal quantity;
    private BigDecimal initialValue;


    public Holding(String clientId, String instrumentId, BigDecimal quantity,
                   LocalDate datePurchased, BigDecimal initialValue) {
        this.clientId = clientId;
        this.instrumentId = instrumentId;
        this.quantity = quantity;
        this.datePurchased = datePurchased;
        this.initialValue = initialValue;
    }

    // Existing constructor for backwards compatibility
    public Holding(String clientId, String instrumentId, BigDecimal quantity) {
        this(clientId, instrumentId, quantity, null, null);
    }

    public String getClientId() { return clientId; }
    public String getInstrumentId() { return instrumentId; }
    public BigDecimal getQuantity() { return quantity; }
    public LocalDate getDatePurchased() { return datePurchased; }
    public BigDecimal getInitialValue() { return initialValue; }

    @Override
    public String toString() {
        return "Holding[" + clientId + "," + instrumentId + "," + quantity + (datePurchased != null ? ("," + datePurchased) : "") + "]";
    }
}
