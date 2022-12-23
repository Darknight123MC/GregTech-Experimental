package dev.su5ed.gregtechmod.blockentity.component;

import dev.su5ed.gregtechmod.blockentity.base.BaseBlockEntity;
import dev.su5ed.gregtechmod.network.GregTechNetwork;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

// TODO use interface instead of BaseBlockEntity
public abstract class GtComponentBase<T extends BaseBlockEntity> implements BlockEntityComponent {
    protected final T parent;

    protected GtComponentBase(T parent) {
        this.parent = parent;
    }

    @Override
    public T getParent() {
        return this.parent;
    }
    
    @Override
    public void onLoad() {}

    @Override
    public void onUnload() {}

    @Override
    public <U> LazyOptional<U> getCapability(@NotNull Capability<U> cap, @Nullable Direction side) {
        return LazyOptional.empty();
    }

    @Override
    public void invalidateCaps() {}
    
    @Override
    public void tickServer() {}

    public void updateClient() {
        GregTechNetwork.updateClientComponent(this.parent, this);
    }
    
    public boolean isServerSide() {
        return !this.parent.getLevel().isClientSide;
    }

    @Override
    public void getScanInfo(List<Component> scan, Player player, BlockPos pos, int scanLevel) {}
}
