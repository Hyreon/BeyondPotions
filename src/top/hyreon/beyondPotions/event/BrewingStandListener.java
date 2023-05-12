package top.hyreon.beyondPotions.event;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.BrewingStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.BrewerInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import top.hyreon.beyondPotions.BrewAction;
import top.hyreon.beyondPotions.BrewClock;
import top.hyreon.beyondPotions.BrewingRecipe;
import top.hyreon.beyondPotions.BeyondPotions;

import java.util.Optional;

public class BrewingStandListener implements Listener {

    @EventHandler
    public void customPotionItemStackClick(InventoryClickEvent event) {

        Inventory inv = event.getClickedInventory();

        if (inv == null || inv.getType() != InventoryType.BREWING) {
            return;
        }

        if (!(event.getClick() == ClickType.LEFT || event.getClick() == ClickType.RIGHT)) {
            updateInATick(inv, false);
            return;
        }

        ItemStack is = event.getCurrentItem(); // GETS ITEMSTACK THAT IS BEING CLICKED
        ItemStack is2 = event.getCursor(); // GETS CURRENT ITEMSTACK HELD ON MOUSE

        if (event.getSlot() != 3) { //only change behavior for the ingredient slot.
            updateInATick(inv, true);
            return;
        }

        event.setCancelled(true);

        Player p = (Player)(event.getView().getPlayer());

        boolean compare = is.isSimilar(is2);
        ClickType type = event.getClick();

        int firstAmount = is.getAmount();
        int secondAmount = is2.getAmount();

        int stack = is.getMaxStackSize();
        int half = firstAmount / 2;

        int clickedSlot = event.getSlot();

        if (type == ClickType.LEFT) {

            if (is.getType() == Material.AIR) {

                p.setItemOnCursor(is);
                inv.setItem(clickedSlot, is2);

            } else if (compare) {

                int used = stack - firstAmount;
                if (secondAmount <= used) {

                    is.setAmount(firstAmount + secondAmount);
                    p.setItemOnCursor(null);

                } else {

                    is2.setAmount(secondAmount - used);
                    is.setAmount(firstAmount + used);
                    p.setItemOnCursor(is2);

                }

            } else {

                inv.setItem(clickedSlot, is2);
                p.setItemOnCursor(is);

            }

        } else if (type == ClickType.RIGHT) {

            if (is.getType() == Material.AIR) {

                ItemStack is2Clone = is2.clone();
                is2Clone.setAmount(1);
                inv.setItem(clickedSlot, is2Clone);
                is2.setAmount(secondAmount - 1);

            } else if (is.getType() != Material.AIR && is2.getType() == Material.AIR) {

                ItemStack isClone = is.clone();
                isClone.setAmount(firstAmount - half);
                p.setItemOnCursor(isClone);

                is.setAmount(firstAmount % 2 == 0 ? firstAmount - half : firstAmount - half - 1);

            } else if (compare) {

                if ((firstAmount + 1) <= stack) {

                    is2.setAmount(secondAmount - 1);
                    is.setAmount(firstAmount + 1);

                }

            } else {

                inv.setItem(clickedSlot, is2);
                p.setItemOnCursor(is);
            }

        }

        if (((BrewerInventory) inv).getIngredient() == null) {
            updateInATick(inv, false);
            return;
        }

        BrewingRecipe recipe = BrewingRecipe.getRecipe((BrewerInventory) inv);

        if (recipe == null) {
            updateInATick(inv, false);
        } else {
            if (!compare) { //items are dissimilar
                updateInATick(inv, true, true);
            } else {
                updateInATick(inv, true);
            }
        }

    }

    public static void updateInATick(Inventory inventory, boolean force) {
        updateInATick(inventory, force, false);
    }

    static void updateInATick(Inventory inventory, boolean force, boolean reset) {

        BrewingStand brewingStand = ((BrewerInventory) inventory).getHolder();
        if (brewingStand != null) {
            //assured an updated brewing stand
            applyCheats((BrewingStand) brewingStand.getBlock().getState());
        }

        Optional<BrewerInventory> stand = BrewClock.brewingStandsInUse.keySet().stream()
                .filter(st -> st.getLocation().equals(inventory.getLocation()))
                .findFirst();

        final int time;
        if (stand.isPresent()) {
            BrewClock clock = BrewClock.brewingStandsInUse.get(stand.get());
            if (reset) time = BrewClock.DEFAULT_TIME;
            else time = clock.getCurrentTime();

            //stop any further forced item updates here
            BrewClock.brewingStandsInUse.get((BrewerInventory) inventory).cancel();
        } else if (!force) {
            return; //won't start a new recipe unless forced to.
        } else {
            time = BrewClock.DEFAULT_TIME;
        }

        //and recreate the brewing stand in its last state.
        Bukkit.getScheduler().scheduleSyncDelayedTask(BeyondPotions.getInstance(), () -> {

            BrewingRecipe recipe = BrewingRecipe.getRecipe((BrewerInventory) inventory);
            if (recipe != null) { //if there's no recipe, none of this matters
                if (((BrewingStand)inventory.getLocation().getBlock().getState()).getBrewingTime() == 0) {
                    recipe.startBrewing((BrewerInventory) inventory, time);
                } else if (recipe.getAction() != BrewAction.RANDOM) { //hard-coded recipes take precedent
                    recipe.startBrewing((BrewerInventory) inventory, time - 1); // skips the fuel usage tick
                } else if (BeyondPotions.getInstance().overrideVanilla() &&
                        ((BrewerInventory) inventory).getIngredient() != null &&
                        ((BrewerInventory) inventory).getIngredient().getType() != Material.NETHER_WART) {
                    recipe.startBrewing((BrewerInventory) inventory, time - 1); // skips the fuel usage tick
                }
            }
        }, 2L); //psych! was actually 2 ticks

    }

    private static void applyCheats(BrewingStand stand) {
        boolean cheatsApplied = false;
        if (BeyondPotions.getInstance().freeFuel()) {
            stand.setFuelLevel(20);
            cheatsApplied = true;
        }
        if (BeyondPotions.getInstance().doFastBrew() && stand.getBrewingTime() < 400 && stand.getBrewingTime() > 0) {
            stand.setBrewingTime(2);
            cheatsApplied = true;
        }
        if (cheatsApplied) {
            stand.update(true);
        }
    }

}
