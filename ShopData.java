package com.ibff77.shophs;

import org.bukkit.inventory.ItemStack;

public final class ShopData {

    private final ItemStack item;
    private final double price;

    public ShopData(ItemStack item, double price) {
        this.item = item.clone();
        this.price = price;
    }

    public ItemStack getItem() {
        return item.clone();
    }

    public double getPrice() {
        return price;
    }
}
