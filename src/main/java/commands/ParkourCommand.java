package commands;

import database.DatabaseManager;
import database.RetreiveParkours;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import utils.InventoryFunctions;
import utils.ParkourUser;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

public class ParkourCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        //String parkours[] = {"alperna", "akromatiskt", "adventure", "among_us", "backen", "grottan", "sjukhuset", "mardröm"};

        if (args.length >= 1) {
            ArrayList<ParkourUser> time_top = new ArrayList<ParkourUser>();
            ArrayList<ParkourUser> plays_top = new ArrayList<ParkourUser>();
            Player p = (Player) sender;
            int page = 0;
            String parkour = args[0];
            if(args.length >=2){
                page = Integer.parseInt(args[1]) - 1;
            }
            InventoryFunctions.displayStats(parkour, p, page);
        } else {
            sender.sendMessage(ChatColor.RED + "Usage: /parkour [parkour_name]");
        }

        return true;
    }
}
