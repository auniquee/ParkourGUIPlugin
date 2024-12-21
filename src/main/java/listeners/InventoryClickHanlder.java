package listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import utils.InventoryFunctions;

public class InventoryClickHanlder implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e){
        if( e.getView().getTitle().contains("topplista") && e.getCurrentItem() != null) {
            e.setCancelled(true);
            Player p = (Player) e.getWhoClicked();

            if(e.getCurrentItem().getType() == Material.ARROW) {
                int page;
                String parkour;


                page = Integer.parseInt(e
                        .getCurrentItem()
                        .getItemMeta()
                        .getLore()
                        .get(0)
                        .replaceAll(".*[(]", "")
                        .replaceAll("[)].*", "")
                        .split("/")[0]
                ) - 1; // -1 to make it 0 indexed

                parkour = e.getView().getTitle().split(" ")[0];

                if (e.getCurrentItem().getItemMeta().getDisplayName().contains("Nästa")) {
                    InventoryFunctions.displayStats(parkour, p, page + 1);
                    // -1 one before so we can add one here and remove one below
                } else {
                    InventoryFunctions.displayStats(parkour, (Player) e.getWhoClicked(), page - 1);
                }
        }else if(e.getCurrentItem().getType() == Material.PLAYER_HEAD){
                InventoryFunctions.DisplayPlayerStats(Bukkit.getOfflinePlayer(e
                                .getCurrentItem()
                                .getItemMeta()
                                .getDisplayName()
                                .split(" ")[1]), p);
                // getOfflinePlayer depricated because names are unreliable but works really well in my case

            }

        }
        if( e.getView().getTitle().contains("statistik") && e.getCurrentItem() != null) {
            e.setCancelled(true);
            InventoryFunctions.displayStats(ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName()), (Player) e.getWhoClicked(), 0);
        }
    }
}
