package com.ibff77.shophs;

import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ShopManager {

    private static final String KEY = "shop";

    private final ShopHs plugin;
    private final List<ShopData> items = new ArrayList<>();

    public ShopManager(ShopHs plugin) {
        this.plugin = plugin;
    }

    public void load() {
        items.clear();
        for (Map<?, ?> map : plugin.getConfig().getMapList(KEY)) {
            Object item = map.get("item");
            Object price = map.get("price");
            if (item instanceof ItemStack stack && price instanceof Number number) {
                items.add(new ShopData(stack, number.doubleValue()));
            }
        }
    }

    public void save() {
        List<Map<String, Object>> list = new ArrayList<>();
        for (ShopData data : items) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("item", data.getItem());
            map.put("price", data.getPrice());
            list.add(map);
        }
        plugin.getConfig().set(KEY, list);
        plugin.saveConfig();
    }

    public void add(ShopData data) {
        items.add(data);
    }

    public ShopData remove(int index) {
        return items.remove(index);
    }

    public List<ShopData> getItems() {
        return Collections.unmodifiableList(items);
    }

    public int size() {
        return items.size();
    }
}
