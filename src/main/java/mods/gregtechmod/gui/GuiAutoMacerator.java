package mods.gregtechmod.gui;

import mods.gregtechmod.objects.blocks.teblocks.container.ContainerBasicMachine;
import mods.gregtechmod.util.GtUtil;
import net.minecraft.util.ResourceLocation;

public class GuiAutoMacerator extends GuiBasicMachine<ContainerBasicMachine<?>> {
    public static final ResourceLocation TEXTURE = GtUtil.getGuiTexture("auto_macerator");

    public GuiAutoMacerator(ContainerBasicMachine<?> container) {
        super(container, GregtechGauge.MACERATING);
    }

    @Override
    protected ResourceLocation getTexture() {
        return TEXTURE;
    }
}
