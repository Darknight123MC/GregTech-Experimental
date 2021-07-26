package mods.gregtechmod.gui;

import mods.gregtechmod.api.util.Reference;
import mods.gregtechmod.objects.blocks.teblocks.container.ContainerBasicMachine;
import net.minecraft.util.ResourceLocation;

public class GuiBender extends GuiBasicMachine<ContainerBasicMachine<?>> {
    public static final ResourceLocation TEXTURE = new ResourceLocation(Reference.MODID, "textures/gui/bender.png");

    public GuiBender(ContainerBasicMachine<?> container) {
        super(container, GregtechGauge.BENDING);
    }

    @Override
    protected ResourceLocation getTexture() {
        return TEXTURE;
    }
}
