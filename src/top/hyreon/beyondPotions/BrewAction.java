package top.hyreon.beyondPotions;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.BrewerInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.util.*;
import java.util.stream.Collectors;

public abstract class BrewAction {

    private static final int MIN_EXTEND_DURATION = 900; //900 ticks, 45 seconds - vanilla regeneration effect
    private static final int MAX_PROCESSING_ATTEMPTS = 200; //should hover around the 4 mark, 200 is an extreme case.
    public static Random serverWideRNG = new Random(BeyondPotions.getSeed(null));

    public static List<Character> chipMap = generateChipMap();
    public static List<String> environmentMap = generateEnvironmentMap();

    public static char[][] environmentChipGrid = generateChipGrid();
    public static String[] basicEnvironments = generateBasicEnvironments();
    private static final Map<PotionEffectType, Integer> powerIndex = generatePowerIndex();

    private static Map<PotionEffectType, Integer> generatePowerIndex() {
        Map<PotionEffectType, Integer> potionEffects = new HashMap<>();
        potionEffects.put(PotionEffectType.INCREASE_DAMAGE, 100);
        potionEffects.put(PotionEffectType.DAMAGE_RESISTANCE, 100);
        potionEffects.put(PotionEffectType.HEALTH_BOOST, 75);
        potionEffects.put(PotionEffectType.SATURATION, 75);
        potionEffects.put(PotionEffectType.REGENERATION, 50);
        potionEffects.put(PotionEffectType.DOLPHINS_GRACE, 50);
        potionEffects.put(PotionEffectType.HEAL, 50);
        potionEffects.put(PotionEffectType.INVISIBILITY, 50);
        potionEffects.put(PotionEffectType.JUMP, 50);
        potionEffects.put(PotionEffectType.SLOW_FALLING, 50);
        potionEffects.put(PotionEffectType.SPEED, 50);
        potionEffects.put(PotionEffectType.ABSORPTION, 50);
        potionEffects.put(PotionEffectType.CONDUIT_POWER, 25);
        potionEffects.put(PotionEffectType.FAST_DIGGING, 25);
        potionEffects.put(PotionEffectType.FIRE_RESISTANCE, 25);
        potionEffects.put(PotionEffectType.WATER_BREATHING, 25);
        potionEffects.put(PotionEffectType.NIGHT_VISION, 10);
        potionEffects.put(PotionEffectType.HERO_OF_THE_VILLAGE, 5);
        potionEffects.put(PotionEffectType.LUCK, 1);
        potionEffects.put(PotionEffectType.UNLUCK, -1);
        potionEffects.put(PotionEffectType.GLOWING, -5);
        potionEffects.put(PotionEffectType.LEVITATION, -5);
        potionEffects.put(PotionEffectType.BAD_OMEN, -5);
        potionEffects.put(PotionEffectType.BLINDNESS, -10);
        potionEffects.put(PotionEffectType.DARKNESS, -10);
        potionEffects.put(PotionEffectType.HUNGER, -10);
        potionEffects.put(PotionEffectType.CONFUSION, -10);
        potionEffects.put(PotionEffectType.SLOW, -25);
        potionEffects.put(PotionEffectType.WEAKNESS, -25);
        potionEffects.put(PotionEffectType.HARM, -25);
        potionEffects.put(PotionEffectType.POISON, -50);
        potionEffects.put(PotionEffectType.WITHER, -50);
        potionEffects.put(PotionEffectType.SLOW_DIGGING, -50);

        return potionEffects;
    }

    PotionEffectType[] potionEffectTypes = generatePotionEffectTypeArray();

    private PotionEffectType[] generatePotionEffectTypeArray() {
        PotionEffectType[] array = new PotionEffectType[125];
        PotionEffectType[] baseArray = PotionEffectType.values();
        for (int i = 0; i < baseArray.length; i++) {
            array[i] = baseArray[i];
        }
        return array;
    }

    public static final String POTION_NAME = ChatColor.WHITE + "%s Potion of %s";
    public static final String SPLASH_POTION_NAME = ChatColor.WHITE + "%s Splash Potion of %s";
    public static final String LINGER_POTION_NAME = ChatColor.WHITE + "%s Lingering Potion of %s";
    public static final String ARROW_NAME = ChatColor.WHITE + "%s Arrow of %s";

