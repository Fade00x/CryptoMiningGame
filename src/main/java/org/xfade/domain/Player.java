package org.xfade.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Player {
    private final String name;
    private BigDecimal usdBalance;
    private BigDecimal btcBalance;
    private int currentDay;
    private LocationType currentLocation;
    private final List<MiningDevice> inventory;

    public Player(String name) {
        this.name = name;
        this.usdBalance = new BigDecimal("200.00");
        this.btcBalance = BigDecimal.ZERO;
        this.currentDay = 1;
        this.currentLocation = LocationType.BEDROOM;
        this.inventory = new ArrayList<>();
    }

    // Безопасное списание: при 0 просто возвращает true, при минусе - false, без краша программы
    public boolean withdrawUsd(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0) {
            return false;
        }
        if (amount.compareTo(BigDecimal.ZERO) == 0) {
            return true; // Списание 0$ (например, бесплатный свет) — успешно
        }
        if (this.usdBalance.compareTo(amount) < 0) {
            return false; // Недостаточно средств
        }
        this.usdBalance = this.usdBalance.subtract(amount);
        return true;
    }

    // Безопасное пополнение
    public void depositUsd(BigDecimal amount) {
        if (amount != null && amount.compareTo(BigDecimal.ZERO) > 0) {
            this.usdBalance = this.usdBalance.add(amount);
        }
    }

    public void depositBtc(BigDecimal amount) {
        if (amount != null && amount.compareTo(BigDecimal.ZERO) > 0) {
            this.btcBalance = this.btcBalance.add(amount);
        }
    }

    public boolean withdrawBtc(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0) {
            return false;
        }
        if (amount.compareTo(BigDecimal.ZERO) == 0) {
            return true;
        }
        if (this.btcBalance.compareTo(amount) < 0) {
            return false;
        }
        this.btcBalance = this.btcBalance.subtract(amount);
        return true;
    }

    public int getTotalPowerWatts() {
        return inventory.stream()
                .filter(d -> !d.isBroken())
                .mapToInt(MiningDevice::getPowerWatts)
                .sum();
    }

    public void nextDay() {
        this.currentDay++;
    }

    // Геттеры
    public String getName() { return name; }
    public BigDecimal getUsdBalance() { return usdBalance; }
    public BigDecimal getBtcBalance() { return btcBalance; }
    public int getCurrentDay() { return currentDay; }
    public LocationType getCurrentLocation() { return currentLocation; }
    public void setCurrentLocation(LocationType location) { this.currentLocation = location; }
    public List<MiningDevice> getInventory() { return inventory; }
}
