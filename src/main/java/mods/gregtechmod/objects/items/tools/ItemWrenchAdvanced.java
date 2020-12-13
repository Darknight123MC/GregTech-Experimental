package mods.gregtechmod.objects.items.tools;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItem;
import ic2.core.item.ElectricItemManager;
import ic2.core.item.IPseudoDamageItem;
import mods.gregtechmod.core.GregTechMod;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class ItemWrenchAdvanced extends ItemWrench implements IElectricItem, IPseudoDamageItem {

    public ItemWrenchAdvanced() {
        super("wrench_advanced", 0);
        setRegistryName("wrench_advanced");
        setCreativeTab(GregTechMod.GREGTECH_TAB);
        setNoRepair();
        this.rotateDamage = 1000;
        this.removeDamage = 3000;
        this.showDurability = false;
    }

    @Override
    public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack) {
        return HashMultimap.create();
    }

    public boolean canTakeDamage(ItemStack stack, int amount) {
        return ElectricItem.manager.getCharge(stack) >= amount;
    }

    public void damage(ItemStack stack, int amount, EntityPlayer player) {
        ElectricItem.manager.use(stack, amount, player);
    }

    @Override
    public boolean canProvideEnergy(ItemStack itemStack) {
        return false;
    }

    @Override
    public double getMaxCharge(ItemStack itemStack) {
        return 128000;
    }

    @Override
    public int getTier(ItemStack itemStack) {
        return 1;
    }

    @Override
    public double getTransferLimit(ItemStack itemStack) {
        return 128;
    }

    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems) {
        if (isInCreativeTab(tab)) ElectricItemManager.addChargeVariants(this, subItems);
    }

    @Override
    public void setStackDamage(ItemStack itemStack, int damage) {
        super.setDamage(itemStack, damage);
    }
}
