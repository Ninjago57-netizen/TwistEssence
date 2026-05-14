package fr.twist.essence.listeners;

import fr.twist.essence.TwistEssencePlugin;
import fr.twist.essence.abilities.Ability;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public final class ShopClickListener implements Listener {
    private final TwistEssencePlugin plugin;

    public ShopClickListener(TwistEssencePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        if (!plugin.shopGui().isShopInventory(event.getInventory())) {
            return;
        }
        event.setCancelled(true);
        Ability ability = plugin.shopGui().getAbility(event.getCurrentItem());
        if (ability == null) {
            return;
        }
        boolean ok = plugin.abilityService().purchase(player, ability);
        if (ok) {
            plugin.shopGui().open(player);
        }
    }
}
