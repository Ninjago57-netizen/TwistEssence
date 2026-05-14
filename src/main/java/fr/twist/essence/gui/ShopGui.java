package fr.twist.essence.gui;

import fr.twist.essence.TwistEssencePlugin;
import fr.twist.essence.abilities.Ability;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public final class ShopGui {
    private final TwistEssencePlugin plugin;

    public ShopGui(TwistEssencePlugin plugin) {
        this.plugin = plugin;
    }

    public void open(Player player) {
        Inventory inv = Bukkit.createInventory(new Holder(), 27, ChatColor.DARK_PURPLE + "Essence Shop");
        int[] slots = {10, 11, 12, 14, 15, 16};
        Ability[] abilities = Ability.values();
        for (int i = 0; i < abilities.length && i < slots.length; i++) {
            inv.setItem(slots[i], buttonItem(player, abilities[i]));
        }
        inv.setItem(22, infoItem(player));
        player.openInventory(inv);
    }

    public boolean isShopInventory(Inventory inv) {
        return inv != null && inv.getHolder() instanceof Holder;
    }

    public Ability getAbility(ItemStack clicked) {
        if (clicked == null || !clicked.hasItemMeta()) {
            return null;
        }
        ItemMeta meta = clicked.getItemMeta();
        String id = meta.getPersistentDataContainer().get(plugin.shopItemKey(), PersistentDataType.STRING);
        if (id == null) {
            return null;
        }
        return Ability.fromId(id);
    }

    private ItemStack buttonItem(Player player, Ability ability) {
        ItemStack item = new ItemStack(ability.icon());
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ability.displayName());
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "CoÃ»t: " + ChatColor.LIGHT_PURPLE + Ability.COST + " essences");
        lore.add(ChatColor.DARK_GRAY + "Max essence: 20");
        lore.add(" ");
        for (String line : ability.description()) {
            lore.add(ChatColor.WHITE + "- " + ChatColor.GRAY + line);
        }
        lore.add(" ");
        boolean owned = plugin.abilityService().has(player, ability);
        lore.add(owned ? ChatColor.RED + "DÃ©jÃ  achetÃ©" : ChatColor.GREEN + "Clique pour acheter");
        meta.setLore(lore);
        meta.getPersistentDataContainer().set(plugin.shopItemKey(), PersistentDataType.STRING, ability.id());
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack infoItem(Player player) {
        ItemStack item = new ItemStack(Material.NETHER_STAR);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.LIGHT_PURPLE + "Tes essences");
        List<String> lore = List.of(
                ChatColor.GRAY + "Essences: " + ChatColor.LIGHT_PURPLE + plugin.essenceItem().count(player) + ChatColor.GRAY + "/20",
                ChatColor.DARK_GRAY + "Kill joueur: +1 essence",
                ChatColor.DARK_GRAY + "Shift + clic droit Beacon + carotte d'or"
        );
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    public static final class Holder implements InventoryHolder {
        @Override
        public Inventory getInventory() {
            return Bukkit.createInventory(this, 27);
        }
    }
}