    private static List<Character> generateChipMap() {
        List<Character> chipMap = new ArrayList<>(6);
        chipMap.add('Ω');
        chipMap.add('∵');
        chipMap.add('△');
        chipMap.add('◇');
        chipMap.add('▽');
        chipMap.add('∴');
        return chipMap;
    }

    private static List<String> generateEnvironmentMap() {
        List<String> environmentMap = new ArrayList<>(32);
        environmentMap.add("Thick Potion");
        environmentMap.add("Mundane Potion");
        environmentMap.add("Uninteresting Potion");
        environmentMap.add("Bland Potion");
        environmentMap.add("Clear Potion");
        environmentMap.add("Milky Potion");
        environmentMap.add("Diffuse Potion");
        environmentMap.add("Artless Potion");
        environmentMap.add("Thin Potion");
        environmentMap.add("Flat Potion");
        environmentMap.add("Bulky Potion");
        environmentMap.add("Bungling Potion");
        environmentMap.add("Buttered Potion");
        environmentMap.add("Smooth Potion");
        environmentMap.add("Suave Potion");
        environmentMap.add("Debonair Potion");
        environmentMap.add("Elegant Potion");
        environmentMap.add("Fancy Potion");
        environmentMap.add("Charming Potion");
        environmentMap.add("Dashing Potion");
        environmentMap.add("Refined Potion");
        environmentMap.add("Cordial Potion");
        environmentMap.add("Sparkling Potion");
        environmentMap.add("Potent Potion");
        environmentMap.add("Foul Potion");
        environmentMap.add("Odorless Potion");
        environmentMap.add("Rank Potion");
        environmentMap.add("Harsh Potion");
        environmentMap.add("Acrid Potion");
        environmentMap.add("Gross Potion");
        environmentMap.add("Stinky Potion");
        environmentMap.add("Burpy Potion");
        return environmentMap;
    }

    private static char[][] generateChipGrid() {
        char[][] chipGrid = new char[6][6];
        for (int j = 0; j < 6; j++) { //the added texture
            chipGrid[0][j] = chipMap.get(j);
            //Ω returns identity, guaranteeing at least 1 of each symbol
            //also simplifies behavior for untagged potions (they can just be 'ΩΩΩΩΩΩΩΩ')
        }
        for (int i = 1; i < 6; i++) { //the base texture
            for (int j = 0; j < 6; j++) { //the added texture
                chipGrid[i][j] = asEnvironmentSymbol(serverWideRNG.nextInt(16));
                if (!BeyondPotions.getInstance().isChaotic() && i == j) chipGrid[i][j] = chipMap.get(j); //steady IDs
            }
        }
        return chipGrid;
    }

    private static String[] generateBasicEnvironments() {
        String[] basicEnvironments = new String[32];
        for (int i = 0; i < basicEnvironments.length; i++) {
            basicEnvironments[i] = environmentFromInt(serverWideRNG.nextInt());
        }
        return basicEnvironments;
    }

    public static boolean isRandom(PotionMeta meta) {

        if (BeyondPotions.getInstance().usesPersistentData()) {
            byte value = meta.getPersistentDataContainer().getOrDefault(BeyondPotions.PersistentKey.IS_RANDOM, PersistentDataType.BYTE, (byte) 0);
            return value != 0;
        } else {
            return meta.getBasePotionData().getType() == PotionType.UNCRAFTABLE; //simply assume uncraftable potions are randomly generated.
        }

    }

    public static void setRandomName(PotionMeta meta, String potionFormat) {

        PotionEffect strongest = getStrongestPotionEffect(meta);
        List<PotionEffect> secondaryEffectList = meta.getCustomEffects().stream()
                .filter(e -> !e.equals(strongest))
                .collect(Collectors.toList());
        String secondaryAdjective = getEffectAdjective(secondaryEffectList);
        String potionName = Arrays.stream(strongest.getType().getName().split("_"))
                .map(s -> s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase())
                .collect(Collectors.joining(" "));


        meta.setDisplayName(String.format(potionFormat, secondaryAdjective, potionName));
        meta.setColor(strongest.getType().getColor());

    }

