package top.hyreon.beyondPotions;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.block.BrewingStand;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffectType;
import top.hyreon.beyondPotions.event.BrewingStandListener;
import top.hyreon.beyondPotions.event.CraftListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;

public class BeyondPotions extends JavaPlugin {

    private static final long AUTO_UPDATE_FREQUENCY = 200L;
    private static BeyondPotions instance;

    public static BeyondPotions getInstance() {
        return instance;
    }

    private static boolean compatibilityTags = true;

    private static boolean allowStale = false;
    private static boolean isChaotic = true;
    private static boolean infiniteFuel = false;
    private static boolean fastBrew = false;

    private static boolean forceDefaults = true;
    private static boolean useAnything = true;

    private static boolean useWorldSeed = true;
    private static long seed = 0;

    private static Map<PotionEffectType, Integer> maxLevels = new HashMap<>();
    private static int universalMaxLevel = 6;

    public static long getSeed(World world) {
        if (useWorldSeed) {
            if (world == null) return seed;
            return world.getSeed();
        } else {
            return seed;
        }
    }

    public boolean isChaotic() {
        return isChaotic;
    }

    public boolean usesPersistentData() { return compatibilityTags; }

    public boolean overrideVanilla() {
        return !forceDefaults;
    }

    public boolean doesRandomize() {
        return useAnything;
    }

    public boolean recipeCanFail() {
        return !allowStale;
    }

    public boolean freeFuel() {
        return infiniteFuel;
    }

    public boolean doFastBrew() {
        return fastBrew;
    }

    //maximum displayed level.
    public int maxLevelOfEffect(PotionEffectType type) {
        int maxLevel = maxLevels.get(type);
        if (maxLevel < 0) maxLevel = 127; //negative values are treated as no max level
        if (maxLevel > 255) maxLevel = 255; //even if using technical values, it should never exceed 255
        return Math.min(maxLevel, universalMaxLevel); //whichever is lower - universal max, or personal max
    }

    public boolean allowsEffect(PotionEffectType type) {
        return maxLevelOfEffect(type) != 0;
    }

    @Override
    public void onEnable() {
        instance = this;
        
        addListener();

        //double check every brewing stand for valid recipes, and start them up if possible.
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
            Bukkit.getWorlds().forEach(world -> {
                    for (Chunk chunk : world.getLoadedChunks()) {
                        for (BlockState tileEntity : chunk.getTileEntities()) {
                            if (tileEntity instanceof BrewingStand) {
                                BrewingStandListener.updateInATick(((BrewingStand) tileEntity).getInventory(), true);
                            }
                        }
                    }
                }
            );
        }, AUTO_UPDATE_FREQUENCY, AUTO_UPDATE_FREQUENCY);

        //this.getCommand("stench").setExecutor(new StenchCommand());
        
        reload();
    }

    private void addListener() {
    	getServer().getPluginManager().registerEvents(new BrewingStandListener(), this);
    	getServer().getPluginManager().registerEvents(new CraftListener(), this);
	}

	public void reload() {
        saveDefaultConfig();
        reloadConfig();
        cacheConfig();
    }

    /**
     * Loads the config values into plugin values for faster access and,
     * in some cases, better formatting.
     */
    private void cacheConfig() {
        //seed settings
        loadSeed();
        useWorldSeed = getConfig().getBoolean("use-world-seed");

        compatibilityTags = getConfig().getBoolean("use-namespaces");

        //content settings
        useAnything = getConfig().getBoolean("use-anything");
        forceDefaults = getConfig().getBoolean("force-defaults");
        isChaotic = getConfig().getBoolean("unstable-ids");

        //cheat settings
        allowStale = getConfig().getBoolean("allow-stale");
        infiniteFuel = getConfig().getBoolean("infinite-fuel");
        fastBrew = getConfig().getBoolean("fast-brew");

        //cap settings
        universalMaxLevel = getConfig().getInt("level-caps.base-max-level");
        if (universalMaxLevel <= 0) { //only allow values that work as intended
            universalMaxLevel = 127;
        } else if (universalMaxLevel > 255) { //they're allowing negative values, whoops
            universalMaxLevel = 255;
        }
        for (PotionEffectType type : PotionEffectType.values()) {
            maxLevels.put(type, getConfig().getInt("level-caps.max-level." + type.getName(), 255));
        }
    }

    private void loadSeed() {
        if (!getConfig().contains("seed")) {
            getLogger().log(Level.INFO, "Seed was not set. Creating new recipe seed based on current time.");
            seed = new Random().nextLong();
            getConfig().set("seed", seed);
            saveConfig();
        } else if (getConfig().isLong("seed") || getConfig().isInt("seed")) {
            seed = getConfig().getLong("seed");
            //override seed if set to 0
            if (seed == 0) {
                getLogger().log(Level.INFO, "Seed was 0. Creating new recipe seed based on current time.");
                seed = new Random().nextLong();
                getConfig().set("seed", seed);
                saveConfig();
            } else {
                getLogger().log(Level.INFO, "Using recipe seed as number.");
            }
        } else {
            //not null, bad options have been eliminated
            getLogger().log(Level.INFO, "Using recipe seed from string.");
            seed = getConfig().getString("seed").hashCode();
        }
    }

    @Override
    public void onDisable() {
        instance = null;
    }

    public void tagPotion(PotionMeta meta, boolean random, String lore) {
        if (usesPersistentData()) {
            meta.getPersistentDataContainer().set(PersistentKey.IS_RANDOM, PersistentDataType.BYTE, (byte) (random ? 1 : 0));
            meta.getPersistentDataContainer().set(PersistentKey.CRYPTOGRAM, PersistentDataType.STRING, lore);
        }
    }

    public static class PersistentKey {

        public static final NamespacedKey IS_RANDOM = new NamespacedKey(BeyondPotions.getInstance(), "random");
        public static final NamespacedKey CRYPTOGRAM = new NamespacedKey(BeyondPotions.getInstance(), "cryptogram");

    }
}
