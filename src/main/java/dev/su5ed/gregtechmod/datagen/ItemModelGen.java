package dev.su5ed.gregtechmod.datagen;

import dev.su5ed.gregtechmod.api.util.Reference;
import dev.su5ed.gregtechmod.object.*;
import dev.su5ed.gregtechmod.util.ItemProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import one.util.streamex.StreamEx;

import javax.annotation.Nonnull;

import static dev.su5ed.gregtechmod.api.util.Reference.location;

class ItemModelGen extends ItemModelProvider {
    private final ResourceLocation generatedParent = mcLoc("item/generated");
    
    public ItemModelGen(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, Reference.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        StreamEx.<ItemProvider>of(ModBlock.values())
            .append(Ore.values())
            .map(block -> block.getItem().getRegistryName().getPath())
            .mapToEntry(name -> location("block", name))
            .forKeyValue(this::withExistingParent);
        
        registerItems(Ingot.values(), "ingot");
        registerItems(Nugget.values(), "nugget");
        registerItems(Rod.values(), "rod");
        registerItems(Dust.values(), "dust");
        registerItems(Smalldust.values(), "smalldust");
        registerItems(Plate.values(), "plate");
        registerItems(TurbineRotor.values(), "turbine_rotor");
    }

    @Nonnull
    @Override
    public String getName() {
        return Reference.NAME + " Item Models";
    }

    private void registerItems(ItemProvider[] providers, String folder) {
        StreamEx.of(providers)
            .forEach(provider -> {
                String path = provider.getItem().getRegistryName().getPath();
                ResourceLocation texture = location("item", folder, provider.getName());
                singleTexture(path, this.generatedParent, "layer0", texture);
            });
    }
}
