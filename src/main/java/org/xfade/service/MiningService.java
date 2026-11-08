package org.xfade.service;

import org.xfade.domain.MiningDevice;
import org.xfade.domain.Player;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class MiningService {
    // Формула: 1 MH/s дает 0.00001 BTC в сутки
    private static final BigDecimal BTC_PER_MHS = new BigDecimal("0.00001");

    public BigDecimal calculateDailyMinedBtc(Player player) {
        int totalMhs = player.getInventory().stream()
                .mapToInt(MiningDevice::getEffectiveMhs)
                .sum();

        return BigDecimal.valueOf(totalMhs).multiply(BTC_PER_MHS);
    }

    // Расчёт суточного счёта за электричество: (Watts * 24 / 1000) * тариф
    public BigDecimal calculateDailyElectricityCost(Player player) {
        int totalWatts = player.getTotalPowerWatts();
        if (totalWatts == 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal dailyKwh = BigDecimal.valueOf(totalWatts)
                .multiply(BigDecimal.valueOf(24))
                .divide(BigDecimal.valueOf(1000), 4, RoundingMode.HALF_UP);

        return dailyKwh.multiply(player.getCurrentLocation().getElectricityRate())
                .setScale(2, RoundingMode.HALF_UP);
    }
    public List<MiningDevice> processWearAndBreakdowns(Player player) {
        List<MiningDevice> newlyBroken = new ArrayList<>();
        for (MiningDevice device : player.getInventory()) {
            boolean wasBrokenBefore = device.isBroken();
            device.applyDailyWear();
            if (!wasBrokenBefore && device.isBroken()) {
                newlyBroken.add(device);
            }
        }
        return newlyBroken;
    }
}
