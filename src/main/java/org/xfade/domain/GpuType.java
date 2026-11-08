package org.xfade.domain;

import java.math.BigDecimal;

public enum GpuType {
    GTX_1060("GTX 1060 6GB", 25, 120, new BigDecimal("120.00"), "Стартовая карта, низкий износ"),
    RX_580("RX 580 8GB", 32, 150, new BigDecimal("160.00"), "Народный хит, быстрый нагрев"),
    RTX_3070("RTX 3070", 62, 220, new BigDecimal("500.00"), "Оптимальный баланс W / MH/s"),
    RTX_4090("RTX 4090", 130, 450, new BigDecimal("1600.00"), "Топовая игровая видеокарта");

    private final String title;
    private final int baseMhs;
    private final int powerWatts;
    private final BigDecimal basePrice;
    private final String description;

    GpuType(String title, int baseMhs, int powerWatts, BigDecimal basePrice, String description) {
        this.title = title;
        this.baseMhs = baseMhs;
        this.powerWatts = powerWatts;
        this.basePrice = basePrice;
        this.description = description;
    }

    public String getTitle() { return title; }
    public int getBaseMhs() { return baseMhs; }
    public int getPowerWatts() { return powerWatts; }
    public BigDecimal getBasePrice() { return basePrice; }
    public String getDescription() { return description; }
}
