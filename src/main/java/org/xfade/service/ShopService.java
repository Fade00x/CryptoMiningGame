package org.xfade.service;

import org.xfade.domain.*;

import java.math.BigDecimal;

public class ShopService {
    public enum PurchaseResult {
        SUCCESS,
        NOT_ENOUGH_MONEY,
        NO_SLOTS_AVAILABLE,
        ASIC_NOT_ALLOWED_HERE
    }

    // Покупка видеокарты
    public PurchaseResult buyGpu(Player player, GpuType gpuType) {
        if (player.getUsdBalance().compareTo(gpuType.getBasePrice()) < 0) {
            return PurchaseResult.NOT_ENOUGH_MONEY;
        }

        if (player.getInventory().size() >= player.getCurrentLocation().getMaxSlots()) {
            return PurchaseResult.NO_SLOTS_AVAILABLE;
        }

        player.withdrawUsd(gpuType.getBasePrice());
        player.getInventory().add(new GpuDevice(
                gpuType.getTitle(),
                gpuType.getBaseMhs(),
                gpuType.getPowerWatts(),
                gpuType.getBasePrice()
        ));

        return PurchaseResult.SUCCESS;
    }

    // Покупка ASIC-майнера
    public PurchaseResult buyAsic(Player player, AsicType asicType) {
        // Проверка: разрешено ли ставить ASIC в текущей локации (в спальне нельзя)
        if (!player.getCurrentLocation().isAsicAllowed()) {
            return PurchaseResult.ASIC_NOT_ALLOWED_HERE;
        }

        if (player.getUsdBalance().compareTo(asicType.getBasePrice()) < 0) {
            return PurchaseResult.NOT_ENOUGH_MONEY;
        }

        if (player.getInventory().size() >= player.getCurrentLocation().getMaxSlots()) {
            return PurchaseResult.NO_SLOTS_AVAILABLE;
        }

        player.withdrawUsd(asicType.getBasePrice());
        player.getInventory().add(new AsicDevice(
                asicType.getTitle(),
                asicType.getBaseMhs(),
                asicType.getPowerWatts(),
                asicType.getBasePrice(),
                asicType.getInitialCondition()
        ));

        return PurchaseResult.SUCCESS;
    }

    // Переезд в новое помещение
    public boolean rentLocation(Player player, LocationType newLocation, BigDecimal movingCost) {
        if (player.getCurrentLocation() == newLocation) {
            return false;
        }

        // Если текущие устройства не влезают в новую локацию
        if (player.getInventory().size() > newLocation.getMaxSlots()) {
            return false;
        }

        // Если пытаемся переехать в локацию без поддержки ASIC, имея ASIC на руках
        if (!newLocation.isAsicAllowed()) {
            boolean hasAsic = player.getInventory().stream()
                    .anyMatch(device -> device instanceof AsicDevice);
            if (hasAsic) {
                return false;
            }
        }

        if (player.withdrawUsd(movingCost)) {
            player.setCurrentLocation(newLocation);
            return true;
        }

        return false;
    }

    // Ремонт оборудования
    public boolean repairDevice(Player player, MiningDevice device) {
        if (!device.isBroken() && device.getConditionPercent() >= 85) {
            return false; // Ремонт не требуется
        }

        BigDecimal cost = device.getRepairCost();
        if (player.withdrawUsd(cost)) {
            device.repair();
            return true;
        }

        return false;
    }

    // Сдать устройство на б/у рынок
    public BigDecimal sellDevice(Player player, MiningDevice device) {
        BigDecimal scrapValue = device.getScrapValue();
        player.getInventory().remove(device);
        player.depositUsd(scrapValue);
        return scrapValue;
    }
}