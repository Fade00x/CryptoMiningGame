package org.xfade.domain;

import java.math.BigDecimal;

public class AsicDevice extends MiningDevice{
    public AsicDevice() {
        super();
    }

    public AsicDevice(String name, int baseMhs, int powerWatts, BigDecimal purchasePrice, int initialCondition) {
        super(name, baseMhs, powerWatts, purchasePrice);
        setConditionPercent(initialCondition);
    }

    @Override
    public int getEffectiveMhs() {
        if (isBroken() || getConditionPercent() <= 0) {
            return 0;
        }
        return getBaseMhs();
    }
}
