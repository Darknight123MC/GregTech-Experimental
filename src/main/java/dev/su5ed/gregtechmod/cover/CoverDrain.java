package dev.su5ed.gregtechmod.cover;

import dev.su5ed.gregtechmod.api.cover.CoverType;
import dev.su5ed.gregtechmod.api.cover.ICoverable;
import dev.su5ed.gregtechmod.api.machine.IGregTechMachine;
import dev.su5ed.gregtechmod.util.GtLocale;
import dev.su5ed.gregtechmod.util.GtUtil;
import dev.su5ed.gregtechmod.util.nbt.NBTPersistent;
import ic2.core.util.LiquidUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;

import java.util.Locale;

public class CoverDrain extends CoverGeneric {
    public static final ResourceLocation TEXTURE = GtUtil.getCoverTexture("drain");

    @NBTPersistent
    protected DrainMode mode = DrainMode.IMPORT;

    public CoverDrain(ResourceLocation name, ICoverable be, Direction side, ItemStack stack) {
        super(name, be, side, stack);
    }

    @Override
    public ResourceLocation getIcon() {
        return TEXTURE;
    }

    @Override
    public void doCoverThings() {
        if (!canWork()) return;

        BlockEntity be = (BlockEntity) this.be;
        Level level = be.getLevel();
        BlockPos pos = be.getBlockPos();
        BlockPos offset = pos.relative(this.side);
        Block block = level.getBlockState(offset).getBlock();

        if (LiquidUtil.isFluidTile(be, this.side) && this.mode.isImport) {
            be.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, this.side).ifPresent(handler -> {
                if (this.side == Direction.UP && level.isRainingAt(pos)) {
                    int amount = (int) (level.getBiome(pos).getDownfall() * 10);
                    if (amount > 0) {
                        handler.fill(new FluidStack(Fluids.WATER, level.isThundering() ? amount * 2 : amount), FluidAction.EXECUTE);
                    }
                }

                FluidStack liquid;
                if (block == Blocks.WATER) liquid = new FluidStack(Fluids.WATER, FluidAttributes.BUCKET_VOLUME);
                else if (block == Blocks.LAVA) liquid = new FluidStack(Fluids.LAVA, FluidAttributes.BUCKET_VOLUME);
                else if (block instanceof IFluidBlock fluid) liquid = fluid.drain(level, offset, FluidAction.SIMULATE);
                else liquid = null;

                if (liquid != null) {
                    Fluid fluid = liquid.getFluid();
                    if (fluid != null) {
                        int density = fluid.getAttributes().getDensity();

                        if ((this.side != Direction.DOWN || density <= 0) && (this.side != Direction.UP || density >= 0) && handler.fill(liquid, FluidAction.SIMULATE) == liquid.getAmount()) {
                            handler.fill(liquid, FluidAction.EXECUTE);
                            level.removeBlock(offset, false);
                        }
                    }
                }
            });
        }
        if (!this.mode.isImport && block != Blocks.AIR && (block instanceof LiquidBlock || block instanceof IFluidBlock)) {
            level.setBlock(offset, Blocks.AIR.defaultBlockState(), 0);
        }
    }

    @Override
    public boolean onScrewdriverClick(Player player) {
        this.mode = this.mode.next();
        GtUtil.sendActionBarMessage(player, this.mode.getMessageKey());
        return true;
    }

    @Override
    public boolean letsLiquidsIn() {
        return canWork();
    }

    @Override
    public int getTickRate() {
        return this.mode.tickRate;
    }

    public boolean canWork() {
        return !(this.mode.conditional && this.be instanceof IGregTechMachine machine && machine.isAllowedToWork() == this.mode.inverted);
    }

    @Override
    public CoverType getType() {
        return CoverType.IO;
    }

    private enum DrainMode {
        IMPORT(50, true),
        IMPORT_CONDITIONAL(50, true, true),
        IMPORT_CONDITIONAL_INVERTED(50, true, true, true),
        KEEP_LIQUIDS_AWAY(1),
        KEEP_LIQUIDS_AWAY_CONDITIONAL(1, false, true),
        KEEP_LIQUIDS_AWAY_CONDITIONAL_INVERTED(1, false, true, true);

        private static final DrainMode[] VALUES = values();
        public final int tickRate;
        public final boolean isImport;
        public final boolean conditional;
        public final boolean inverted;

        DrainMode(int tickRate) {
            this(tickRate, false);
        }

        DrainMode(int tickRate, boolean isImport) {
            this(tickRate, isImport, false);
        }

        DrainMode(int tickRate, boolean isImport, boolean conditional) {
            this(tickRate, isImport, conditional, false);
        }

        DrainMode(int tickRate, boolean isImport, boolean conditional, boolean inverted) {
            this.tickRate = tickRate;
            this.isImport = isImport;
            this.conditional = conditional;
            this.inverted = inverted;
        }

        public DrainMode next() {
            return VALUES[(ordinal() + 1) % VALUES.length];
        }

        public GtLocale.TranslationKey getMessageKey() {
            return GtLocale.key("cover", "inventory_mode", name().toLowerCase(Locale.ROOT));
        }
    }
}
