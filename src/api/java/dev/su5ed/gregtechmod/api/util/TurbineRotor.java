package dev.su5ed.gregtechmod.api.util;

import net.minecraft.world.item.ItemStack;

public record TurbineRotor(ItemStack item, int efficiency, int efficiencyMultiplier, int damageToComponent) {}