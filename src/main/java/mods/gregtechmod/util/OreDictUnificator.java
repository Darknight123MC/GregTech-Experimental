package mods.gregtechmod.util;

import ic2.core.util.StackUtil;
import mods.gregtechmod.core.GregTechConfig;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import java.util.*;
import java.util.stream.Collectors;

public class OreDictUnificator {
    private static final Map<String, ItemStack> NAME_TO_ITEM = new HashMap<>();
    private static final Map<ItemStack, String> ITEM_TO_ORE = new HashMap<>();

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
        if (name == null || name.isEmpty() || stack.isEmpty() || stack.getItemDamage() < 0) return;
        
        ItemStack ore = StackUtil.copyWithSize(stack, 1);
        addAssociation(name, ore);
        if (!NAME_TO_ITEM.containsKey(name)) {
            NAME_TO_ITEM.put(name, ore);
        } else {
            if (overwrite && Arrays.asList(GregTechConfig.UNIFICATION.specialUnificationTargets).contains(getStackConfigName(ore))) {
                NAME_TO_ITEM.remove(name);
                NAME_TO_ITEM.put(name, ore);
            }
        }
        registerOre(name, ore);
    }

    public static void override(String name, ItemStack stack) {
        if (name == null || name.isEmpty() || name.startsWith("itemDust") || stack.isEmpty() || stack.getItemDamage() < 0) return;

        if (stack.getDisplayName().isEmpty() || Arrays.asList(GregTechConfig.UNIFICATION.specialUnificationTargets).contains(getStackConfigName(stack))) set(name, stack);
    }

    public static ItemStack getUnifiedOre(String name) {
        return getUnifiedOre(name, ItemStack.EMPTY);
    }
    
    public static ItemStack getUnifiedOre(String name, ItemStack defaultValue) {
        ItemStack stack = NAME_TO_ITEM.get(name);
        return stack == null ? defaultValue : stack;
    }
    
    public static boolean oreExists(String ore) {
        return !OreDictionary.getOres(ore).isEmpty();
    }

    public static OptionalItemStack getFirstOre(String name) {
        return getFirstOre(name, 1);
    }

    public static OptionalItemStack getFirstOre(String name, int amount) {
        if (name == null || name.isEmpty()) return OptionalItemStack.EMPTY;
        if (NAME_TO_ITEM.containsKey(name)) return OptionalItemStack.of(get(name, ItemStack.EMPTY, amount));

        ItemStack stack = ItemStack.EMPTY;
        List<ItemStack> ores = OreDictionary.getOres(name);
        if (!ores.isEmpty()) stack = ores.get(0).copy();
        if (!stack.isEmpty()) stack.setCount(amount);

        return OptionalItemStack.of(stack);
    }

    public static ItemStack get(String name) {
        return get(name, 1);
    }

    public static ItemStack get(String name, int amount) {
        return get(name, ItemStack.EMPTY, amount);
    }

    public static ItemStack get(String name, ItemStack replacement, int amount) {
        ItemStack stack = NAME_TO_ITEM.get(name);
        if (stack == null) {
            List<ItemStack> ores = OreDictionary.getOres(name);
            if (!ores.isEmpty()) return ores.get(0);
            else return replacement;
        } else return StackUtil.copyWithSize(stack, amount);
    }

    public static void addAssociation(String name, ItemStack stack) {
        if (name == null || name.isEmpty() || stack.isEmpty()) return;
        ITEM_TO_ORE.put(stack, name);
    }

    public static String getAssociation(ItemStack stack) {
        List<String> names = getAssociations(stack);

        return !names.isEmpty() ? names.get(0) : "";
    }

    public static List<String> getAssociations(ItemStack stack) {
        String name = ITEM_TO_ORE.get(stack);
        if (name == null) {
            int[] ids = OreDictionary.getOreIDs(stack);
            return Arrays.stream(ids)
                    .mapToObj(OreDictionary::getOreName)
                    .collect(Collectors.toList());
        } else return Collections.singletonList(name);
    }

    public static boolean isItemInstanceOf(Block block, String name, boolean prefix) {
        return isItemInstanceOf(new ItemStack(block), name, prefix);
    }

    public static boolean isItemInstanceOf(ItemStack stack, String name, boolean prefix) {
        if (stack.isEmpty() || name == null || name.isEmpty()) return false;

        List<String> names = getAssociations(stack);
        return names.stream()
                .anyMatch(str -> prefix ? str.startsWith(name) : str.equals(name));
    }

    public static void registerOre(String name, ItemStack stack) {
        if (name == null || name.isEmpty() || stack.isEmpty()) return;
        
        boolean nonexistent = OreDictionary.getOres(name).stream()
                .noneMatch(stack::isItemEqual);
        if (nonexistent) {
            ItemStack ore = StackUtil.copyWithSize(stack, 1);
            OreDictionary.registerOre(name, ore);
        }
    }

    public static String getStackConfigName(ItemStack stack) {
        if (stack.isEmpty()) return null;

        String name = OreDictUnificator.getAssociation(stack);
        if (!name.isEmpty()) return name;
        else if (!(name = stack.getDisplayName()).isEmpty()) return name;

        return stack.getItem().getRegistryName().toString() + ":" + stack.getItemDamage();
    }
}
