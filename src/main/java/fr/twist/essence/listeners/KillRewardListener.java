package fr.twist.essence.listeners;

import fr.twist.essence.EssenceItem;
import fr.twist.essence.TwistEssencePlugin;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public final class KillRewardListener implements Listener {
    private final TwistEssencePlugin plugin;

    public KillRewardListener(TwistEssencePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player dead = event.getEntity();
        Player killer = dead.getKiller();
        if (killer == null) {
            return;
        }
        int before = plugin.essenceItem().count(killer);
        int given = plugin.essenceItem().give(killer, 1);
        if (given <= 0) {
            killer.sendMessage(ChatColor.RED + "Kill: pas d'essence gagnÃ©e (max " + EssenceItem.MAX_ESSENCE + ").");
            return;
        }
        int after = plugin.essenceItem().count(killer);
        killer.sendMessage(ChatColor.GREEN + "+1 essence (kill) (" + after + "/" + EssenceItem.MAX_ESSENCE + ")");
    }
}
