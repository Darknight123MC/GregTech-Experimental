package dev.su5ed.gregtechmod.cover;

import dev.su5ed.gregtechmod.api.cover.CoverType;
import dev.su5ed.gregtechmod.api.cover.ICoverable;
import dev.su5ed.gregtechmod.api.machine.IElectricMachine;
import dev.su5ed.gregtechmod.api.machine.IGregTechMachine;
import dev.su5ed.gregtechmod.api.machine.IUpgradableMachine;
import dev.su5ed.gregtechmod.api.util.CoverInteractionResult;
import dev.su5ed.gregtechmod.util.GtLocale;
import dev.su5ed.gregtechmod.util.GtUtil;
import dev.su5ed.gregtechmod.util.nbt.NBTPersistent;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;

import java.util.Locale;

public class EnergyMeterCover extends BaseCover {
    public static final ResourceLocation TEXTURE = GtUtil.getCoverTexture("eu_meter");

    @NBTPersistent
    protected Mode mode = Mode.UNIVERSAL;

    public EnergyMeterCover(ResourceLocation name, ICoverable be, Direction side, Item item) {
        super(name, be, side, item);
    }

    @Override
    public void doCoverThings() {
        if (this.be instanceof IElectricMachine machine) {
            int strength;

            if (this.mode == Mode.AVERAGE_EU_IN || this.mode == Mode.AVERAGE_EU_IN_INVERTED) {
                strength = (int) (machine.getAverageEUInput() / (machine.getMaxInputEUp() / 15));
            }
            else if (this.mode == Mode.AVERAGE_EU_OUT || this.mode == Mode.AVERAGE_EU_OUT_INVERTED) {
                strength = (int) (machine.getAverageEUOutput() / (machine.getMaxOutputEUt() / 15));
            }
            else {
                boolean upgradable = machine instanceof IUpgradableMachine;
                double stored = -1;
                double capacity = 1;

                if (this.mode == Mode.UNIVERSAL || this.mode == Mode.UNIVERSAL_INVERTED) {
                    stored = upgradable ? ((IUpgradableMachine) machine).getUniversalEnergy() : machine.getStoredEU();
                    capacity = upgradable ? ((IUpgradableMachine) machine).getUniversalEnergyCapacity() : machine.getEUCapacity();
                }
                else if (this.mode == Mode.ELECTRICITY || this.mode == Mode.ELECTRICITY_INVERTED) {
                    stored = machine.getStoredEU();
                    capacity = machine.getEUCapacity();
                }
                else if (upgradable) {
                    if ((this.mode == Mode.MJ || this.mode == Mode.MJ_INVERTED) && ((IUpgradableMachine) machine).hasMjUpgrade()) {
                        stored = ((IUpgradableMachine) machine).getStoredMj();
                        capacity = ((IUpgradableMachine) machine).getMjCapacity();
                    }
                    else if ((this.mode == Mode.STEAM || this.mode == Mode.STEAM_INVERTED) && ((IUpgradableMachine) machine).hasSteamTank()) {
                        stored = ((IUpgradableMachine) machine).getStoredSteam();
                        capacity = ((IUpgradableMachine) machine).getSteamCapacity();
                    }
                }

                strength = (int) ((stored + 1) / capacity * 15);
            }

            if (strength > 0) {
                machine.setRedstoneOutput(side, this.mode.inverted ? 15 - strength : strength);
            }
            else {
                machine.setRedstoneOutput(side, this.mode.inverted ? 15 : 0);
            }
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
        return CoverInteractionResult.UPDATE;
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
        if (this.be instanceof IGregTechMachine machine) {
            machine.setRedstoneOutput(this.side, 0);
        }
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
    public CoverType getType() {
        return CoverType.METER;
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
