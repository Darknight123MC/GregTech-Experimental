package mods.gregtechmod.gui;

import ic2.core.ContainerBase;
import ic2.core.GuiIC2;
import ic2.core.gui.CustomButton;
import ic2.core.gui.CycleHandler;
import ic2.core.gui.Gauge;
import ic2.core.gui.LinkedGauge;
import mods.gregtechmod.core.GregTechMod;
import mods.gregtechmod.objects.blocks.teblocks.base.TileEntityBasicMachine;
import mods.gregtechmod.util.NumericValueHandler;
import net.minecraft.client.resources.I18n;

public abstract class GuiBasicMachine<T extends ContainerBase<? extends TileEntityBasicMachine<?, ?, ?, ?>>> extends GuiIC2<T> {
    
    public GuiBasicMachine(T container, Gauge.IGaugeStyle gaugeStyle) {
        super(container);

        CycleHandler energyCycleHandler = new CycleHandler(58, 0, 76, 18, 18, true, 2,
                new NumericValueHandler(container.base, 0, () -> GuiBasicMachine.this.container.base.provideEnergy ? 1 : 0));
        this.addElement(new CustomButton(this, 7, 62, 18, 18, energyCycleHandler, GregTechMod.COMMON_TEXTURE, energyCycleHandler));

        CycleHandler autoOutput = new CycleHandler(76, 0, 94, 18, 18, true, 2,
                new NumericValueHandler(container.base, 2, () -> GuiBasicMachine.this.container.base.autoOutput ? 1 : 0));
        this.addElement(new CustomButton(this, 25, 62, 18, 18, autoOutput, GregTechMod.COMMON_TEXTURE, autoOutput));

        CycleHandler splitInput = new CycleHandler(94, 0, 112, 18, 18, true, 2,
                new NumericValueHandler(container.base, 4, () -> GuiBasicMachine.this.container.base.splitInput ? 1 : 0));
        this.addElement(new CustomButton(this, 43, 62, 18, 18, splitInput, GregTechMod.COMMON_TEXTURE, splitInput));

        addElement(new LinkedGauge(this, 78, 24, container.base, "progress", gaugeStyle));
    }

    @Override
    protected void drawBackgroundAndTitle(float partialTicks, int mouseX, int mouseY) {
        bindTexture();
        drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
        drawString(8, 4, I18n.format(this.container.base.getName()), 4210752, false);
    }
}
