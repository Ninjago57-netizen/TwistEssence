package fr.twist.essence;

import fr.twist.essence.abilities.AbilityService;
import fr.twist.essence.commands.EssenceCommand;
import fr.twist.essence.gui.ShopGui;
import fr.twist.essence.listeners.AbilityCombatListener;
import fr.twist.essence.listeners.AbilityJoinListener;
import fr.twist.essence.listeners.EssenceLimitListener;
import fr.twist.essence.listeners.KillRewardListener;
import fr.twist.essence.listeners.ShopClickListener;
import fr.twist.essence.listeners.ShopOpenListener;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

public final class TwistEssencePlugin extends JavaPlugin {
    private NamespacedKey essenceKey;
    private NamespacedKey shopItemKey;
    private EssenceItem essenceItem;
    private AbilityService abilityService;
    private ShopGui shopGui;

    @Override
    public void onEnable() {
        this.essenceKey = new NamespacedKey(this, "essence");
        this.shopItemKey = new NamespacedKey(this, "shop_item");
        this.essenceItem = new EssenceItem(this);
        this.abilityService = new AbilityService(this);
        this.shopGui = new ShopGui(this);

        getServer().getPluginManager().registerEvents(new EssenceLimitListener(this), this);
        getServer().getPluginManager().registerEvents(new KillRewardListener(this), this);
        getServer().getPluginManager().registerEvents(new ShopOpenListener(this), this);
        getServer().getPluginManager().registerEvents(new ShopClickListener(this), this);
        getServer().getPluginManager().registerEvents(new AbilityCombatListener(this), this);
        getServer().getPluginManager().registerEvents(new AbilityJoinListener(this), this);

        if (getCommand("essence") != null) {
            getCommand("essence").setExecutor(new EssenceCommand(this));
        }

        getServer().getOnlinePlayers().forEach(p -> abilityService.applyPassives(p, false));
    }

    public NamespacedKey essenceKey() {
        return essenceKey;
    }

    public NamespacedKey shopItemKey() {
        return shopItemKey;
    }

    public EssenceItem essenceItem() {
        return essenceItem;
    }

    public AbilityService abilityService() {
        return abilityService;
    }

    public ShopGui shopGui() {
        return shopGui;
    }
}
