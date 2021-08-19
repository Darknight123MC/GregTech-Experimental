package mods.gregtechmod.util;

import ic2.core.profile.Version;
import mods.gregtechmod.compat.ModHandler;
import mods.gregtechmod.core.GregTechMod;
import mods.gregtechmod.objects.BlockItems;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.lang.reflect.Field;

public class ProfileDelegate {

    public static boolean shouldEnable(IItemProvider holder) {
        try {
            Field field = holder.getClass().getField(holder.name());
            return Version.shouldEnable(field);
        } catch (NoSuchFieldException e) {
            GregTechMod.logger.catching(e);
        }
        return false;
    }

    public static ItemStack getEmptyCell() {
        return getCell(null);
    }
    
    public static ItemStack getCell(String fluid) {
        if (GregTechMod.classic) {
            Item cell = BlockItems.classicCells.get(fluid);
            if (cell != null) return new ItemStack(cell);
            else {
                if (fluid == null) fluid = "empty";
                else if (fluid.startsWith("ic2")) fluid = fluid.substring(3);
                return ModHandler.getIC2ItemSafely("cell", fluid);
            }
        } else return ModHandler.getIC2ItemSafely("fluid_cell", fluid == null || fluid.equals("empty") ? null : fluid);
    }
}
