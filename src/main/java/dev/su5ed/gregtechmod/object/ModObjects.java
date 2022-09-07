package dev.su5ed.gregtechmod.object;

import dev.su5ed.gregtechmod.GregTechTab;
import dev.su5ed.gregtechmod.util.BlockEntityProvider;
import dev.su5ed.gregtechmod.util.BlockItemProvider;
import dev.su5ed.gregtechmod.util.ItemProvider;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;
import one.util.streamex.StreamEx;

public final class ModObjects {
    public static final ModObjects INSTANCE = new ModObjects();

    private ModObjects() {}

    public static Item.Properties itemProperties() {
        return new Item.Properties().tab(GregTechTab.INSTANCE);
    }

    @SubscribeEvent
    public void registerBlocks(RegistryEvent.Register<Block> event) {
        IForgeRegistry<Block> registry = event.getRegistry();

        StreamEx.<BlockItemProvider>of(ModBlock.values())
            .append(Ore.values())
            .append(GTBlockEntity.values())
            .map(BlockItemProvider::getBlock)
            .forEach(registry::register);
    }

    @SubscribeEvent
    public void registerItems(RegistryEvent.Register<Item> event) {
        IForgeRegistry<Item> registry = event.getRegistry();

        StreamEx.<ItemProvider>of(ModBlock.values())
            .append(Ore.values())
            .append(Ingot.values())
            .append(Nugget.values())
            .append(Rod.values())
            .append(Dust.values())
            .append(Smalldust.values())
            .append(Plate.values())
            .append(TurbineRotor.values())
            .append(Component.values())
            .append(GTBlockEntity.values())
            .append(ModCoverItem.values())
            .append(Tool.values())
            .append(Upgrade.values())
            .append(Miscellaneous.values())
            .append(ColorSpray.values())
            .append(Wrench.values())
            .append(JackHammer.values())
            .append(Hammer.values())
            .append(Saw.values())
            .append(File.values())
            .append(Cell.values())
            .append(NuclearCoolantPack.values())
            .map(ItemProvider::getItem)
            .forEach(registry::register);
    }

    @SubscribeEvent
    public void registerBlockEntities(RegistryEvent.Register<BlockEntityType<?>> event) {
        IForgeRegistry<BlockEntityType<?>> registry = event.getRegistry();

        StreamEx.<BlockEntityProvider>of(GTBlockEntity.values())
            .map(BlockEntityProvider::getType)
            .forEach(registry::register);
    }
}
