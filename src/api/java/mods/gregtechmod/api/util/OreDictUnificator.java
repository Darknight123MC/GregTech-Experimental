package mods.gregtechmod.api.util;

import mods.gregtechmod.api.GregTechAPI;
import mods.gregtechmod.api.GregTechConfig;
import net.minecraft.block.Block;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class OreDictUnificator {

    private static final HashMap<String, ItemStack> name2OreMap = new HashMap<>();

    private static final HashMap<ItemStack, String> item2OreMap = new HashMap<>();

    private static final ArrayList<ItemStack> sBlackList = new ArrayList<>();

    public static void addToBlacklist(ItemStack stack) {
        sBlackList.add(stack);
    }

    public static void add(String name, Block block) {
        add(name, new ItemStack(block));
    }

    public static void add(String name, Item item) {
        add(name, new ItemStack(item));
    }

    public static void add(String name, ItemStack stack) {
        set(name, stack, false);
    }

    public static void set(String name, ItemStack stack) {
        set(name, stack, true);
    }

    public static void set(String name, ItemStack stack, boolean overwrite) {
        if (name == null || name.isEmpty() || name.startsWith("itemDust") || stack.isEmpty() || stack.getItemDamage() < 0) return;
        stack = stack.copy().splitStack(1);
        addAssociation(name, stack);
        if (!name2OreMap.containsKey(name)) {
            name2OreMap.put(name, stack);
        } else {
            if (overwrite && Arrays.asList(GregTechConfig.GENERAL.specialUnificationTargets).contains(GtUtil.getStackConfigName(stack))) {
                name2OreMap.remove(name);
                name2OreMap.put(name, stack);
            }
        }
        registerOre(name, stack);
    }

    public static void override(String name, ItemStack stack) {
        if (name == null || name.isEmpty() || name.startsWith("itemDust") || stack.isEmpty() || stack.getItemDamage() < 0) return;

        if (stack.getItem().getRegistryName() == null || stack.getDisplayName().isEmpty() || Arrays.asList(GregTechConfig.GENERAL.specialUnificationTargets).contains(GtUtil.getStackConfigName(stack))) set(name, stack);
    }

    public static ItemStack getFirstOre(String name, int amount) {
        if (name == null || name.isEmpty()) return null;
        if (name2OreMap.containsKey(name)) return get(name, null, amount);

        ItemStack stack = null;
        List<ItemStack> ores = getOres(name);
        if (ores.size() > 0) stack = ores.get(0).copy();
        if (stack != null) stack.setCount(amount);

        return stack;
    }

    public static ItemStack getFirstCapsulatedOre(String name, int amount) {
        if (name2OreMap.containsKey(name)) return get(name, null, amount);
        ItemStack stack = null;
        List<ItemStack> ores = getOres(name);
        for (ItemStack ore : ores) {
            if (ore != null && GtUtil.getCapsuleCellContainerCount(ore) == 1) {
                stack = ore.copy().splitStack(amount);
                break;
            }
        }
        return stack;
    }

    public static ItemStack getFirstUnCapsulatedOre(String name, int amount) {
        if (name2OreMap.containsKey(name)) return get(name, null, amount);
        ItemStack stack = null;
        List<ItemStack> ores = getOres(name);
        for (ItemStack ore : ores) {
            if (ore != null && GtUtil.getCapsuleCellContainerCount(ore) <= 0) {
                stack = ore.copy().splitStack(amount);
                break;
            }
        }
        return stack;
    }

    public static ItemStack get(String name, int amount) {
        return get(name, null, amount);
    }

    public static ItemStack get(String name, ItemStack replacement, int amount) {
        ItemStack stack = name2OreMap.get(name);
        if (!name2OreMap.containsKey(name) && replacement.isEmpty()) GregTechAPI.logger.error("Unknown key for unification, typo? " + name);
        if (stack.isEmpty()) {
            stack = (replacement.isEmpty()) ? null : replacement.copy();
        } else {
            stack = stack.copy();
            stack.setCount(amount);
        }
        return stack;
    }

    public static ItemStack get(ItemStack stack) {
        if (stack.isEmpty() || sBlackList.contains(stack)) return stack;
        String name = item2OreMap.get(stack);
        ItemStack ore = null;
        if (name != null) ore = name2OreMap.get(name);

        if (ore == null) ore = stack.copy();
        else ore = ore.copy();

        ore.setCount(stack.getCount());
        return ore;
    }

    public static void addAssociation(String name, ItemStack stack) {
        if (name == null || name.isEmpty() || stack.isEmpty()) return;
        item2OreMap.put(stack, name);
    }

    public static String getAssociation(ItemStack stack) {
        return item2OreMap.get(stack);
    }

    public static boolean isItemStackInstanceOf(ItemStack stack, String name, boolean prefix) {
        if (stack.isEmpty() || name == null || name.isEmpty()) return false;
        String string = item2OreMap.get(stack);
        if (string == null) {
            ItemStack ore = stack.copy();
            ore.setItemDamage(OreDictionary.WILDCARD_VALUE);
            string = item2OreMap.get(ore);
            if (string == null) {
                if (!prefix) {
                    for (ItemStack oreStack : getOres(name)) {
                        if (!oreStack.isItemEqual(stack)) continue;
                        return true;
                    }
                }
                return false;
            }
        }
        return prefix ? string.startsWith(name) : string.equals(name);
    }

    public static boolean isItemStackDye(ItemStack stack) {
        if (stack.isEmpty()) return false;
        for (EnumDyeColor dye : EnumDyeColor.values()) {
            if (isItemStackInstanceOf(stack, dye.getName(), false)) return true;
        }
        return false;
    }

    public static boolean registerOre(String name, ItemStack stack) {
        if (name == null || name.isEmpty() || stack.isEmpty()) return false;
        List<ItemStack> ores = getOres(name);
        for (int i = 0; i < ores.size(); ) {
            if (ores.get(i).isItemEqual(stack))
                return false;
            i++;
        }
        stack = stack.copy().splitStack(1);
        OreDictionary.registerOre(name, stack);
        return true;
    }

    public static List<ItemStack> getOres(String name) {
        return OreDictionary.getOres(name);
    }
}
