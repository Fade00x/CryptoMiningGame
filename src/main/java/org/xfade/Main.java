package org.xfade;

import org.xfade.domain.*;
import org.xfade.repository.SaveLoadRepository;
import org.xfade.service.EventService;
import org.xfade.service.MarketService;
import org.xfade.service.MiningService;
import org.xfade.service.ShopService;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Scanner;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        SaveLoadRepository repository = new SaveLoadRepository();
        MiningService miningService = new MiningService();
        MarketService marketService = new MarketService();
        ShopService shopService = new ShopService();
        EventService eventService = new EventService();

        Player player = initGame(scanner, repository);

        boolean running = true;

        while (running) {
            printDashboard(player, marketService);

            System.out.println("Действия:");
            System.out.println("[1] Завершить день (Майнинг, Свет, Износ)");
            System.out.println("[2] Криптобиржа (Купить / Продать BTC)");
            System.out.println("[3] Магазин оборудования (GPU & ASIC)");
            System.out.println("[4] Аренда помещений (Переезд)");
            System.out.println("[5] Состояние фермы (Диагностика, Ремонт, Б/У)");
            System.out.println("[6] Сохранить игру");
            System.out.println("[7] Выйти");
            System.out.print("> ");

            String input = scanner.nextLine().trim();

            switch (input) {
                case "1" -> processNextDay(player, miningService, marketService, eventService);
                case "2" -> openMarketMenu(scanner, player, marketService);
                case "3" -> openShopMenu(scanner, player, shopService);
                case "4" -> openRealEstateMenu(scanner, player, shopService);
                case "5" -> openFarmManagementMenu(scanner, player, shopService);
                case "6" -> saveGame(player, repository);
                case "7" -> {
                    System.out.print("Сохранить игру перед выходом? (y/n): ");
                    if (scanner.nextLine().trim().equalsIgnoreCase("y")) {
                        saveGame(player, repository);
                    }
                    System.out.println("До встречи!");
                    running = false;
                }
                default -> System.out.println("⚠️ Неверная команда.");
            }
        }
        scanner.close();
    }

    private static Player initGame(Scanner sc, SaveLoadRepository repo) {
        if (repo.hasSaveFile()) {
            System.out.println("Найдено сохранение!");
            System.out.println("[1] Загрузить игру");
            System.out.println("[2] Начать новую игру");
            System.out.print("> ");
            if ("1".equals(sc.nextLine().trim())) {
                try {
                    Player p = repo.load();
                    if (p != null) {
                        System.out.println("✅ Игра успешно загружена!");
                        return p;
                    }
                } catch (IOException e) {
                    System.out.println("❌ Ошибка загрузки файла. Начинаем новую игру.");
                }
            }
        }
        return new Player("Игрок");
    }

    private static void saveGame(Player player, SaveLoadRepository repo) {
        try {
            repo.save(player);
            System.out.println("💾 Игра успешно сохранена в savegame.json!");
        } catch (IOException e) {
            System.out.println("❌ Ошибка при сохранении: " + e.getMessage());
        }
    }

    private static void printDashboard(Player player, MarketService market) {
        System.out.println("\n" + "=".repeat(68));
        System.out.printf(" ДЕНЬ #%d | USD: $%s | BTC: %s\n",
                player.getCurrentDay(),
                player.getUsdBalance().setScale(2, RoundingMode.HALF_UP),
                player.getBtcBalance().setScale(8, RoundingMode.HALF_UP));
        System.out.printf(" КУРС BTC: $%s | История: %s\n",
                market.getCurrentBtcPrice(),
                market.getPriceHistory().stream().map(p -> "$" + p.intValue()).toList());
        System.out.println("=".repeat(68));
        System.out.printf("Локация: %s | Места: %d/%d | Нагрузка: %dW / %dW\n",
                player.getCurrentLocation().getTitle(),
                player.getInventory().size(),
                player.getCurrentLocation().getMaxSlots(),
                player.getTotalPowerWatts(),
                player.getCurrentLocation().getMaxPowerWatts());
        System.out.println("-".repeat(68));
    }

    private static void processNextDay(Player player, MiningService mining, MarketService market, EventService events) {
        if (player.getTotalPowerWatts() > player.getCurrentLocation().getMaxPowerWatts()) {
            System.out.println("\n💥 ВЫБИЛО ПРОБКИ! Потребление превышает лимит. День пропущен!");
        } else {
            BigDecimal minedBtc = mining.calculateDailyMinedBtc(player);
            BigDecimal powerCost = mining.calculateDailyElectricityCost(player);
            BigDecimal rentCost = player.getCurrentLocation().getDailyRent();

            player.depositBtc(minedBtc);
            player.withdrawUsd(powerCost.add(rentCost));

            System.out.println("\n--- ИТОГИ СУТОК ---");
            System.out.println("⛏️ Намайнено: +" + minedBtc.setScale(8, RoundingMode.HALF_UP) + " BTC");
            System.out.println("⚡ Счёт за свет: -$" + powerCost);
            if (rentCost.compareTo(BigDecimal.ZERO) > 0) {
                System.out.println("🏠 Аренда: -$" + rentCost);
            }
        }

        List<MiningDevice> broken = mining.processWearAndBreakdowns(player);
        if (!broken.isEmpty()) {
            System.out.println("\n⚠️ ВНИМАНИЕ: Сгорело оборудование (" + broken.size() + " шт.)!");
            for (MiningDevice d : broken) {
                System.out.println("   ❌ Вышла из строя: " + d.getName());
            }
        }

        market.updateDailyRate();
        player.nextDay();

        String eventText = events.triggerDailyEvent(player, market);
        if (eventText != null) {
            System.out.println("\n📣 СОБЫТИЕ ДНЯ: " + eventText);
        }
    }

    private static void openMarketMenu(Scanner sc, Player player, MarketService market) {
        System.out.println("\n--- КРИПТОБИРЖА ---");
        System.out.println("Текущий курс: 1 BTC = $" + market.getCurrentBtcPrice());
        System.out.println("[1] Продать все BTC");
        System.out.println("[2] Продать точную сумму BTC");
        System.out.println("[3] Купить BTC на сумму в USD");
        System.out.println("[0] Назад");
        System.out.print("> ");

        switch (sc.nextLine().trim()) {
            case "1" -> {
                if (market.sellBtc(player, player.getBtcBalance())) System.out.println("✅ Все BTC проданы!");
                else System.out.println("❌ Нечего продавать.");
            }
            case "2" -> {
                System.out.print("Сколько BTC продать: ");
                try {
                    BigDecimal toSell = new BigDecimal(sc.nextLine().trim());
                    if (market.sellBtc(player, toSell)) System.out.println("✅ Продано!");
                    else System.out.println("❌ Недостаточно BTC.");
                } catch (Exception e) {
                    System.out.println("⚠️ Некорректное значение.");
                }
            }
            case "3" -> {
                System.out.print("На сколько USD купить BTC: ");
                try {
                    BigDecimal toSpend = new BigDecimal(sc.nextLine().trim());
                    if (market.buyBtc(player, toSpend)) System.out.println("✅ Куплено!");
                    else System.out.println("❌ Недостаточно USD.");
                } catch (Exception e) {
                    System.out.println("⚠️ Некорректное значение.");
                }
            }
        }
    }

    private static void openShopMenu(Scanner sc, Player player, ShopService shop) {
        System.out.println("\n--- МАГАЗИН ОБОРУДОВАНИЯ ---");
        System.out.println("[1] Видеокарты (GPU)");
        System.out.println("[2] Промышленные майнеры (ASIC)");
        System.out.println("[0] Назад");
        System.out.print("> ");

        String type = sc.nextLine().trim();
        if ("1".equals(type)) {
            GpuType[] cards = GpuType.values();
            for (int i = 0; i < cards.length; i++) {
                GpuType c = cards[i];
                System.out.printf("[%d] %s | $%s | %d MH/s | %dW\n", i + 1, c.getTitle(), c.getBasePrice(), c.getBaseMhs(), c.getPowerWatts());
            }
            System.out.print("Выберите карту > ");
            try {
                int idx = Integer.parseInt(sc.nextLine().trim()) - 1;
                if (idx >= 0 && idx < cards.length) {
                    ShopService.PurchaseResult res = shop.buyGpu(player, cards[idx]);
                    printPurchaseResult(res);
                }
            } catch (Exception ignored) {}
        } else if ("2".equals(type)) {
            AsicType[] asics = AsicType.values();
            for (int i = 0; i < asics.length; i++) {
                AsicType a = asics[i];
                System.out.printf("[%d] %s | $%s | %d MH/s | %dW (Износ: %d%%)\n",
                        i + 1, a.getTitle(), a.getBasePrice(), a.getBaseMhs(), a.getPowerWatts(), 100 - a.getInitialCondition());
            }
            System.out.print("Выберите ASIC > ");
            try {
                int idx = Integer.parseInt(sc.nextLine().trim()) - 1;
                if (idx >= 0 && idx < asics.length) {
                    ShopService.PurchaseResult res = shop.buyAsic(player, asics[idx]);
                    printPurchaseResult(res);
                }
            } catch (Exception ignored) {}
        }
    }

    private static void printPurchaseResult(ShopService.PurchaseResult res) {
        switch (res) {
            case SUCCESS -> System.out.println("✅ Оборудование куплено и подключено!");
            case NOT_ENOUGH_MONEY -> System.out.println("❌ Не хватает средств!");
            case NO_SLOTS_AVAILABLE -> System.out.println("❌ Нет мест в помещении!");
            case ASIC_NOT_ALLOWED_HERE -> System.out.println("❌ В спальне запрещено ставить ASIC! Арендуйте Гараж или Ангар.");
            default -> {}
        }
    }

    private static void openRealEstateMenu(Scanner sc, Player player, ShopService shop) {
        System.out.println("\n--- АРЕНДА ПОМЕЩЕНИЙ ---");
        LocationType[] locations = LocationType.values();
        for (int i = 0; i < locations.length; i++) {
            LocationType loc = locations[i];
            boolean isCur = loc == player.getCurrentLocation();
            System.out.printf("[%d] %s %s | Слоты: %d | Лимит: %dW | Свет: $%s/кВт·ч | Аренда: $%s/день | ASIC: %s\n",
                    i + 1, loc.getTitle(), (isCur ? "(ТЕКУЩАЯ)" : ""),
                    loc.getMaxSlots(), loc.getMaxPowerWatts(), loc.getElectricityRate(), loc.getDailyRent(),
                    loc.isAsicAllowed() ? "Да" : "Запрещены");
        }
        System.out.print("Номер для переезда > ");
        try {
            int idx = Integer.parseInt(sc.nextLine().trim()) - 1;
            if (idx >= 0 && idx < locations.length) {
                BigDecimal fee = new BigDecimal("100.00");
                System.out.print("Стоимость переезда $" + fee + ". Подтвердить? (y/n): ");
                if (sc.nextLine().trim().equalsIgnoreCase("y")) {
                    if (shop.rentLocation(player, locations[idx], fee)) {
                        System.out.println("🚚 Переезд завершён!");
                    } else {
                        System.out.println("❌ Ошибка переезда (проверьте деньги и лимит слотов).");
                    }
                }
            }
        } catch (Exception ignored) {}
    }

    private static void openFarmManagementMenu(Scanner sc, Player player, ShopService shop) {
        List<MiningDevice> inv = player.getInventory();
        if (inv.isEmpty()) {
            System.out.println("\n⚠️ Ваша ферма пуста!");
            return;
        }

        System.out.println("\n--- СОСТОЯНИЕ ОБОРУДОВАНИЯ ---");
        for (int i = 0; i < inv.size(); i++) {
            MiningDevice d = inv.get(i);
            String status = d.isBroken() ? "💥 СЛОМАНА" : (d.getConditionPercent() < 30 ? "⚠️ ИЗНОШЕНА" : "🟢 ОК");
            System.out.printf("[%d] %s | Ресурс: %d%% | %s | Ремонт: $%s | Б/У: $%s\n",
                    i + 1, d.getName(), d.getConditionPercent(), status, d.getRepairCost(), d.getScrapValue());
        }
        System.out.print("Выберите устройство > ");
        try {
            int idx = Integer.parseInt(sc.nextLine().trim()) - 1;
            if (idx >= 0 && idx < inv.size()) {
                MiningDevice d = inv.get(idx);
                System.out.println("[1] Починить ($" + d.getRepairCost() + ")");
                System.out.println("[2] Продать на б/у ($" + d.getScrapValue() + ")");
                System.out.println("[0] Назад");
                System.out.print("> ");
                switch (sc.nextLine().trim()) {
                    case "1" -> {
                        if (shop.repairDevice(player, d)) System.out.println("🔧 Отремонтировано!");
                        else System.out.println("❌ Не хватает денег или ремонт не нужен.");
                    }
                    case "2" -> {
                        BigDecimal money = shop.sellDevice(player, d);
                        System.out.println("💰 Продано за $" + money);
                    }
                }
            }
        } catch (Exception ignored) {}
    }
}