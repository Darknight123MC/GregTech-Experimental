package dev.su5ed.gregtechmod.item;

import dev.su5ed.gregtechmod.util.TurbineRotor;
import net.minecraft.network.chat.MutableComponent;

public class TurbineRotorItem extends ResourceItem implements TurbineRotor {
    private final int efficiency;
    private final int efficiencyMultiplier;

    public TurbineRotorItem(Properties pProperties, MutableComponent description, int efficiency, int efficiencyMultiplier) {
        super(pProperties, description);
        
        this.efficiency = efficiency;
        this.efficiencyMultiplier = efficiencyMultiplier;
    }

    @Override
    public int getEfficiency() {
        return this.efficiency;
    }

    @Override
    public int getEfficiencyMultiplier() {
        return this.efficiencyMultiplier;
    }
}
