package dev.su5ed.gregtechmod.cover;

import dev.su5ed.gregtechmod.api.cover.ICover;
import dev.su5ed.gregtechmod.api.cover.ICoverable;
import dev.su5ed.gregtechmod.api.util.CoverInteractionResult;
import dev.su5ed.gregtechmod.api.util.NBTTarget;
import dev.su5ed.gregtechmod.util.nbt.NBTSaveHandler;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;

public abstract class BaseCover implements ICover {
    private final ResourceLocation name;
    protected final ICoverable be;
    protected final Direction side;
    protected final Item item;

    protected BaseCover(ResourceLocation name, ICoverable be, Direction side, Item item) {
        this.name = name;
        this.be = be;
        this.side = side;
        this.item = item;
    }

    @Override
    public ResourceLocation getName() {
        return name;
    }

    @Override
    public void doCoverThings() {}

    @Override
    public boolean onCoverRightClick(Player player, InteractionHand hand, Direction side, float hitX, float hitY, float hitZ) {
        return false;
    }

    @Override
    public CoverInteractionResult onScrewdriverClick(Player player) {
        return !player.level.isClientSide && player instanceof ServerPlayer sp ? onServerScrewdriverClick(sp) : onClientScrewdriverClick(player);
    }
    
    protected CoverInteractionResult onClientScrewdriverClick(Player player) {
        return CoverInteractionResult.PASS;
    }
    
    protected CoverInteractionResult onServerScrewdriverClick(ServerPlayer player) {
        return CoverInteractionResult.PASS;
    }

    @Override
    public boolean allowEnergyTransfer() {
        return false;
    }

    @Override
    public boolean letsRedstoneIn() {
        return false;
    }

    @Override
    public boolean letsRedstoneOut() {
        return false;
    }

    @Override
    public boolean letsLiquidsIn() {
        return false;
    }

    @Override
    public boolean letsLiquidsOut() {
        return false;
    }

    @Override
    public boolean letsItemsIn() {
        return false;
    }

    @Override
    public boolean letsItemsOut() {
        return false;
    }

    @Override
    public boolean opensGui(Direction side) {
        return false;
    }

    @Override
    public boolean acceptsRedstone() {
        return false;
    }

    @Override
    public boolean overrideRedstoneOut() {
        return false;
    }

    @Override
    public int getRedstoneInput() {
        return 0;
    }

    @Override
    public Direction getSide() {
        return this.side;
    }

    @Override
    public Item getItem() {
        return this.item;
    }

    @Override
    public CompoundTag save(NBTTarget target) {
        return NBTSaveHandler.writeClassToNBT(this, target);
    }

    @Override
    public void load(CompoundTag tag, boolean notifyListeners) {
        NBTSaveHandler.readClassFromNBT(this, tag, notifyListeners);
    }

    @Override
    public int getTickRate() {
        return 0;
    }

    @Override
    public void onCoverRemove() {}
}
