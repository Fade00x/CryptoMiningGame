package org.xfade.domain;

import java.math.BigDecimal;

public enum AsicType {
    ANTMINER_S9("Antminer S9 (Б/У)", 450, 1350, new BigDecimal("900.00"), 50),
    ANTMINER_S19("Antminer S19 Pro", 1400, 3250, new BigDecimal("4200.00"), 100),
    WHATS_MINER_M60("WhatsMiner M60", 2500, 5500, new BigDecimal("8500.00"), 100);

    private final String title;
    private final int baseMhs;
    private final int powerWatts;
    private final BigDecimal basePrice;
    private final int initialCondition;

    AsicType(String title, int baseMhs, int powerWatts, BigDecimal basePrice, int initialCondition) {
        this.title = title;
        this.baseMhs = baseMhs;
        this.powerWatts = powerWatts;
        this.basePrice = basePrice;
        this.initialCondition = initialCondition;
    }

    public String getTitle() { return title; }
    public int getBaseMhs() { return baseMhs; }
    public int getPowerWatts() { return powerWatts; }
    public BigDecimal getBasePrice() { return basePrice; }
    public int getInitialCondition() { return initialCondition; }
}
