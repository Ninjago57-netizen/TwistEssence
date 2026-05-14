package fr.twist.essence.listeners;

import fr.twist.essence.TwistEssencePlugin;
import fr.twist.essence.abilities.Ability;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class AbilityCombatListener implements Listener {
    private final TwistEssencePlugin plugin;
    private final Map<UUID, Integer> windHits = new HashMap<>();
    private final Map<UUID, ElytraState> elytraStates = new HashMap<>();

    public AbilityCombatListener(TwistEssencePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onFall(EntityDamageEvent event) {
        if (event.getCause() != EntityDamageEvent.DamageCause.FALL) {
            return;
        }
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        if (!plugin.abilityService().has(player, Ability.WIND)) {
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void onHit(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player attacker)) {
            return;
        }
        Entity entity = event.getEntity();
        if (!(entity instanceof LivingEntity target)) {
            return;
        }

        if (plugin.abilityService().has(attacker, Ability.ICE)) {
            target.setFreezeTicks(20 * 15);
            target.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 20 * 15, 10, false, true, true));
        }

        if (plugin.abilityService().has(attacker, Ability.BLINDNESS)) {
            target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20 * 5, 0, false, true, true));
        }

        if (plugin.abilityService().has(attacker, Ability.WIND)) {
            int hits = windHits.getOrDefault(attacker.getUniqueId(), 0) + 1;
            if (hits >= 2) {
                target.getWorld().strikeLightning(target.getLocation());
                hits = 0;
            }
            windHits.put(attacker.getUniqueId(), hits);
        }

        if (plugin.abilityService().has(attacker, Ability.MASSACREUR)) {
            ItemStack hand = attacker.getInventory().getItemInMainHand();
            if (hand.getType() == Material.MACE) {
                applyElytra(attacker);
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        UUID id = event.getPlayer().getUniqueId();
        windHits.remove(id);
        ElytraState state = elytraStates.remove(id);
        if (state != null && state.task != null) {
            state.task.cancel();
        }
    }

    private void applyElytra(Player player) {
        UUID id = player.getUniqueId();
        ElytraState state = elytraStates.get(id);
        if (state == null) {
            state = new ElytraState(cloneNullable(player.getInventory().getChestplate()));
            elytraStates.put(id, state);
        }

        if (state.task != null) {
            state.task.cancel();
        }

        player.getInventory().setChestplate(new ItemStack(Material.ELYTRA));
        state.task = plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            ItemStack current = player.getInventory().getChestplate();
            if (current != null && current.getType() == Material.ELYTRA) {
                player.getInventory().setChestplate(cloneNullable(state.original));
            }
            elytraStates.remove(id);
        }, 20L * 5);
    }

    private ItemStack cloneNullable(ItemStack item) {
        return item == null ? null : item.clone();
    }

    private static final class ElytraState {
        private final ItemStack original;
        private BukkitTask task;

        private ElytraState(ItemStack original) {
            this.original = original;
        }
    }
}
