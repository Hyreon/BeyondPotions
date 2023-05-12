package top.hyreon.beyondPotions.event;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;

import java.util.List;

public class DrinkPotionListener implements Listener {


    @EventHandler
    public static void drinkPotionEvent(PlayerItemConsumeEvent e) {

        if (e.getItem().getType() == Material.POTION) {
            //TODO apply secondary effects based on this potion's secrets
            List<String> lore = e.getItem().getItemMeta().getLore();
        }

    }

}