    private static String getEffectAdjective(List<PotionEffect> secondaryEffectList) {

        int effectAlignment = 0;
        for (PotionEffect effect : secondaryEffectList) {
            effectAlignment += weightedDuration(effect) * powerIndex.getOrDefault(effect.getType(), 0);
        }

        if (effectAlignment < -100 * 2 * 60 * 20) { //nasty effects for longer than 2 minutes
            return "Abysmal";
        } else if (effectAlignment < -50 * 30 * 20) { //bad effects for 30 seconds, or worse
            return "Cursed";
        } else if (effectAlignment < -25 * 10 * 20) { //10 second minor consequences, or worse
            return "Nasty";
        } else if (effectAlignment < -10 * 5 * 20) { //5 second inconvenience, or worse
            return "Ephemeral";
        } else if (effectAlignment < 10 * 5 * 20) {
            return "Ordinary";
        } else if (effectAlignment < 25 * 10 * 20) {
            return "Enhanced";
        } else if (effectAlignment < 50 * 30 * 20) {
            return "Sweet";
        } else if (effectAlignment < 100 * 2 * 60 * 20) {
            return "Blessed";
        } else {
            return "Apotheotic";
        }

    }

    private static PotionEffect getStrongestPotionEffect(PotionMeta meta) {

        PotionEffect strongest = null;

        for (PotionEffect effect : meta.getCustomEffects()) {
            if (strongest == null) {
                strongest = effect;
            } else strongest = updateStrongestPotionEffect(strongest, effect);
        }

        return strongest;

    }

    public abstract void brew(BrewerInventory inventory, ItemStack item, ItemStack ingredient);

    public static final BrewAction LINGER = new BrewAction() {

        @Override
        public void brew(BrewerInventory inventory, ItemStack item, ItemStack ingredient) {

            if (item.getType() != Material.SPLASH_POTION) return; //only affects potions and water bottles

            if (!(item.getItemMeta() instanceof PotionMeta)) return; //no change
            PotionMeta meta = (PotionMeta) item.getItemMeta();

            if (isRandom(meta)) {
                setRandomName(meta, LINGER_POTION_NAME);
            }

            item.setType(Material.LINGERING_POTION);
            item.setItemMeta(meta);

        }
    };

    public static final BrewAction SPLASH = new BrewAction() {

        @Override
        public void brew(BrewerInventory inventory, ItemStack item, ItemStack ingredient) {

            if (item.getType() != Material.POTION) return; //only affects potions and water bottles

            if (!(item.getItemMeta() instanceof PotionMeta)) return; //no change
            PotionMeta meta = (PotionMeta) item.getItemMeta();

            if (isRandom(meta)) {
                setRandomName(meta, SPLASH_POTION_NAME);
            }

            item.setType(Material.SPLASH_POTION);
            item.setItemMeta(meta);

        }
    };

    public static final BrewAction EXTEND = new BrewAction() {
        @Override
        public void brew(BrewerInventory inventory, ItemStack item, ItemStack ingredient) {

            if (!(item.getItemMeta() instanceof PotionMeta)) return; //no change
            PotionMeta meta = (PotionMeta) item.getItemMeta();

            if (meta.getBasePotionData().getType() != PotionType.UNCRAFTABLE) { //vanilla potions
                if (meta.getBasePotionData().getType().isExtendable()) {
                    meta.setBasePotionData(new PotionData(meta.getBasePotionData().getType(), true, false));
                    item.setItemMeta(meta);
                }
                return;
            }

            for (PotionEffect effect : meta.getCustomEffects()) {
                if (effect.getAmplifier() > 0 && !effect.getType().isInstant()) {
                    PotionEffect effectClone = new PotionEffect(effect.getType(), effect.getDuration() * 2, effect.getAmplifier() - 1);
                    meta.addCustomEffect(effectClone, true);
                }
            }

            item.setItemMeta(meta);

        }
    };

