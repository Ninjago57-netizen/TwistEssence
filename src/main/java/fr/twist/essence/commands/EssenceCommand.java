package fr.twist.essence.commands;

import fr.twist.essence.EssenceItem;
import fr.twist.essence.TwistEssencePlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class EssenceCommand implements CommandExecutor {
    private final TwistEssencePlugin plugin;

    public EssenceCommand(TwistEssencePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            if (sender instanceof Player player) {
                int count = plugin.essenceItem().count(player);
                sender.sendMessage(ChatColor.LIGHT_PURPLE + "Essences: " + ChatColor.WHITE + count + ChatColor.GRAY + "/" + EssenceItem.MAX_ESSENCE);
                return true;
            }
            sender.sendMessage(ChatColor.RED + "Usage: /essence give <joueur> <montant>");
            return true;
        }

        if (args[0].equalsIgnoreCase("give")) {
            if (!sender.hasPermission("twistessence.admin")) {
                sender.sendMessage(ChatColor.RED + "Pas la permission.");
                return true;
            }
            if (args.length < 3) {
                sender.sendMessage(ChatColor.RED + "Usage: /essence give <joueur> <montant>");
                return true;
            }
            Player target = Bukkit.getPlayerExact(args[1]);
            if (target == null) {
                sender.sendMessage(ChatColor.RED + "Joueur introuvable.");
                return true;
            }
            int amount;
            try {
                amount = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                sender.sendMessage(ChatColor.RED + "Montant invalide.");
                return true;
            }
            int given = plugin.essenceItem().give(target, amount);
            int after = plugin.essenceItem().count(target);
            sender.sendMessage(ChatColor.GREEN + "Essences donnÃ©es: " + given + " (" + after + "/" + EssenceItem.MAX_ESSENCE + ")");
            if (sender != target) {
                target.sendMessage(ChatColor.LIGHT_PURPLE + "Tu as reÃ§u " + given + " essence(s). (" + after + "/" + EssenceItem.MAX_ESSENCE + ")");
            }
            return true;
        }

        sender.sendMessage(ChatColor.RED + "Sous-commande inconnue.");
        return true;
    }
}
