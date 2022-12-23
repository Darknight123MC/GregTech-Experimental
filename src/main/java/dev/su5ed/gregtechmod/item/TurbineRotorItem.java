package dev.su5ed.gregtechmod.item;

import dev.su5ed.gregtechmod.api.item.TurbineRotor;

// TODO TurbineRotor capability
public class TurbineRotorItem extends ResourceItem implements TurbineRotor {
    private final int efficiency;
    private final int efficiencyMultiplier;

    public TurbineRotorItem(ExtendedItemProperties<?> properties, int efficiency, int efficiencyMultiplier) {
        super(properties);
        
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

    @Override
    public int getDamageToComponent() {
        return 1;
    }
}
