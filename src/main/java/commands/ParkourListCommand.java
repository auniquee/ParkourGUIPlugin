package commands;

import me.jonis.database_test.Database_test;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import utils.InventoryFunctions;

import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

public class ParkourListCommand implements CommandExecutor {
    private final Database_test plugin;

    public ParkourListCommand(Database_test plugin){
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(sender instanceof Player p){
            Inventory menu = Bukkit.createInventory(p,27,"Parkour");

        plugin.saveDefaultConfig();
        FileConfiguration config = plugin.getConfig();
        DateTimeFormatter fmt = DateTimeFormatter.ISO_ZONED_DATE_TIME;
        String eventStr = "2024-12-15T23:59:59Z";


        Instant nextSundayDate = fmt.parse(eventStr, Instant::from);
        //System.out.println(config.getString("veckansParkour"));
        //sender.sendMessage(config.getString("veckansParkour"));

        menu.setItem(4, InventoryFunctions.makeItem(
            ChatColor.AQUA + "Veckans Parkour",
                List.of(
                        ChatColor.GRAY + "Parkour: " + ChatColor.YELLOW + config.getString("veckansParkour.namn"),
                        ChatColor.GRAY + "Pris: " + ChatColor.YELLOW + config.getString("veckansParkour.pris"),
                        ChatColor.GRAY + "Avslutas om: " + ChatColor.YELLOW + Duration.between(Instant.now(), nextSundayDate),
                        "",                                     // vvvv hur ska vi göra? kanske lägga till §colorcode i configen?
                        ChatColor.GRAY + "Svårighetsgrad: " + ChatColor.DARK_PURPLE + config.getString("veckansParkour.svårighetsgrad"),
                        ChatColor.GRAY + "Personligt rekord: " + ChatColor.YELLOW + "N/A",
                        ChatColor.GRAY + "Antal klarade: " + ChatColor.YELLOW + "N/A"
                ),
                new ItemStack(Material.BARRIER)
        ));

        p.openInventory(menu);




        }else{
            sender.sendMessage(ChatColor.RED + "Bara spelare kan använda detta kommando");
        }


        return true;

    }


}