    public static final BrewAction CONCENTRATE = new BrewAction() {
        @Override
        public void brew(BrewerInventory inventory, ItemStack item, ItemStack ingredient) {

            if (!(item.getItemMeta() instanceof PotionMeta)) return; //no change
            PotionMeta meta = (PotionMeta) item.getItemMeta();

            if (meta.getBasePotionData().getType() != PotionType.UNCRAFTABLE) { //vanilla potions
                if (meta.getBasePotionData().getType().isUpgradeable()) {
                    meta.setBasePotionData(new PotionData(meta.getBasePotionData().getType(), false, true));
                    item.setItemMeta(meta);
                }
                return;
            }

            for (PotionEffect effect : meta.getCustomEffects()) {
                if (effect.getDuration() > MIN_EXTEND_DURATION &&
                        !effect.getType().isInstant() &&
                        effect.getAmplifier() + 1 < BeyondPotions.getInstance().maxLevelOfEffect(effect.getType())
                ) {
                    PotionEffect effectClone = new PotionEffect(effect.getType(), effect.getDuration() / 2, effect.getAmplifier() + 1);
                    meta.addCustomEffect(effectClone, true);
                }
            }

            item.setItemMeta(meta);

        }
    };

    public static final BrewAction RANDOM = new BrewAction() {

        @Override
        public void brew(BrewerInventory inventory, ItemStack item, ItemStack ingredient) {

            if (!(item.getItemMeta() instanceof PotionMeta)) return; //no change
            PotionMeta meta = (PotionMeta) item.getItemMeta();

            boolean existingEffects = item.getItemMeta() != null && !((PotionMeta) item.getItemMeta()).getCustomEffects().isEmpty();

            long serverSeed = BeyondPotions.getSeed(inventory.getLocation().getWorld());

            int ingredientSeed = ingredient.getType().ordinal();

            String itemEnvironment = getEnvironment(item);

            double intensity = 1.00;
            long enchantSeed = 0;
            if (ingredient.getItemMeta() != null) {

                double max = ingredient.getType().getMaxDurability();
                if (max > 0) {
                    double damage = ((Damageable) ingredient.getItemMeta()).getDamage();
                    intensity = (max - damage) / max;
                }

                for (Map.Entry<Enchantment, Integer> enchantment : ingredient.getItemMeta().getEnchants().entrySet()) {
                    enchantSeed = enhanceSeed(enchantSeed, enchantment.getKey().hashCode() * enchantment.getValue());
                }

            }

            if (existingEffects) intensity /= 2.00;

            final long brewingSeed = enhanceSeed(serverSeed, ingredientSeed, enchantSeed);
            Random random = new Random(brewingSeed);

            int refreshes = 0;
            //check if the last nybble matches random.nextInt
            while (asEnvironmentSymbol(random.nextInt(16)) == itemEnvironment.charAt(itemEnvironment.length()-1-refreshes)) {
                refreshes++;
                //if itemEnvironment is exhausted, it needs to stop.
                if (refreshes == 8) break;
            }

            PotionType baseType = meta.getBasePotionData().getType();
            if  (BeyondPotions.getInstance().recipeCanFail() && (
                    (baseType == PotionType.AWKWARD && refreshes >= 2) ||
                    (baseType == PotionType.WATER || baseType == PotionType.MUNDANE || baseType == PotionType.THICK && refreshes < 2))) {
                fail(item, random.nextInt());
                return;
            }

            int addedEffects = rareLevelup(random.nextDouble()) + 1;

            intensity = (intensity * (1 + 0.5 * refreshes));
            List<PotionEffect> potionEffects = new ArrayList<>();
            for (int i = 0; i < addedEffects; i++) {
                potionEffects.add(generatePotionEffect(random, intensity));
            }

            //always last step: create the environment for next time
            int environment = random.nextInt();

            //i lied, there is a laster step: post-processing
            int postProcessingAttempts = 0;
            for (int i = 0; i < potionEffects.size(); i++) {
                PotionEffect potionEffect = potionEffects.get(i);
                if (potionEffect == null ||
                        !BeyondPotions.getInstance().allowsEffect(potionEffect.getType())) {
                    potionEffect = generatePotionEffect(random, (intensity));
                    potionEffects.set(i, potionEffect);
                    i--;
                    postProcessingAttempts++;
                } else if (potionEffect.getAmplifier() >= BeyondPotions.getInstance().maxLevelOfEffect(potionEffect.getType())) {
                    potionEffect = new PotionEffect(
                            potionEffect.getType(),
                            potionEffect.getDuration(),
                            BeyondPotions.getInstance().maxLevelOfEffect(potionEffect.getType()) - 1);
                    potionEffects.set(i, potionEffect);
                    //completely safe operation right now, but if post-processing gets more complex it may not be
                    //i--;
                    //postProcessingAttempts++;
                }
            }

            meta.setBasePotionData(new PotionData(PotionType.UNCRAFTABLE));

            Map<PotionEffectType, PotionEffect> effectIndex = new HashMap<>();
            for (PotionEffect existingEffect : meta.getCustomEffects()) {
                PotionEffect effect = new PotionEffect(
                        existingEffect.getType(),
                        existingEffect.getDuration() / 2,
                        existingEffect.getAmplifier()
                );
                effectIndex.put(effect.getType(), effect);
                meta.addCustomEffect(
                        effect, true);

            }

            //unused 'strongest effect', used to determine what the potion should be called
            PotionEffect strongestPotionEffect = new PotionEffect(PotionEffectType.CONFUSION, 0, 0);

            for (PotionEffect potionEffect : potionEffects) {
                if (!effectIndex.containsKey(potionEffect.getType())) {
                    meta.addCustomEffect(potionEffect, true);
                    strongestPotionEffect = updateStrongestPotionEffect(strongestPotionEffect, potionEffect);
                } else {
                    PotionEffect previousEffect = effectIndex.get(potionEffect.getType());
                    if (previousEffect.getAmplifier() < potionEffect.getAmplifier()) {
                        meta.addCustomEffect(potionEffect, true);
                        strongestPotionEffect = updateStrongestPotionEffect(strongestPotionEffect, potionEffect);
                    } else if (previousEffect.getAmplifier() == potionEffect.getAmplifier()) {
                        PotionEffect effectUnion = new PotionEffect(
                                potionEffect.getType(),
                                potionEffect.getDuration() + previousEffect.getDuration(),
                                potionEffect.getAmplifier()
                        );
                        meta.addCustomEffect(effectUnion, true);
                        strongestPotionEffect = updateStrongestPotionEffect(strongestPotionEffect, effectUnion);
                    }
                }
            }

            //itemEnvironment is the EXISTING, BASE environment (i)
            //newEnvironment is  the NEW, EXPECTED environment (j)
            String newEnvironment = environmentFromInt(environment);
            StringBuilder combinedEnvironment = new StringBuilder();
            for (int i = 0; i < newEnvironment.length(); i++) {
                int base = chipMap.indexOf(itemEnvironment.charAt(i));
                int added = chipMap.indexOf(newEnvironment.charAt(i));
                combinedEnvironment.append(environmentChipGrid[base][added]);
            }

            List<String> lore = new ArrayList<>();
            lore.add(combinedEnvironment.toString());
            meta.setLore(lore);

            //name potion after the most powerful effect
            //adjective to indicate if the secondary effects are generally good, and how much they matter
            String potionNoun = strongestPotionEffect.getType().getName();
            String potionAdjective = "actualized"; //placeholder
            //meta.setLocalizedName(String.format("item.potion.%s.%s", potionAdjective, potionNoun));
            //can't use this, splash, lingering and arrows depend on names being simple

            if (item.getType() == Material.SPLASH_POTION) {
                setRandomName(meta, SPLASH_POTION_NAME);
            } else if (item.getType() == Material.LINGERING_POTION) {
                setRandomName(meta, LINGER_POTION_NAME);
            } else {
                setRandomName(meta, POTION_NAME);
            }

            BeyondPotions.getInstance().tagPotion(meta, true, combinedEnvironment.toString());

            item.setItemMeta(meta);

        }

    };

