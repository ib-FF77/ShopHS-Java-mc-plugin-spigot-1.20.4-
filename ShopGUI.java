package com.ibff77.shophs;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.PlayerInventory;

import java.util.List;

public class ShopGUI implements Listener {

    private static final String TITLE = ChatColor.GOLD + "Shop";

    private final ShopHs plugin;

    public ShopGUI(ShopHs plugin) {
        this.plugin = plugin;
    }

    /** Holder used to identify our shop inventory reliably. */
    private static final class ShopHolder implements InventoryHolder {
        private Inventory inventory;

        @Override
        public Inventory getInventory() {
            return inventory;
        }
    }

    public void open(Player player) {
        List<ShopData> items = plugin.getShopManager().getItems();
        int size = Math.min(54, Math.max(9, (int) Math.ceil(items.size() / 9.0) * 9));

        ShopHolder holder = new ShopHolder();
        Inventory inv = Bukkit.createInventory(holder, size, TITLE);
        holder.inventory = inv;

        for (int i = 0; i < items.size() && i < size; i++) {
            ShopData data = items.get(i);
            ItemStack display = data.getItem();
            ItemMeta meta = display.getItemMeta();
            if (meta != null) {
                meta.setLore(List.of(
                        ChatColor.YELLOW + "قیمت: " + plugin.getEconomy().format(data.getPrice()),
                        ChatColor.GRAY + "ردیف: " + (i + 1),
                        ChatColor.GREEN + "برای خرید کلیک کن"
                ));
                display.setItemMeta(meta);
            }
            inv.setItem(i, display);
        }
        player.openInventory(inv);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Inventory top = event.getView().getTopInventory();
        if (!(top.getHolder() instanceof ShopHolder)) return;

        // Block every interaction with the shop menu, including shift-clicks from the player's inventory.
        event.setCancelled(true);

        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (event.getClickedInventory() == null || !event.getClickedInventory().equals(top)) return;

        int slot = event.getSlot();
        List<ShopData> items = plugin.getShopManager().getItems();
        if (slot < 0 || slot >= items.size()) return;

        buy(player, items.get(slot));
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        if (event.getView().getTopInventory().getHolder() instanceof ShopHolder) {
            event.setCancelled(true);
        }
    }

    private void buy(Player player, ShopData data) {
        EconomyHook economy = plugin.getEconomy();
        if (!economy.isReady()) {
            player.sendMessage(ChatColor.RED + "اقتصاد فعال نیست؛ خرید ممکن نیست.");
            return;
        }

        ItemStack item = data.getItem();
        if (!hasSpace(player.getInventory(), item)) {
            player.sendMessage(ChatColor.RED + "جای خالی تو اینونتوریت کافی نیست.");
            return;
        }

        if (!economy.has(player, data.getPrice())) {
            player.sendMessage(ChatColor.RED + "پولت کافی نیست. قیمت: " + economy.format(data.getPrice()));
            return;
        }

        if (!economy.withdraw(player, data.getPrice())) {
            player.sendMessage(ChatColor.RED + "پرداخت انجام نشد.");
            return;
        }

        player.getInventory().addItem(item);
        player.sendMessage(ChatColor.GREEN + "خریدی: " + item.getType() + " x" + item.getAmount()
                + " به قیمت " + economy.format(data.getPrice()));
    }

    private boolean hasSpace(PlayerInventory inv, ItemStack item) {
        int remaining = item.getAmount();
        int max = item.getMaxStackSize();
        for (ItemStack slot : inv.getStorageContents()) {
            if (slot == null || slot.getType().isAir()) {
                remaining -= max;
            } else if (slot.isSimilar(item)) {
                remaining -= (max - slot.getAmount());
            }
            if (remaining <= 0) return true;
        }
        return false;
    }
}
