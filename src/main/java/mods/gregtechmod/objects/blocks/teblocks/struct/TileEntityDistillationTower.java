package mods.gregtechmod.objects.blocks.teblocks.struct;

import ic2.core.ContainerBase;
import mods.gregtechmod.api.recipe.GtRecipes;
import mods.gregtechmod.api.recipe.IRecipeCellular;
import mods.gregtechmod.api.recipe.ingredient.IRecipeIngredient;
import mods.gregtechmod.api.recipe.manager.IGtRecipeManagerCellular;
import mods.gregtechmod.gui.GuiDistillationTower;
import mods.gregtechmod.inventory.InvSlotConsumableCell;
import mods.gregtechmod.objects.BlockItems;
import mods.gregtechmod.objects.blocks.teblocks.container.ContainerDistillationTower;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class TileEntityDistillationTower extends TileEntityStructureBase<Object, IRecipeCellular, IRecipeIngredient, ItemStack, IGtRecipeManagerCellular> {
    public InvSlotConsumableCell cellSlot;
    
    public TileEntityDistillationTower() {
        super("distillation_tower", 10000, 4, 2, GtRecipes.distillation);
        this.cellSlot = new InvSlotConsumableCell(this, "cell_slot", 1);
    }
    
    @Override
    protected List<List<String>> getStructurePattern() {
        return Arrays.asList(
                Arrays.asList(
                        "SSS",
                        "SSS",
                        "SSS",
                        " X "
                ),
                Arrays.asList(
                        "DDD",
                        "DAD",
                        "DDD",
                        "   "
                ),
                Arrays.asList(
                        "SSS",
                        "SAS",
                        "SSS",
                        "   "
                ),
                Arrays.asList(
                        "DDD",
                        "DAD",
                        "DDD",
                        "   "
                ),
                Arrays.asList(
                        "SSS",
                        "SSS",
                        "SSS",
                        "   "
                )
        );
    }
    
    @Override
    protected void getStructureElements(Map<Character, Predicate<IBlockState>> map) {
        map.put('S', state -> state.getBlock() == BlockItems.Block.STANDARD_MACHINE_CASING.getInstance());
        map.put('D', state -> state.getBlock() == BlockItems.Block.ADVANCED_MACHINE_CASING.getInstance());
        map.put('A', state -> state.getBlock() == Blocks.AIR);
    }

    @Override
    public void consumeInput(IRecipeCellular recipe, boolean consumeContainers) {
        IRecipeIngredient input = recipe.getInput();
        
        this.inputSlot.consume(input.getCount(), true);
        this.cellSlot.consume(recipe.getCells());
    }

    @Override
    public IRecipeCellular getRecipe() {
        return this.recipeManager.getRecipeFor(this.inputSlot.get(), this.cellSlot.get());
    }

    @Override
    public ContainerBase<?> getGuiContainer(EntityPlayer player) {
        return new ContainerDistillationTower(player, this);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public GuiScreen getGui(EntityPlayer player, boolean isAdmin) {
        return new GuiDistillationTower(new ContainerDistillationTower(player, this));
    }
}