    public PotionEffect generatePotionEffect(Random random, double intensity) {
        PotionEffectType potionEffectSelection = potionEffectTypes[random.nextInt(potionEffectTypes.length)];
        int potionEffectIntensity = rareLevelup(random.nextDouble());

        int potionEffectDuration;
        int randomValue = random.nextInt(16383) + 1; //48 hours / some value from 1 to 16383

        if (potionEffectSelection == null) return null;

        //a random value is consumed, even if unused by the particular potion effect
        if (potionEffectSelection.isInstant()) {
            potionEffectDuration = 1;
        } else {
            int totalTime = 48*60*60 / randomValue; //as long as 48 hours, but usually far, far less
            totalTime = (int) Math.ceil(((double) (totalTime)) / 5.0) * 5;
            potionEffectDuration = (int) (totalTime * 20 * intensity); //random number of seconds
            if (potionEffectDuration <= 0) {
                potionEffectDuration = 1;
            }
        }


        return new PotionEffect(potionEffectSelection, potionEffectDuration, potionEffectIntensity);
    }

    private static void fail(ItemStack item, int randomNumber) {
        PotionMeta potion = (PotionMeta) item.getItemMeta();
        if (Integer.remainderUnsigned(randomNumber, 2) == 0) {
            potion.setBasePotionData(new PotionData(PotionType.MUNDANE));
        } else {
            potion.setBasePotionData(new PotionData(PotionType.THICK));
        }
        int displayIndex = Integer.remainderUnsigned(randomNumber, 32);
        potion.setDisplayName(ChatColor.WHITE + environmentMap.get(displayIndex));
        item.setItemMeta(potion);
    }

