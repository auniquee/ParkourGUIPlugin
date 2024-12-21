package commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import utils.InventoryFunctions;

public class ViewStats implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length >= 1) {
            InventoryFunctions.DisplayPlayerStats(Bukkit.getOfflinePlayer(args[0]), (Player) sender);
        }else{
            sender.sendMessage(ChatColor.RED + "Använding: /stats [spelarnamn]");
        }
        return true;
    }
}
