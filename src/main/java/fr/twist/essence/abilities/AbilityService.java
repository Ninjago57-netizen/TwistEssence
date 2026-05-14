package fr.twist.essence.abilities;

import fr.twist.essence.EssenceItem;
import fr.twist.essence.TwistEssencePlugin;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class AbilityService {
    private final TwistEssencePlugin plugin;

    public AbilityService(TwistEssencePlugin plugin) {
        this.plugin = plugin;
    }

    public boolean has(Player player, Ability ability) {
        PersistentDataContainer pdc = player.getPersistentDataContainer();
        return pdc.has(key(ability), PersistentDataType.BYTE);
    }

    public void unlock(Player player, Ability ability) {
        player.getPersistentDataContainer().set(key(ability), PersistentDataType.BYTE, (byte) 1);
    }

    public void applyPassives(Player player, boolean notify) {
        boolean changed = false;
        if (has(player, Ability.FIRE)) {
            changed |= applyEffect(player, PotionEffectType.FIRE_RESISTANCE, 0);
        }
        if (has(player, Ability.ICE)) {
            changed |= applyEffect(player, PotionEffectType.DOLPHINS_GRACE, 1);
        }
        if (has(player, Ability.BLINDNESS)) {
            changed |= applyEffect(player, PotionEffectType.INVISIBILITY, 0);
        }
        if (notify && changed) {
            player.sendMessage(ChatColor.GRAY + "Tes effets permanents ont Ã©tÃ© remis.");
        }
    }

    public boolean purchase(Player player, Ability ability) {
        if (has(player, ability)) {
            player.sendMessage(ChatColor.RED + "Tu as dÃ©jÃ  cette abilitÃ©.");
            return false;
        }

        EssenceItem essence = plugin.essenceItem();
        int current = essence.count(player);
        if (current < Ability.COST) {
            player.sendMessage(ChatColor.RED + "Il te faut " + Ability.COST + " essences (tu en as " + current + ").");
            return false;
        }

        if (!essence.take(player, Ability.COST)) {
            player.sendMessage(ChatColor.RED + "Impossible de retirer les essences.");
            return false;
        }

        unlock(player, ability);
        onUnlock(player, ability);
        player.sendMessage(ChatColor.GREEN + "Achat rÃ©ussi: " + ability.displayName() + ChatColor.GREEN + " (-" + Ability.COST + " essences)");
        return true;
    }

    private void onUnlock(Player player, Ability ability) {
        switch (ability) {
            case FIRE -> applyPassives(player, false);
            case ICE -> applyPassives(player, false);
            case WIND -> {}
            case BLINDNESS -> applyPassives(player, false);
            case EARTH -> spawnWolves(player);
            case MASSACREUR -> {}
        }
    }

    private boolean applyEffect(Player player, PotionEffectType type, int amplifier) {
        PotionEffect existing = player.getPotionEffect(type);
        if (existing != null && existing.getAmplifier() == amplifier && existing.getDuration() > 20 * 60) {
            return false;
        }
        PotionEffect effect = new PotionEffect(type, Integer.MAX_VALUE, amplifier, false, false, false);
        player.addPotionEffect(effect);
        return true;
    }

    private void spawnWolves(Player player) {
        for (int i = 0; i < 15; i++) {
            Wolf wolf = player.getWorld().spawn(player.getLocation(), Wolf.class, w -> {
                w.setAdult();
                w.setOwner(player);
                double maxHealth = 20.0;
                if (w.getAttribute(Attribute.GENERIC_MAX_HEALTH) != null) {
                    w.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(40.0);
                    maxHealth = w.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue();
                }
                w.setHealth(maxHealth);
                w.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, Integer.MAX_VALUE, 1, false, true, true));
            });
            if (wolf.getOwner() == null) {
                wolf.setOwner(player);
            }
        }
        player.sendMessage(ChatColor.GREEN + "15 loups invoquÃ©s.");
    }

    public NamespacedKey key(Ability ability) {
        return new NamespacedKey(plugin, "ability_" + ability.id());
    }
}
