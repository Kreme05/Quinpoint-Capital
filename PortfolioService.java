package com.quinpoint.service;

import com.quinpoint.model.Holding;
import com.quinpoint.model.PricePoint;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;

public class PortfolioService {

    private final CsvLoaderService loader;

    public PortfolioService(CsvLoaderService loader) {
        this.loader = loader;
    }

    /**
     * Get total portfolio value for a client (latest prices).
     */
    public BigDecimal getPortfolioValue(String clientId) {
        List<Holding> clientHoldings = loader.getHoldings().getOrDefault(clientId, List.of());
        Map<String, List<PricePoint>> allPrices = loader.getPrices();

        BigDecimal total = BigDecimal.ZERO;

        for (Holding h : clientHoldings) {
            List<PricePoint> priceHistory = allPrices.get(h.getInstrumentId());
            if (priceHistory == null || priceHistory.isEmpty()) continue;

            BigDecimal latestPrice = priceHistory.get(priceHistory.size() - 1).getClose();
            BigDecimal value = latestPrice.multiply(h.getQuantity());
            total = total.add(value);
        }

        return total.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Get time series of portfolio value (aggregated across holdings).
     */
    public Map<LocalDate, BigDecimal> getPortfolioHistory(String clientId) {
        List<Holding> clientHoldings = loader.getHoldings().getOrDefault(clientId, List.of());
        Map<String, List<PricePoint>> allPrices = loader.getPrices();

        Map<LocalDate, BigDecimal> history = new TreeMap<>();

        for (Holding h : clientHoldings) {
            List<PricePoint> priceHistory = allPrices.get(h.getInstrumentId());
            if (priceHistory == null) continue;

            for (PricePoint p : priceHistory) {
                BigDecimal value = p.getClose().multiply(h.getQuantity());
                history.merge(p.getDate(), value, BigDecimal::add);
            }
        }

        // normalize precision
        history.replaceAll((date, val) -> val.setScale(2, RoundingMode.HALF_UP));
        return history;
    }

    /**
     * Get individual holding values (current snapshot).
     */
    public Map<String, BigDecimal> getHoldingsBreakdown(String clientId) {
        List<Holding> clientHoldings = loader.getHoldings().getOrDefault(clientId, List.of());
        Map<String, List<PricePoint>> allPrices = loader.getPrices();

        Map<String, BigDecimal> breakdown = new HashMap<>();

        for (Holding h : clientHoldings) {
            List<PricePoint> priceHistory = allPrices.get(h.getInstrumentId());
            if (priceHistory == null || priceHistory.isEmpty()) continue;

            BigDecimal latestPrice = priceHistory.get(priceHistory.size() - 1).getClose();
            BigDecimal value = latestPrice.multiply(h.getQuantity());
            breakdown.put(h.getInstrumentId(), value.setScale(2, RoundingMode.HALF_UP));
        }

        return breakdown;
    }
}
