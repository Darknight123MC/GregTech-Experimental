package dev.su5ed.gregtechmod.blockentity;

import dev.su5ed.gregtechmod.api.util.NBTTarget;
import dev.su5ed.gregtechmod.blockentity.component.BlockEntityComponent;
import dev.su5ed.gregtechmod.network.GregTechNetwork;
import dev.su5ed.gregtechmod.util.BlockEntityProvider;
import dev.su5ed.gregtechmod.util.GtUtil;
import dev.su5ed.gregtechmod.util.nbt.NBTSaveHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import one.util.streamex.StreamEx;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public abstract class BaseBlockEntity extends BlockEntity {
    private final Map<ResourceLocation, BlockEntityComponent> components = new HashMap<>();

    protected BaseBlockEntity(BlockEntityProvider provider, BlockPos pos, BlockState state) {
        this(provider.getType(), pos, state);
    }

    protected BaseBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public void tickClient() {
    }

    public void tickServer() {
    }
    
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        return InteractionResult.PASS;
    }

    public Optional<ItemStack> getCloneItemStack(BlockState state, HitResult target, BlockGetter world, BlockPos pos, Player player) {
        return Optional.empty();
    }

    public void updateClientField(String name) {
        GregTechNetwork.updateClientField(this, name);
    }

    protected <T extends BlockEntityComponent> T addComponent(T component) {
        ResourceLocation name = component.getName();
        if (this.components.containsKey(name)) throw new RuntimeException("Duplicate component: " + name + " of type " + component.getClass().getName());
        else this.components.put(name, component);
        return component;
    }

    public Optional<BlockEntityComponent> getComponent(ResourceLocation name) {
        return Optional.ofNullable(this.components.get(name));
    }

    public void updateRenderClient() {
        GtUtil.ensureClient(this.level);

        requestModelDataUpdate();
        BlockState state = getBlockState();
        this.level.sendBlockUpdated(this.worldPosition, state, state, Block.UPDATE_IMMEDIATE);
    }

    @Override
    public CompoundTag getUpdateTag() {
        return saveTag(new CompoundTag(), NBTTarget.SYNC);
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        loadTag(tag, true);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        saveTag(tag, NBTTarget.SAVE);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        loadTag(tag, false);
    }

    private CompoundTag saveComponents(NBTTarget target) {
        CompoundTag tag = new CompoundTag();
        this.components.forEach((name, component) -> {
            CompoundTag compound = component.save(target);
            tag.put(name.toString(), compound);
        });
        return tag;
    }

    private void loadComponents(CompoundTag tag, boolean notifyListeners) {
        StreamEx.of(tag.getAllKeys())
            .mapToEntry(name -> this.components.get(new ResourceLocation(name)), tag::getCompound)
            .nonNullKeys()
            .forKeyValue((component, compound) -> component.load(compound, notifyListeners));
    }

    private CompoundTag saveTag(CompoundTag tag, NBTTarget target) {
        tag.put("fields", NBTSaveHandler.writeClassToNBT(this, target));
        tag.put("components", saveComponents(target));
        return tag;
    }

    private void loadTag(CompoundTag tag, boolean notifyListeners) {
        NBTSaveHandler.readClassFromNBT(this, tag.getCompound("fields"), notifyListeners);
        loadComponents(tag.getCompound("components"), notifyListeners);
    }
}
