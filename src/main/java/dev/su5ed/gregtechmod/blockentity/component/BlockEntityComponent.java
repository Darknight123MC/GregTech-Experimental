package dev.su5ed.gregtechmod.blockentity.component;

import dev.su5ed.gregtechmod.blockentity.BaseBlockEntity;
import dev.su5ed.gregtechmod.util.nbt.FieldUpdateListener;
import dev.su5ed.gregtechmod.util.nbt.NBTPersistent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

public interface BlockEntityComponent extends FieldUpdateListener {
    BaseBlockEntity getParent();
    
    ResourceLocation getName();
    
    CompoundTag save(NBTPersistent.Mode mode);
    
    void load(CompoundTag tag, boolean notifyListeners);
}