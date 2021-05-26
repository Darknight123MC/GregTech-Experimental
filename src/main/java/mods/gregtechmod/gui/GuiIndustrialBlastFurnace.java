package mods.gregtechmod.gui;

import ic2.core.gui.LinkedGauge;
import mods.gregtechmod.api.util.Reference;
import mods.gregtechmod.objects.blocks.teblocks.container.ContainerBlastFurnace;
import mods.gregtechmod.util.GtUtil;
import net.minecraft.util.ResourceLocation;

public class GuiIndustrialBlastFurnace extends GuiStructure<ContainerBlastFurnace> {
    public static final ResourceLocation TEXTURE = new ResourceLocation(Reference.MODID, "textures/gui/industrial_blast_furnace.png");

    public GuiIndustrialBlastFurnace(ContainerBlastFurnace container) {
        super(container);
        
        addElement(new LinkedGauge(this, 58, 28, container.base, "progress", GregtechGauge.BLASTING));
    }

    @Override
    protected void drawForegroundLayer(int mouseX, int mouseY) {
        super.drawForegroundLayer(mouseX, mouseY);
    }
        
    @Override
    protected void doWhenValid() {
        drawString(8, ySize - 103, GtUtil.translateInfo("heat_capacity", this.container.base.getHeatCapacity()), 4210752, false);
    }

    @Override
    protected ResourceLocation getTexture() {
        return TEXTURE;
    }
}
