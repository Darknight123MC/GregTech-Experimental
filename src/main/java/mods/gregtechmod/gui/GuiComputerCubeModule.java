package mods.gregtechmod.gui;

import ic2.core.ContainerBase;
import ic2.core.GuiIC2;
import mods.gregtechmod.objects.blocks.teblocks.computercube.IComputerCubeModule;
import mods.gregtechmod.objects.blocks.teblocks.computercube.TileEntityComputerCube;

public abstract class GuiComputerCubeModule<M extends IComputerCubeModule, T extends ContainerBase<? extends TileEntityComputerCube>> extends GuiIC2<T> {
    private final Class<M> moduleClass;
    private M module;

    public GuiComputerCubeModule(T container, int xSize, Class<M> moduleClass) {
        super(container, xSize, 166);
        
        this.moduleClass = moduleClass;
    }

    @Override
    protected void drawBackgroundAndTitle(float partialTicks, int mouseX, int mouseY) {
        this.bindTexture();
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
        
        // cache module to prevent disappearing text when switching modules
        IComputerCubeModule newModule = this.container.base.getActiveModule();
        if (this.moduleClass.isInstance(newModule)) this.module = this.moduleClass.cast(newModule);
        
        if (this.module != null) drawBackground(this.module, partialTicks, mouseX, mouseY);
    }
    
    protected void drawBackground(M module, float partialTicks, int mouseX, int mouseY) {}
}
