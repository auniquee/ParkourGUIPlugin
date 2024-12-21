package utils;

import database.DatabaseManager;
import database.RetreiveParkours;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import javax.xml.transform.Result;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static database.RetreiveParkours.getPlayerRow;
import static database.RetreiveParkours.getRecordsTop;


public class InventoryFunctions {
    public static ItemStack makeItem(String name, List lore, ItemStack item){
        //ItemStack item = new ItemStack(item_type);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }
    private static ItemStack getHead(UUID uuid){
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        meta.setOwningPlayer(Bukkit.getOfflinePlayer(uuid));
        meta.setOwnerProfile(Bukkit.createPlayerProfile(uuid));
        item.setItemMeta(meta);
        return item;
    }
    public static void DisplayPlayerStats(OfflinePlayer target, Player p){
        //p.getUniqueId().toString()
        //System.out.println(target.getUniqueId().toString());
        Map<String, ParkourColumn> stats = RetreiveParkours.getPlayerRow(target.getUniqueId().toString());
        List<String> parkours = ConfigManager.getInstance().getParkourList();
        Map<String, Integer> speedrun_place = RetreiveParkours.getPlayerRanking(target.getUniqueId().toString());
        Inventory menu = Bukkit.createInventory(p, 45, target.getName() + "s statistik");
        DateFormat open_format = new SimpleDateFormat("HH:mm:ss:SSS");
        open_format.setTimeZone(TimeZone.getTimeZone("GMT"));

        int i = 0;

        for(int y = 1; y < 4; y++) { // 3 rows
            for(int x = 0; x < 7; x++) { // 7 cols
                //p.sendMessage(parkours.get(i));
                //p.sendMessage(parkours.get(i) + " completions: " + stats.get(parkours.get(i)).completions);
                menu.setItem(
                        y*9 + 1 + x,
                        makeItem(
                                ChatColor.YELLOW + parkours.get(i).substring(0, 1).toUpperCase() + parkours.get(i).substring(1),
                                List.of(
                                        ChatColor.YELLOW + "Plats: " + ChatColor.WHITE + speedrun_place.get(parkours.get(i)),
                                        ChatColor.YELLOW + "Tid: " + ChatColor.WHITE + open_format.format(stats.get(parkours.get(i)).time),
                                        ChatColor.YELLOW + "Antal klarade: " + ChatColor.WHITE + stats.get(parkours.get(i)).completions
                                ),
                                new ItemStack(Material.matchMaterial(ConfigManager.getInstance().getParkourBlock(parkours.get(i))))

                        )
                );

                i++;
                if(i >= parkours.size()){

                    menu.setItem(0,
                        makeItem(
                            ChatColor.YELLOW + target.getName(),
                                List.of(
                                        ChatColor.YELLOW + "Totala klarningar: " + ChatColor.WHITE + RetreiveParkours.totalPlayerCompletions(target),
                                        ChatColor.YELLOW + "gQmynt: " + ChatColor.WHITE + "gqmynt",
                                        ChatColor.YELLOW + "Online tid: " + ChatColor.WHITE + "tid"
                                ),
                                getHead(target.getUniqueId())

                    ));
                    p.openInventory(menu);
                    return;
                }
            }
        }


    }

    public static void displayStats(String parkour, Player p, int page){

        ArrayList<ParkourUser> times_top = RetreiveParkours.getRecordsTop(parkour, page);
        ArrayList<ParkourUser> plays_top = RetreiveParkours.getPlaysTop(parkour, page);

        Inventory menu = Bukkit.createInventory(p, 45, parkour + " topplista");
        ParkourUser currentFastestPlayer, currentMostPlayed;

        DateFormat open_format = new SimpleDateFormat("HH:mm:ss:SSS");
        open_format.setTimeZone(TimeZone.getTimeZone("GMT"));

        menu.setItem(0,
                makeItem(
                        ChatColor.YELLOW + parkour.substring(0, 1).toUpperCase() + parkour.substring(1),
                        List.of(
                                ChatColor.YELLOW + "Totalt klarad: "+ ChatColor.WHITE + RetreiveParkours.totalParkourCompletionsOrAvgTime(parkour, true),
                                ChatColor.YELLOW + "Medel tid: " + ChatColor.WHITE + open_format.format(RetreiveParkours.totalParkourCompletionsOrAvgTime(parkour, false))


                        ),
                        new ItemStack(Material.FILLED_MAP)
                ));

        for(int i = 0; i < 7; i++){




            if (i < times_top.size()){
                currentFastestPlayer = times_top.get(i);
                menu.setItem(
                        19 + (i % 7),
                        makeItem(
                                String.format(ChatColor.YELLOW + "%d. %s", i+1, Bukkit.getOfflinePlayer(UUID.fromString(currentFastestPlayer.user)).getName()),
                                List.of(
                                        ChatColor.YELLOW + "Tid: " + ChatColor.WHITE + open_format.format(currentFastestPlayer.time),
                                        ChatColor.YELLOW + "Antal klarade: " + ChatColor.WHITE + currentFastestPlayer.completions
                                ),
                                getHead(UUID.fromString(currentFastestPlayer.user))

                        )
                );
            }else{
                menu.setItem(
                        19 + (i % 7),
                        makeItem(
                                ChatColor.RED + "Ingen",
                                List.of(),
                                new ItemStack(Material.BARRIER)

                        )
                );
            }
            if(i < plays_top.size()){
                currentMostPlayed = plays_top.get(i);
                menu.setItem(
                        28 + (i % 7),
                        makeItem(
                                String.format(ChatColor.YELLOW + "%d. %s", i+1, Bukkit.getOfflinePlayer(UUID.fromString(currentMostPlayed.user)).getName()),
                                List.of(
                                        ChatColor.YELLOW + "Antal klarade: " + ChatColor.WHITE + currentMostPlayed.completions
                                ),
                                getHead(UUID.fromString(currentMostPlayed.user))

                        )
                );
            }else{
                menu.setItem(
                        28 + (i % 7),
                        makeItem(
                                ChatColor.RED + "Ingen",
                                List.of(),
                                new ItemStack(Material.BARRIER)

                        )
                );
            }

        }
        if(plays_top.size() >= 7 || times_top.size() >= 7){
            menu.setItem(
                    44,
                    makeItem(
                            ChatColor.YELLOW + "Nästa sida",
                            List.of(String.format(ChatColor.GRAY + "Sida (%d/%d)", page+1, RetreiveParkours.getPages(parkour))), // fix page number
                            new ItemStack(Material.ARROW)
                    )
            );
        }

        if(page > 0){
            menu.setItem(
                    36,
                    makeItem(
                            ChatColor.YELLOW + "Föregående sida",
                            List.of(String.format(ChatColor.GRAY + "Sida (%d/%d)", page+1, 20)), // fix page number
                            new ItemStack(Material.ARROW)
                    )
            );
        }




        p.openInventory(menu);
    }

}
