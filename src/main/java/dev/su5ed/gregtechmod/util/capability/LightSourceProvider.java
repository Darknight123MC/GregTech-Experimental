package dev.su5ed.gregtechmod.util.capability;

import dev.su5ed.gregtechmod.Capabilities;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.Nullable;

public class LightSourceProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
    private final LightSource instance = new LightSourceStore();
    private final LazyOptional<LightSource> optionalData = LazyOptional.of(() -> instance);

    @Override
    public CompoundTag serializeNBT() {
        return this.instance.serializeNBT();
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.instance.deserializeNBT(nbt);
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
        return Capabilities.LIGHT_SOURCE.orEmpty(cap, this.optionalData);
    }
}
