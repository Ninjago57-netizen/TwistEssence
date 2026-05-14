package fr.twist.essence.listeners;

import fr.twist.essence.EssenceItem;
import fr.twist.essence.TwistEssencePlugin;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemStack;

public final class EssenceLimitListener implements Listener {
    private final TwistEssencePlugin plugin;

    public EssenceLimitListener(TwistEssencePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPickup(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        ItemStack stack = event.getItem().getItemStack();
        if (!plugin.essenceItem().isEssence(stack)) {
            return;
        }
        int current = plugin.essenceItem().count(player);
        if (current >= EssenceItem.MAX_ESSENCE) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "Tu es dÃ©jÃ  au maximum d'essences (20).");
        }
    }
}
