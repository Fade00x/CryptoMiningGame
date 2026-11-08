package org.xfade.service;

import org.xfade.domain.Player;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Random;

public class MarketService {
    private BigDecimal currentBtcPrice;
    private final Deque<BigDecimal> priceHistory = new ArrayDeque<>();
    private final Random random = new Random();

    public MarketService() {
        this.currentBtcPrice = new BigDecimal("42000.00");
        this.priceHistory.addLast(currentBtcPrice);
    }
    public void setCurrentBtcPrice(BigDecimal currentBtcPrice) {
        this.currentBtcPrice = currentBtcPrice;
    }

    // Вызывается каждый игровой день для пересчёта курса
    public void updateDailyRate() {
        // Колебание от -10% до +12% в день
        double changePercent = -10.0 + (random.nextDouble() * 22.0);
        BigDecimal multiplier = BigDecimal.valueOf(1.0 + (changePercent / 100.0));

        BigDecimal newPrice = currentBtcPrice.multiply(multiplier).setScale(2, RoundingMode.HALF_UP);

        // Коридор курса: от $20,000 до $90,000
        if (newPrice.compareTo(new BigDecimal("20000.00")) < 0) {
            newPrice = new BigDecimal("20000.00");
        } else if (newPrice.compareTo(new BigDecimal("90000.00")) > 0) {
            newPrice = new BigDecimal("90000.00");
        }

        this.currentBtcPrice = newPrice;
        if (priceHistory.size() >= 5) {
            priceHistory.removeFirst();
        }
        priceHistory.addLast(newPrice);
    }

    // Продажа BTC
    public boolean sellBtc(Player player, BigDecimal amountBtc) {
        if (amountBtc.compareTo(BigDecimal.ZERO) <= 0 || !player.withdrawBtc(amountBtc)) {
            return false;
        }
        BigDecimal gainedUsd = amountBtc.multiply(currentBtcPrice).setScale(2, RoundingMode.HALF_UP);
        player.depositUsd(gainedUsd);
        return true;
    }

    // Покупка BTC за доллары
    public boolean buyBtc(Player player, BigDecimal amountUsd) {
        if (amountUsd.compareTo(BigDecimal.ZERO) <= 0 || !player.withdrawUsd(amountUsd)) {
            return false;
        }
        BigDecimal boughtBtc = amountUsd.divide(currentBtcPrice, 8, RoundingMode.HALF_UP);
        player.depositBtc(boughtBtc);
        return true;
    }

    public BigDecimal getCurrentBtcPrice() {
        return currentBtcPrice;
    }

    public Deque<BigDecimal> getPriceHistory() {
        return priceHistory;
    }
}
