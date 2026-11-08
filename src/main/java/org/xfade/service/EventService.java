package org.xfade.service;

import org.xfade.domain.Player;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;

public class EventService {
    private final Random random = new Random();

    public String triggerDailyEvent(Player player, MarketService market) {
        // Шанс события: 20% в день
        if (random.nextInt(100) >= 20) {
            return null;
        }

        int eventId = random.nextInt(4);
        switch (eventId) {
            case 0 -> {
                // Твит Илона: курс взлетает на +30%
                BigDecimal pumpedPrice = market.getCurrentBtcPrice().multiply(new BigDecimal("1.30")).setScale(2, RoundingMode.HALF_UP);
                market.setCurrentBtcPrice(pumpedPrice);
                return "🚀 Твит Илона Маска! Рынок взлетел на +30%! Новый курс: $" + pumpedPrice;
            }
            case 1 -> {
                // Криптозима: курс падает на -25%
                BigDecimal dumpedPrice = market.getCurrentBtcPrice().multiply(new BigDecimal("0.75")).setScale(2, RoundingMode.HALF_UP);
                market.setCurrentBtcPrice(dumpedPrice);
                return "📉 Криптозима! Внезапный обвал рынка на -25%. Новый курс: $" + dumpedPrice;
            }
            case 2 -> {
                // Проверка электросети (если не в спальне)
                if (player.getCurrentLocation().getDailyRent().compareTo(BigDecimal.ZERO) > 0) {
                    BigDecimal fine = new BigDecimal("150.00");
                    player.withdrawUsd(fine);
                    return "👮 Проверка электросети! Инспектор выписал штраф за превышение нагрузки: -$150.";
                }
                return null;
            }
            case 3 -> {
                // Аирдроп
                BigDecimal bonusUsd = new BigDecimal("75.00");
                player.depositUsd(bonusUsd);
                return "🎁 Успешный ретро-дроп! На ваш кошелек зачислено +$75.";
            }
            default -> {
                return null;
            }
        }
    }
}
