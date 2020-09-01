package mods.gregtechmod.common.objects.items.base;

import ic2.api.reactor.IReactor;
import ic2.core.item.reactor.ItemReactorHeatStorage;
import mods.gregtechmod.common.core.GregtechMod;
import mods.gregtechmod.common.util.IHasModel;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.List;

public class ItemNuclearHeatStorage extends ItemReactorHeatStorage implements IHasModel {
    private final String name;

    public ItemNuclearHeatStorage(String name, int heatStorage) {
        super(null, heatStorage);
        setRegistryName(name);
        setMaxStackSize(1);
        this.name = name;
    }

    @Override
    public String getTranslationKey() {
        return "item."+this.name;
    }

    @Override
    public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag advanced) {
        tooltip.add("Stored heat: "+this.getCustomDamage(stack));
    }

    @Override
    public float influenceExplosion(ItemStack stack, IReactor reactor) {
        return 1.0F + this.getMaxCustomDamage(stack) / 30000.0F;
    }

    @Override
    public void registerModels() {
        GregtechMod.proxy.registerModel(this, 0, this.name, null, "nuclear");
    }
}
