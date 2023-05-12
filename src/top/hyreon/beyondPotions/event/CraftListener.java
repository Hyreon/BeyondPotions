package top.hyreon.beyondPotions.event;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import top.hyreon.beyondPotions.BrewAction;

public class CraftListener implements Listener {

    @EventHandler
    public static void craftArrowEvent(PrepareItemCraftEvent e) {
        ItemStack result = e.getInventory().getResult();
        if (result == null) return;
        if (result.getType() != Material.TIPPED_ARROW) return;
        ItemStack ingredient = e.getInventory().getMatrix()[4];
        if (ingredient != null && ingredient.hasItemMeta() && ingredient.getItemMeta() instanceof PotionMeta &&
                BrewAction.isRandom((PotionMeta) ingredient.getItemMeta())) {
            PotionMeta meta = (PotionMeta) result.getItemMeta();
            BrewAction.setRandomName(meta, BrewAction.ARROW_NAME);
            result.setItemMeta(meta);
            e.getInventory().setResult(result);
        }
    }

}
