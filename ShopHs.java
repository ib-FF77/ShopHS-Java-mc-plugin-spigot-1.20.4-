package com.ibff77.shophs;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class ShopHs extends JavaPlugin {

    public static final int MAX_ITEMS = 54;

    private ShopManager shopManager;
    private EconomyHook economy;
    private ShopGUI gui;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        shopManager = new ShopManager(this);
        shopManager.load();

        economy = new EconomyHook(this);
        if (!economy.setup()) {
            getLogger().warning("Vault یا یک پلاگین اقتصادی پیدا نشد؛ خرید غیرفعاله.");
        }

        gui = new ShopGUI(this);
        getServer().getPluginManager().registerEvents(gui, this);

        getLogger().info("ShopHs enabled. Create by ib-FF77");
    }

    public ShopManager getShopManager() {
        return shopManager;
    }

    public EconomyHook getEconomy() {
        return economy;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("این فرمان فقط برای بازیکن‌هاست.");
            return true;
        }

        if (args.length == 0) {
            gui.open(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "add" -> handleAdd(player, args);
            case "remove" -> handleRemove(player, args);
            case "info" -> player.sendMessage(ChatColor.GOLD + "Create by ib-FF77");
            default -> player.sendMessage(ChatColor.RED + "دستور نامعتبره. از /shophs info استفاده کن.");
        }
        return true;
    }

    private void handleAdd(Player player, String[] args) {
        if (!player.hasPermission("shophs.admin")) {
            player.sendMessage(ChatColor.RED + "دسترسی نداری.");
            return;
        }
        if (args.length < 2) {
            player.sendMessage(ChatColor.YELLOW + "استفاده: /shophs add <price>");
            return;
        }

        double price;
        try {
            price = Double.parseDouble(args[1]);
        } catch (NumberFormatException e) {
            price = -1;
        }
        if (!Double.isFinite(price) || price < 0) {
            player.sendMessage(ChatColor.RED + "قیمت باید عدد مثبت باشه.");
            return;
        }

        if (shopManager.size() >= MAX_ITEMS) {
            player.sendMessage(ChatColor.RED + "شاپ پره (حداکثر " + MAX_ITEMS + " آیتم).");
            return;
        }

        ItemStack hand = player.getInventory().getItemInMainHand();
        if (hand.getType().isAir()) {
            player.sendMessage(ChatColor.RED + "یه آیتم تو دستت نگرفتی.");
            return;
        }

        shopManager.add(new ShopData(hand, price));
        shopManager.save();
        player.sendMessage(ChatColor.GREEN + hand.getType().toString() + " x" + hand.getAmount()
                + " با قیمت " + economy.format(price) + " به شاپ اضافه شد.");
    }

    private void handleRemove(Player player, String[] args) {
        if (!player.hasPermission("shophs.admin")) {
            player.sendMessage(ChatColor.RED + "دسترسی نداری.");
            return;
        }
        if (args.length < 2) {
            player.sendMessage(ChatColor.YELLOW + "استفاده: /shophs remove <row>");
            return;
        }

        int row;
        try {
            row = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            row = -1;
        }
        if (row < 1 || row > shopManager.size()) {
            player.sendMessage(ChatColor.RED + "ردیف معتبر نیست. تعداد ردیف‌ها: " + shopManager.size());
            return;
        }

        ShopData removed = shopManager.remove(row - 1);
        shopManager.save();
        player.sendMessage(ChatColor.GREEN + "ردیف " + row + " (" + removed.getItem().getType() + ") حذف شد.");
    }
}
