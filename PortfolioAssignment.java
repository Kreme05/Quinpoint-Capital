package com.quinpoint.model;

public class PortfolioAssignment {
    private final String portfolioId;
    private final String clientId;
    private final String advisorId;

    public PortfolioAssignment(String portfolioId, String clientId, String advisorId) {
        this.portfolioId = portfolioId;
        this.clientId = clientId;
        this.advisorId = advisorId;
    }

    public String getPortfolioId() { return portfolioId; }
    public String getClientId() { return clientId; }
    public String getAdvisorId() { return advisorId; }
}