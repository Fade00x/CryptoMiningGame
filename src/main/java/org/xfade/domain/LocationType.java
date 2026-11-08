package org.xfade.domain;

import java.math.BigDecimal;

public enum LocationType {
    BEDROOM("Подвал", BigDecimal.ZERO, 2, 600, BigDecimal.ZERO, false),
    GARAGE("Гараж", new BigDecimal("10.00"), 10, 8000, new BigDecimal("0.10"), true),
    WAREHOUSE("Складской ангар", new BigDecimal("80.00"), 50, 100000, new BigDecimal("0.04"), true);

    private final String title;
    private final BigDecimal dailyRent;
    private final int maxSlots;
    private final int maxPowerWatts;
    private final BigDecimal electricityRate;
    private final boolean asicAllowed;

    LocationType(String title, BigDecimal dailyRent, int maxSlots, int maxPowerWatts, BigDecimal electricityRate, boolean asicAllowed){
        this.title = title;
        this.dailyRent = dailyRent;
        this.maxSlots = maxSlots;
        this.maxPowerWatts = maxPowerWatts;
        this.electricityRate = electricityRate;
        this.asicAllowed = asicAllowed;
    }

    public String getTitle() {
        return title;
    }
    public BigDecimal getDailyRent() {
        return dailyRent;
    }
    public int getMaxSlots(){
        return maxSlots;
    }

    public int getMaxPowerWatts() {
        return maxPowerWatts;
    }

    public BigDecimal getElectricityRate() {
        return electricityRate;
    }

    public boolean isAsicAllowed() {
        return asicAllowed;
    }
}
