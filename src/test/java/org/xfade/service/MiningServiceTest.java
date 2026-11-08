package org.xfade.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.xfade.domain.GpuDevice;
import org.xfade.domain.GpuType;
import org.xfade.domain.LocationType;
import org.xfade.domain.Player;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class MiningServiceTest {
    private MiningService miningService;
    private Player player;

    @BeforeEach
    void setUp() {
        miningService = new MiningService();
        player = new Player("TestMiner");
    }

    @Test
    @DisplayName("Добыча BTC равна 0, если у игрока нет оборудования")
    void testDailyMinedBtcWithEmptyInventory() {
        BigDecimal mined = miningService.calculateDailyMinedBtc(player);
        assertEquals(0, mined.compareTo(BigDecimal.ZERO));
    }

    @Test
    @DisplayName("Корректный расчёт добычи BTC для видеокарты")
    void testDailyMinedBtcWithGpu() {
        GpuType card = GpuType.GTX_1060; // 25 MH/s
        player.getInventory().add(new GpuDevice(card.getTitle(), card.getBaseMhs(), card.getPowerWatts(), card.getBasePrice()));

        // 25 * 0.00001 = 0.00025 BTC
        BigDecimal expected = new BigDecimal("0.00025");
        BigDecimal mined = miningService.calculateDailyMinedBtc(player);

        assertEquals(0, expected.compareTo(mined));
    }

    @Test
    @DisplayName("Счёт за свет в спальне должен быть $0.00")
    void testFreeElectricityInBedroom() {
        player.setCurrentLocation(LocationType.BEDROOM);
        GpuType card = GpuType.RTX_4090; // 450W
        player.getInventory().add(new GpuDevice(card.getTitle(), card.getBaseMhs(), card.getPowerWatts(), card.getBasePrice()));

        BigDecimal cost = miningService.calculateDailyElectricityCost(player);
        assertEquals(0, BigDecimal.ZERO.compareTo(cost));
    }
}
