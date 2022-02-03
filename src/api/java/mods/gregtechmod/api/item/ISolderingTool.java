package mods.gregtechmod.api.item;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public interface ISolderingTool {
    /**
     * Uses the soldering tool, draining its durability and the durability of the soldering metal
     * @param stack ItemStack containing the soldering tool
     * @param player The player soldering
     * @param simulate Whether the solder should be simulated or not. Such simulation can be used to determine if the solder can be performed.
     * @return <code>true</code> if the solder was performed successfully
     */
    boolean solder(ItemStack stack, Player player, boolean simulate);
}
