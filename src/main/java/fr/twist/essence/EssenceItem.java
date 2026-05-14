package fr.twist.essence;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public final class EssenceItem {
    public static final int MAX_ESSENCE = 20;

    private final TwistEssencePlugin plugin;

    public EssenceItem(TwistEssencePlugin plugin) {
        this.plugin = plugin;
    }

    public ItemStack create(int amount) {
        ItemStack item = new ItemStack(Material.NETHER_STAR, amount);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.LIGHT_PURPLE + "Essence");
        meta.setCustomModelData(1);
        meta.getPersistentDataContainer().set(plugin.essenceKey(), PersistentDataType.BYTE, (byte) 1);
        item.setItemMeta(meta);
        return item;
    }

    public boolean isEssence(ItemStack item) {
        if (item == null || item.getType() != Material.NETHER_STAR) {
            return false;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return false;
        }
        return meta.getPersistentDataContainer().has(plugin.essenceKey(), PersistentDataType.BYTE);
    }

    public int count(Player player) {
        int total = 0;
        for (ItemStack item : player.getInventory().getContents()) {
            if (isEssence(item)) {
                total += item.getAmount();
            }
        }
        return total;
    }

    public int give(Player player, int amount) {
        if (amount <= 0) {
            return 0;
        }
        int current = count(player);
        int canGive = Math.min(amount, MAX_ESSENCE - current);
        if (canGive <= 0) {
            return 0;
        }

        PlayerInventory inv = player.getInventory();
        int remaining = canGive;
        while (remaining > 0) {
            int stack = Math.min(remaining, 64);
            inv.addItem(create(stack)).values().forEach(leftover -> player.getWorld().dropItemNaturally(player.getLocation(), leftover));
            remaining -= stack;
        }
        return canGive;
    }

    public boolean take(Player player, int amount) {
        if (amount <= 0) {
            return true;
        }
        if (count(player) < amount) {
            return false;
        }
        int remaining = amount;
        PlayerInventory inv = player.getInventory();
        ItemStack[] contents = inv.getContents();
        for (int i = 0; i < contents.length && remaining > 0; i++) {
            ItemStack item = contents[i];
            if (!isEssence(item)) {
                continue;
            }
            int take = Math.min(item.getAmount(), remaining);
            item.setAmount(item.getAmount() - take);
            remaining -= take;
            if (item.getAmount() <= 0) {
                contents[i] = null;
            }
        }
        inv.setContents(contents);
        return true;
    }
}
