package org.xfade.domain;

import java.math.BigDecimal;

public class GpuDevice extends MiningDevice{
    public GpuDevice() { super(); }
    public GpuDevice(String name, int baseMhs, int powerWatts, BigDecimal purchasePrice) {
        super(name, baseMhs, powerWatts, purchasePrice);
    }

    @Override
    public int getEffectiveMhs() {
        if (isBroken() || getConditionPercent() <= 0) {
            return 0;
        }
        return getBaseMhs();
    }
}
