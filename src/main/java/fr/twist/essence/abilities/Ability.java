package fr.twist.essence.abilities;

import org.bukkit.ChatColor;
import org.bukkit.Material;

import java.util.List;

public enum Ability {
    FIRE("feu", ChatColor.GOLD + "Feu", Material.BLAZE_POWDER, List.of("RÃ©sistance au feu permanente")),
    ICE("ice", ChatColor.AQUA + "Ice", Material.PACKED_ICE, List.of("Freeze l'adversaire 15s", "GrÃ¢ce du dauphin II permanente")),
    WIND("wind", ChatColor.WHITE + "Wind", Material.FEATHER, List.of("Pas de dÃ©gÃ¢ts de chute", "Ã‰clair toutes les 2 attaques")),
    BLINDNESS("blindness", ChatColor.DARK_PURPLE + "Blindness", Material.ENDER_EYE, List.of("Blindness sur l'adversaire", "InvisibilitÃ© permanente")),
    EARTH("earth", ChatColor.DARK_GREEN + "Earth", Material.WOLF_SPAWN_EGG, List.of("Fait apparaÃ®tre 15 loups", "Force II sur les loups")),
    MASSACREUR("massacreur", ChatColor.RED + "Massacreur", Material.MACE, List.of("Quand tu tapes avec une Mace", "Elytra sur le dos 5s"));

    public static final int COST = 19;

    private final String id;
    private final String displayName;
    private final Material icon;
    private final List<String> description;

    Ability(String id, String displayName, Material icon, List<String> description) {
        this.id = id;
        this.displayName = displayName;
        this.icon = icon;
        this.description = description;
    }

    public String id() {
        return id;
    }

    public String displayName() {
        return displayName;
    }

    public Material icon() {
        return icon;
    }

    public List<String> description() {
        return description;
    }

    public static Ability fromId(String id) {
        for (Ability a : values()) {
            if (a.id.equalsIgnoreCase(id)) {
                return a;
            }
        }
        return null;
    }
}
