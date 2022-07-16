package dev.su5ed.gregtechmod.datagen;

import dev.su5ed.gregtechmod.api.util.Reference;
import dev.su5ed.gregtechmod.object.ModBlock;
import dev.su5ed.gregtechmod.object.Ore;
import dev.su5ed.gregtechmod.util.HarvestLevel;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import one.util.streamex.StreamEx;

import javax.annotation.Nullable;
import java.util.Map;

class BlockTagsGen extends BlockTagsProvider {

    public BlockTagsGen(DataGenerator pGenerator, @Nullable ExistingFileHelper existingFileHelper) {
        super(pGenerator, Reference.MODID, existingFileHelper);
    }

    @Override
    protected void addTags() {
        TagAppender<Block> pickaxe = tag(BlockTags.MINEABLE_WITH_PICKAXE);
        TagAppender<Block> ores = tag(Tags.Blocks.ORES);
        Map<HarvestLevel, TagAppender<Block>> harvestLevels = StreamEx.of(HarvestLevel.values())
            .mapToEntry(level -> tag(level.getTag()))
            .toMap();

        StreamEx.of(ModBlock.values())
            .map(ModBlock::getBlock)
            .forEach(block -> {
                pickaxe.add(block);
                harvestLevels.get(HarvestLevel.IRON).add(block);
            });

        StreamEx.of(Ore.values())
            .mapToEntry(Ore::getBlock, Ore::getHarvestLevel)
            .forKeyValue((block, level) -> {
                pickaxe.add(block);
                ores.add(block);
                harvestLevels.get(level).add(block);
            });
    }

    @Override
    public String getName() {
        return Reference.NAME + " Block Tags";
    }
}
