package top.hyreon.beyondPotions;

import org.bukkit.Material;
import org.bukkit.inventory.BrewerInventory;
import org.bukkit.inventory.ItemStack;

public class BrewingRecipe {

    private final ItemStack ingredient;

    private BrewAction action;
    private BrewClock clock;

    //fuel type is always the same
    //fuelSet is always the same
    //fuelCharge is always the same

    private boolean exactItem;

    public BrewingRecipe(ItemStack item, BrewAction action) {
        this.ingredient = item;
        this.action = action;
        this.exactItem = true;
    }

    public BrewingRecipe(Material ingredient, BrewAction action) {
        this.ingredient = new ItemStack(ingredient);
        this.action = action;
        this.exactItem = false;
    }

    public ItemStack getIngredient() {
        return ingredient;
    }

    public BrewAction getAction() {
        return action;
    }

    public void setAction(BrewAction action) {
        this.action = action;
    }

    public BrewClock getClock() {
        return clock;
    }

    public void setClock(BrewClock clock) {
        this.clock = clock;
    }

    public boolean isPerfect() {
        return exactItem;
    }

    public void setPerfect(boolean perfect) {
        this.exactItem = perfect;
    }

    public void startBrewing(BrewerInventory inventory) {
        clock = new BrewClock(this, inventory);
    }

    public void startBrewing(BrewerInventory inventory, int progress) {
        clock = new BrewClock(this, inventory, progress);
    }

    public static BrewingRecipe getRecipe(BrewerInventory inventory) {
        ItemStack ingredient = inventory.getIngredient();
        if (ingredient != null) {
            if (ingredient.getType() == Material.DRAGON_BREATH) {
                return new BrewingRecipe(ingredient, BrewAction.LINGER);
            } else if (ingredient.getType() == Material.GUNPOWDER) {
                return new BrewingRecipe(ingredient, BrewAction.SPLASH);
            } else if (ingredient.getType() == Material.REDSTONE) {
                return new BrewingRecipe(ingredient, BrewAction.EXTEND);
            } else if (ingredient.getType() == Material.GLOWSTONE_DUST) {
                return new BrewingRecipe(ingredient, BrewAction.CONCENTRATE);
            }
        }
        if (BeyondPotions.getInstance().doesRandomize()) {
            return new BrewingRecipe(ingredient, BrewAction.RANDOM);
        } else {
            return null; //oops, no recipe
        }
    }


}