    private static PotionEffect updateStrongestPotionEffect(PotionEffect initialPE, PotionEffect newPE) {
        if (weightedDuration(newPE) > weightedDuration(initialPE)) return newPE;
        else return initialPE;
    }

    private static int weightedDuration(PotionEffect initialPE) {
        return (initialPE.getType().isInstant() ? 20 * 30 :
                initialPE.getDuration()) *
                (initialPE.getAmplifier()+1);
    }


    private static char asEnvironmentSymbol(int digit) {
        switch (digit) {
            case 1:
            case 2:
            case 3:
                return '∵';
            case 4:
            case 5:
            case 6:
                return '△';
            case 7:
            case 8:
            case 9:
                return '◇';
            case 10:
            case 11:
            case 12:
                return '▽';
            case 13:
            case 14:
            case 15:
                return '∴';
            default: //includes 0 and illegal values only
                return 'Ω';
        }
    }

    private static String environmentFromInt(int environment) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            builder.insert(0, asEnvironmentSymbol(Integer.remainderUnsigned(environment, 16)));
            environment = Integer.divideUnsigned(environment, 16);
        }
        return builder.toString();
    }

    public long enhanceSeed(long... seeds) {
        long finalSeed = 0;
        for (long seed : seeds) {
            finalSeed *= 8191;
            finalSeed += seed;
        }
        return finalSeed;
    }

    /**
     * Distributes a double from 0 to 1 to an integer value from 0 to infinity,
     * where each successive value takes a larger fraction of what remains.
     * 1/2 is 0, 2/3 of that is 1, 3/4 of that is 2, etc.
     * The results resemble an inverse factorial.
     * This leaves small values as by far the most common while not limiting the range of possible values.
     * @param input
     * @return
     */
    public int rareLevelup(double input) {
        int output = 0;
        double cutoff = 0.5;
        while (input > cutoff) {
            output++;
            cutoff += (((output + 1.0) / (output + 2.0)) * (1.0 - cutoff));
        }
        return output;
    }

    public String getEnvironment(ItemStack potion) {
        ItemMeta meta = potion.getItemMeta();
        if (meta != null) {
            List<String> lore = meta.getLore();
            if (lore != null && lore.size() > 0) {
                return lore.get(0);
            }
            String name = meta.getDisplayName();
            if (name.contains(ChatColor.WHITE.toString())) { //is a custom brewing recipe name
                for (int i = 0; i < environmentMap.size(); i++) {
                    if (name.contains(environmentMap.get(i))) {
                        return basicEnvironments[i];
                    }
                }
            } else { //probably changed at an anvil
                PotionMeta potionMeta = (PotionMeta) meta;
                if (potionMeta.getBasePotionData().getType() == PotionType.THICK) {
                    return basicEnvironments[0];
                } else if (potionMeta.getBasePotionData().getType() == PotionType.MUNDANE) {
                    return basicEnvironments[1];
                }
            }
        }
        return "ΩΩΩΩΩΩΩΩ"; //hey look, nothing.
    }

}
