package top.hyreon.beyondPotions;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.BrewingStand;
import org.bukkit.inventory.BrewerInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

public class BrewClock extends BukkitRunnable
{

    public static Map<BrewerInventory, BrewClock> brewingStandsInUse = new HashMap<>();

    private BrewerInventory inventory;
    private BrewingRecipe recipe;
    private ItemStack[] before;
    private BrewingStand stand;
    private int currentTime = DEFAULT_TIME;
    public static int DEFAULT_TIME = BeyondPotions.getInstance().doFastBrew() ? 5 : 392;

    public BrewClock(BrewingRecipe recipe, BrewerInventory inventory) {
        this.recipe = recipe;
        this.inventory = inventory;
        this.stand = inventory.getHolder();
        this.before = inventory.getContents();
        brewingStandsInUse.put(inventory, this);
        runTaskTimer(BeyondPotions.getInstance(), 0L, 1L);
    }

    public BrewClock(BrewingRecipe recipe, BrewerInventory inventory, int progress) {
        this(recipe, inventory);
        this.currentTime = progress;
    }

    @Override
    public void cancel() {
        brewingStandsInUse.remove(inventory);
        super.cancel();
    }

    @Override
    public void run() {
        ItemStack ingredient = inventory.getIngredient();

        //make sure the recipe is still (or was ever) valid
        if (searchChanged(before, inventory.getContents(), recipe.isPerfect())) {
            cancel();
            return;
        }

        if (currentTime == DEFAULT_TIME) { //starting a new recipe
            if (stand.getFuelLevel() == 0) { //no fuel left
                cancel(); //can't brew
                return;
            } else if (isDeadRecipe()) {
                cancel(); //no change, ignore this recipe
                return;
            } else {
                // Set the fuel level
                stand = ((BrewingStand)stand.getBlock().getState()); //update the state based on current data
                stand.setFuelLevel(stand.getFuelLevel() - 1);
                stand.update(true);
            }
        } else if (currentTime == 2) //recipe is 'complete' (1 tick faster than vanilla brewing)
        {
            if (inventory.getIngredient() == null) {
                cancel(); //frame perfect removal - impressive, but unrewarded.
                //also seems to include some other edge cases, so remove this, as cool as null potions would be
                return;
            }

            // Set ingredient to 1 less than the current. Otherwise set to air
            if (inventory.getIngredient().getAmount() > 1) {
                ItemStack is = inventory.getIngredient();
                is.setAmount(inventory.getIngredient().getAmount() - 1);
                inventory.setIngredient(is);
            } else {
                inventory.setIngredient(new ItemStack(Material.AIR));
            }

            for (int i = 0; i < 3; i++) {
                if (inventory.getItem(i) == null || inventory.getItem(i).getType() == Material.AIR) {
                    continue;
                }
                recipe.getAction().brew(inventory, inventory.getItem(i), ingredient);
            }
            stand.getWorld().playSound(stand.getLocation(), Sound.BLOCK_BREWING_STAND_BREW, 1.0f, 1.0f);
            cancel();
            return;
        }

        currentTime--;

        stand = ((BrewingStand)stand.getBlock().getState()); //update the state based on current data
        stand.setBrewingTime(currentTime);
        stand.update(true);

    }

    public boolean isDeadRecipe() {
        boolean noChange = true;
        ItemStack[] contents = inventory.getContents();
        for (int i = 0; i < 3; i++) { //all ingredient slots
            if (contents[i] == null) continue;
            ItemStack item = contents[i].clone();
            recipe.getAction().brew(inventory, item, inventory.getIngredient());
            if (!item.equals(contents[i])) {
                noChange = false;
                break;
            }
        }
        return noChange;
    }

    public boolean searchChanged(ItemStack[] before, ItemStack[] after, boolean perfect) {

        boolean allEmpty = true;
        boolean changes = false;
        for (int i = 0; i < 3; i++) {
            if (after[i] != null) {
                allEmpty = false; //found something, they can't all be empty
                if (after[i] != before[i]) { //potion has been swapped
                    changes = true;
                    break; //there have been changes and not all is empty; not much else to search for
                }
            }
        }
        if (allEmpty) return true;  //search has changed, now there's nothing. the brewing needs to stop

        if (before[3] == null || after[3] == null) { //always give up if a null ingredient is involved.
            return true;
        }

        if (changes && isDeadRecipe()) { //checked AFTER everything is confirmed as NULL.
            return true; //recipe has been killed, the brewing needs to stop
        }

        if (!perfect && before[3].isSimilar(after[3])) {
            return false;   //the items are similar, keep going
        } else if (perfect && (before[3].getType() == after[3].getType())) {
            return false;   //the items are identical, keep going
        }

        return true;    //none of the simple changes were made, so this must mean reset time
    }

    public int getCurrentTime() {
        return currentTime;
    }
}
