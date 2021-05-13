package mods.gregtechmod.api.machine;

import net.minecraft.util.EnumFacing;

public interface IGregTechMachine {
    boolean isActive();

    double getProgress();

    int getMaxProgress();

    void increaseProgress(double amount);

    void setRedstoneOutput(EnumFacing side, byte strength);

    double addEnergy(double amount);

    double useEnergy(double amount, boolean simulate);

    /**
     * @return stored EU, MJ, or steam
     */
    double getUniversalEnergy();

    /**
     * @return The maximum amount of energy this machine can store, either EU, MJ, or Steam converted to EU
     */
    double getUniversalEnergyCapacity();

    double getInputVoltage();

    double getOutputVoltage();

    double getStoredEU();

    double getDefaultEUCapacity();

    double getEUCapacity();

    int getAverageEUInput();

    int getAverageEUOutput();

    double getStoredSteam();

    double getSteamCapacity();

    long getStoredMj();

    long getMjCapacity();

    void setMjCapacity(long capacity);

    void disableWorking();

    void enableWorking();

    boolean isAllowedToWork();

    void disableInput();

    void enableInput();

    boolean isInputEnabled();

    void disableOutput();

    void enableOutput();

    boolean isOutputEnabled();

    /**
     * Instructs the machine to go <b>KABOOM</b
     */
    void markForExplosion();
}