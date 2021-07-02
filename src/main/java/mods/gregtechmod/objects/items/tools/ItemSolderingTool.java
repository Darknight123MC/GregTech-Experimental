package mods.gregtechmod.objects.items.tools;

import ic2.api.item.ElectricItem;
import mods.gregtechmod.api.item.ISolderingMetal;
import mods.gregtechmod.api.item.ISolderingTool;
import mods.gregtechmod.core.GregTechMod;
import mods.gregtechmod.objects.items.base.ItemElectricBase;
import mods.gregtechmod.util.GtUtil;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class ItemSolderingTool extends ItemElectricBase implements ISolderingTool {

    public ItemSolderingTool() {
        super("soldering_tool", 10000, 1000, 1, 1000, false);
        setFolder("tool");
        setRegistryName("soldering_tool");
        setTranslationKey("soldering_tool");
        setCreativeTab(GregTechMod.GREGTECH_TAB);
        this.hasEmptyVariant = true;
    }

    public ItemStack findSolderingMetal(EntityPlayer player) {
        for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
            ItemStack stack = player.inventory.getStackInSlot(i);
            if (stack.getItem() instanceof ISolderingMetal) return stack;
        }
        return null;
    }

    @Override
    public boolean solder(ItemStack stack, EntityPlayer player, boolean simulate) {
        if (ElectricItem.manager.getCharge(stack) < 1000) return false;
        ItemStack metalStack = findSolderingMetal(player);
        if (metalStack == null) return false;
        ISolderingMetal metal = (ISolderingMetal) metalStack.getItem();
        if (!metal.canUse()) return false;

        if (!simulate) {
            ElectricItem.manager.use(stack, 1000, player);
            metal.onUsed(player, metalStack);
        }
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        if (ElectricItem.manager.canUse(stack, this.operationEnergyCost)) {
            tooltip.add(GtUtil.translateItem("soldering_tool.metal_requirement"));
        }

        super.addInformation(stack, worldIn, tooltip, flagIn);
    }
}
