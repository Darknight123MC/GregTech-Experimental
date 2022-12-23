package dev.su5ed.gregtechmod.cover;

import dev.su5ed.gregtechmod.api.cover.CoverInteractionResult;
import dev.su5ed.gregtechmod.api.cover.CoverType;
import dev.su5ed.gregtechmod.api.machine.ElectricBlockEntity;
import dev.su5ed.gregtechmod.api.machine.PowerProvider;
import dev.su5ed.gregtechmod.api.machine.UpgradableBlockEntity;
import dev.su5ed.gregtechmod.api.util.FriendlyCompoundTag;
import dev.su5ed.gregtechmod.api.util.GtFluidTank;
import dev.su5ed.gregtechmod.util.GtLocale;
import dev.su5ed.gregtechmod.util.GtUtil;
import dev.su5ed.gregtechmod.util.power.SteamPowerProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;

import java.util.Locale;

public class EnergyMeterCover extends BaseCover<ElectricBlockEntity> {
    public static final ResourceLocation TEXTURE = GtUtil.getCoverTexture("eu_meter");

    protected Mode mode = Mode.UNIVERSAL;

    public EnergyMeterCover(CoverType<ElectricBlockEntity> type, ElectricBlockEntity be, Direction side, Item item) {
        super(type, be, side, item);
    }

    @Override
    public void tick() {
        int strength;

        if (this.mode == Mode.AVERAGE_EU_IN || this.mode == Mode.AVERAGE_EU_IN_INVERTED) {
            strength = (int) (this.energyHandler.getAverageInput() / (this.energyHandler.getMaxInputEUp() / 15));
        }
        else if (this.mode == Mode.AVERAGE_EU_OUT || this.mode == Mode.AVERAGE_EU_OUT_INVERTED) {
            strength = (int) (this.energyHandler.getAverageOutput() / (this.energyHandler.getMaxOutputEUt() / 15));
        }
        else {
            double stored = -1;
            double capacity = 1;

            if (this.mode == Mode.UNIVERSAL || this.mode == Mode.UNIVERSAL_INVERTED) {
                stored = this.energyHandler.getStoredEnergy();
                capacity = this.energyHandler.getEnergyCapacity();
            }
            else if (this.mode == Mode.ELECTRICITY || this.mode == Mode.ELECTRICITY_INVERTED) {
                PowerProvider defaultProvider = this.energyHandler.getDefaultPowerProvider();
                stored = defaultProvider.getStoredEnergy();
                capacity = defaultProvider.getCapacity();
            }
            else if (this.be instanceof UpgradableBlockEntity upgradable) {
//                if ((this.mode == Mode.MJ || this.mode == Mode.MJ_INVERTED) && upgradable.hasMjUpgrade()) { TODO
//                    stored = upgradable.getStoredMj();
//                    capacity = upgradable.getMjCapacity();
//                }
                if (this.mode == Mode.STEAM || this.mode == Mode.STEAM_INVERTED) {
                    GtFluidTank steamTank = this.energyHandler.getPowerProvider(SteamPowerProvider.class)
                        .map(SteamPowerProvider::getSteamTank)
                        .orElse(null);
                    if (steamTank != null) {
                        stored = steamTank.getFluidAmount();
                        capacity = steamTank.getCapacity();
                    }
                }
            }

            strength = (int) ((stored + 1) / capacity * 15);
        }

        if (strength > 0) {
            this.be.setRedstoneOutput(this.side, this.mode.inverted ? 15 - strength : strength);
        }
        else {
            this.be.setRedstoneOutput(this.side, this.mode.inverted ? 15 : 0);
        }
    }

    @Override
    protected CoverInteractionResult onClientScrewdriverClick(Player player) {
        return CoverInteractionResult.SUCCESS;
    }

    @Override
    protected CoverInteractionResult onServerScrewdriverClick(ServerPlayer player) {
        this.mode = this.mode.next();
        GtUtil.sendActionBarMessage(player, this.mode.getMessageKey());
        return CoverInteractionResult.CHANGED;
    }

    @Override
    public boolean acceptsRedstone() {
        return true;
    }

    @Override
    public boolean allowEnergyTransfer() {
        return true;
    }

    @Override
    public boolean letsLiquidsIn() {
        return true;
    }

    @Override
    public boolean letsLiquidsOut() {
        return true;
    }

    @Override
    public boolean letsItemsIn() {
        return true;
    }

    @Override
    public boolean letsItemsOut() {
        return true;
    }

    @Override
    public boolean overrideRedstoneOut() {
        return true;
    }

    @Override
    public void onCoverRemove() {
        this.be.setRedstoneOutput(this.side, 0);
    }

    @Override
    public ResourceLocation getIcon() {
        return TEXTURE;
    }

    @Override
    public int getTickRate() {
        return 5;
    }

    @Override
    public void save(FriendlyCompoundTag tag) {
        super.save(tag);
        tag.putEnum("mode", this.mode);
    }

    @Override
    public void load(FriendlyCompoundTag tag) {
        super.load(tag);
        this.mode = tag.getEnum("mode");
    }

    private enum Mode {
        UNIVERSAL,
        UNIVERSAL_INVERTED(true),
        ELECTRICITY,
        ELECTRICITY_INVERTED(true),
        MJ,
        MJ_INVERTED(true),
        STEAM,
        STEAM_INVERTED(true),
        AVERAGE_EU_IN,
        AVERAGE_EU_IN_INVERTED(true),
        AVERAGE_EU_OUT,
        AVERAGE_EU_OUT_INVERTED(true);

        public final boolean inverted;
        private static final Mode[] VALUES = values();

        Mode() {
            this(false);
        }

        Mode(boolean inverted) {
            this.inverted = inverted;
        }

        public Mode next() {
            return VALUES[(ordinal() + 1) % VALUES.length];
        }

        public GtLocale.TranslationKey getMessageKey() {
            return GtLocale.key("cover", "energy_meter", "mode", name().toLowerCase(Locale.ROOT));
        }
    }
}
