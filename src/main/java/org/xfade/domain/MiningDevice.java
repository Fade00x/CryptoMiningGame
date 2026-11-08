package org.xfade.domain;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;
import java.util.UUID;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = GpuDevice.class, name = "GPU"),
        @JsonSubTypes.Type(value = AsicDevice.class, name = "ASIC")
})
public abstract class MiningDevice {
    private UUID id;
    private String name;
    private int baseMhs;
    private int powerWatts;
    private BigDecimal purchasePrice;
    private int conditionPercent;
    private boolean isBroken;

    private static final Random RANDOM = new Random();

    public MiningDevice() {} // Для Jackson

    public MiningDevice(String name, int baseMhs, int powerWatts, BigDecimal purchasePrice) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.baseMhs = baseMhs;
        this.powerWatts = powerWatts;
        this.purchasePrice = purchasePrice;
        this.conditionPercent = 100;
        this.isBroken = false;
    }

    public void applyDailyWear() {
        if (isBroken) return;
        int wear = 1 + RANDOM.nextInt(3);
        this.conditionPercent = Math.max(0, this.conditionPercent - wear);
        if (this.conditionPercent < 30 && RANDOM.nextInt(100) < 25) {
            this.isBroken = true;
        }
    }

    public BigDecimal getRepairCost() {
        return purchasePrice.multiply(new BigDecimal("0.20")).setScale(2, RoundingMode.HALF_UP);
    }

    public void repair() {
        this.isBroken = false;
        this.conditionPercent = Math.max(this.conditionPercent, 85);
    }

    public BigDecimal getScrapValue() {
        if (isBroken) {
            return purchasePrice.multiply(new BigDecimal("0.10")).setScale(2, RoundingMode.HALF_UP);
        }
        BigDecimal conditionFactor = BigDecimal.valueOf(conditionPercent).divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP);
        return purchasePrice.multiply(conditionFactor).multiply(new BigDecimal("0.50")).setScale(2, RoundingMode.HALF_UP);
    }

    public abstract int getEffectiveMhs();

    public UUID getId() { return id; }
    public String getName() { return name; }
    public int getBaseMhs() { return baseMhs; }
    public int getPowerWatts() { return powerWatts; }
    public BigDecimal getPurchasePrice() { return purchasePrice; }
    public int getConditionPercent() { return conditionPercent; }
    public void setConditionPercent(int conditionPercent) { this.conditionPercent = conditionPercent; }
    public boolean isBroken() { return isBroken; }
    public void setBroken(boolean broken) { isBroken = broken; }
}