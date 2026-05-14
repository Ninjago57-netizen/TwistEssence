package fr.twist.essence.listeners;

import fr.twist.essence.TwistEssencePlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public final class AbilityJoinListener implements Listener {
    private final TwistEssencePlugin plugin;

    public AbilityJoinListener(TwistEssencePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        plugin.abilityService().applyPassives(event.getPlayer(), false);
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> plugin.abilityService().applyPassives(event.getPlayer(), false), 1L);
    }
}
