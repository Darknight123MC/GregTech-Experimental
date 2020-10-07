package mods.gregtechmod.api.machine;

import net.minecraft.util.EnumFacing;

public interface IGregtechMachine {
    boolean isActive();

    double getProgress();

    int getMaxProgress();

    void increaseProgress(double amount);

    void setRedstoneOutput(EnumFacing side, byte strength);

    double addEnergy(double amount);

    double useEnergy(double amount, boolean simulate);

    /**
     * @return stored steam or energy
     */
    double getUniversalEnergy();

    /**
     * @return The maximum amount of universal energy this machine can store, either EU or Steam converted to EU
     */
    double getUniversalEnergyCapacity();

    double getInputVoltage();

    double getStoredEU();

    double getEUCapacity();

    int getAverageEUInput();

    int getAverageEUOutput();

    double getStoredSteam();

    double getSteamCapacity();

    /**
     * Orders a machine to update it's cover behavior (such as cable connections)
     */
    void markForCoverBehaviorUpdate();

    void disableWorking();

    void enableWorking();

    boolean isAllowedToWork();

    void disableInput();

    void enableInput();

    boolean isInputEnabled();

    void disableOutput();

    void enableOutput();

    boolean isOutputEnabled();
}
