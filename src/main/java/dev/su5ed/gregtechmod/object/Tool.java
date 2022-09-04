package dev.su5ed.gregtechmod.object;

import dev.su5ed.gregtechmod.GregTechTags;
import dev.su5ed.gregtechmod.item.AdvancedDrillItem;
import dev.su5ed.gregtechmod.item.AdvancedSawItem;
import dev.su5ed.gregtechmod.item.AdvancedWrenchItem;
import dev.su5ed.gregtechmod.item.CrowbarItem;
import dev.su5ed.gregtechmod.item.DestructorPackItem;
import dev.su5ed.gregtechmod.item.RockCutterItem;
import dev.su5ed.gregtechmod.item.RubberHammerItem;
import dev.su5ed.gregtechmod.item.ScrewdriverItem;
import dev.su5ed.gregtechmod.item.SolderingToolItem;
import dev.su5ed.gregtechmod.item.TeslaStaffItem;
import dev.su5ed.gregtechmod.util.TaggedItemProvider;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.util.Lazy;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public enum Tool implements TaggedItemProvider {
    CROWBAR(CrowbarItem::new, GregTechTags.CROWBAR),
    SCREWDRIVER(ScrewdriverItem::new, GregTechTags.SCREWDRIVER),
    ADVANCED_DRILL(AdvancedDrillItem::new, GregTechTags.LARGE_DRILL),
    ADVANCED_SAW(AdvancedSawItem::new),
    ROCK_CUTTER(RockCutterItem::new),
    SOLDERING_TOOL(SolderingToolItem::new, GregTechTags.SOLDERING_IRON),
    TESLA_STAFF(TeslaStaffItem::new),
    RUBBER_HAMMER(RubberHammerItem::new, GregTechTags.SOFT_HAMMER),
    ADVANCED_WRENCH(AdvancedWrenchItem::new),
    DESTRUCTORPACK(DestructorPackItem::new);
    // TODO
    // Item Scanner
    // Debug Item Scanner
    
    private final Lazy<Item> instance;
    private final TagKey<Item> tag;
    
    Tool(Supplier<Item> supplier) {
        this(supplier, null);
    }

    Tool(Supplier<Item> supplier, TagKey<Item> tag) {
        this.instance = Lazy.of(() -> supplier.get().setRegistryName(getName()));
        this.tag = tag;
    }

    @Override
    public Item getItem() {
        return this.instance.get();
    }

    @Nullable
    @Override
    public TagKey<Item> getTag() {
        return this.tag;
    }
}
