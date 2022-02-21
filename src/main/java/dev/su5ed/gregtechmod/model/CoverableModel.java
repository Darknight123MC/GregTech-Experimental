package dev.su5ed.gregtechmod.model;

import dev.su5ed.gregtechmod.api.cover.ICover;
import dev.su5ed.gregtechmod.blockentity.component.CoverHandler;
import dev.su5ed.gregtechmod.util.GtUtil;
import dev.su5ed.gregtechmod.util.VerticalRotation;
import ic2.core.util.Util;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraftforge.client.model.data.IModelData;
import one.util.streamex.EntryStream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class CoverableModel extends BaseModel {
    //Block face UVs in DUNSWE order
    public static final float[][] BLOCK_FACE_UVS = new float[][] {
        { 0, 16, 16, 0 },
        { 0, 0, 16, 16 },
        { 0, 0, 16, 16 },
        { 0, 0, 16, 16 },
        { 0, 0, 16, 16 },
        { 0, 0, 16, 16 }
    };

    private final Map<Direction, Material> textures;
    private final Map<Material, TextureAtlasSprite> sprites;
    private final ResourceLocation modelLocation;

    public CoverableModel(TextureAtlasSprite particle, Map<Direction, Material> textures, Map<Material, TextureAtlasSprite> sprites, ItemOverrides overrides, ItemTransforms transforms, ResourceLocation modelLocation) {
        super(particle, overrides, transforms);

        this.textures = textures;
        this.sprites = sprites;
        this.modelLocation = modelLocation;
    }

    @Nonnull
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData extraData) {
        if (side != null) {
            Map<Direction, Material> covers = getCoverData(extraData);
            Direction face = getValueOrDefault(state, BlockStateProperties.FACING, Direction.NORTH);
            VerticalRotation verticalRotation = getValueOrDefault(state, VerticalRotation.ROTATION_PROPERTY, VerticalRotation.MIRROR_BACK);

            Direction rotatedSide = rotateSide(verticalRotation, face, side, covers.keySet());
            TextureAtlasSprite sprite = getSpriteFromDirection(face, side, rotatedSide, state, covers);

            Material material = this.textures.get(side);
            BlockFaceUV faceUV = new BlockFaceUV(BLOCK_FACE_UVS[side.get3DDataValue()], getTextureRotation(side, face));
            BlockElementFace elementFace = new BlockElementFace(side, 0, material.texture().toString(), faceUV);
            return bakeSingleQuad(elementFace, sprite, side, this.modelLocation);
        }
        return List.of();
    }
    
    private static <T extends Comparable<T>> T getValueOrDefault(BlockState state, Property<T> property, T fallback) {
        return state == null ? fallback : state.getValue(property);
    }
    
    private Map<Direction, Material> getCoverData(IModelData data) {
        Map<Direction, ICover> covers = data.getData(CoverHandler.COVER_HANDLER_PROPERTY);
        if (covers != null) {
            return EntryStream.of(covers)
                .mapValues(ICover::getIcon)
                .mapValues(location -> new Material(InventoryMenu.BLOCK_ATLAS, location))
                .toImmutableMap();
        }
        return Map.of();
    }

    private static Direction rotateSide(VerticalRotation behavior, Direction face, Direction side, Collection<Direction> covers) {
        if (!covers.contains(side) && face != Direction.NORTH) {
            if (face == side) return Direction.NORTH;
            else if (Util.verticalFacings.contains(face)) {
                return behavior.rotation.apply(face, side);
            }
            else if (Util.horizontalFacings.contains(side)) {
                if (face == Direction.SOUTH) return side.getOpposite();
                else return face.getAxisDirection() == Direction.AxisDirection.POSITIVE ? side.getCounterClockWise() : side.getClockWise();
            }
        }

        return side;
    }

    private TextureAtlasSprite getSpriteFromDirection(Direction face, Direction side, Direction rotatedSide, BlockState state, Map<Direction, Material> covers) {
        if (covers.containsKey(side)) return covers.get(rotatedSide).sprite();

        return getSprite(face, side, rotatedSide, state);
    }

    protected TextureAtlasSprite getSprite(Direction face, Direction side, Direction rotatedSide, BlockState state) {
        return this.sprites.get(this.textures.get(rotatedSide));
    }

    private static int getTextureRotation(Direction side, Direction face) {
        if (GtUtil.isVerticalFacing(side)) {
            if (face == Direction.NORTH) return 180;
            else if (face == Direction.WEST) return side == Direction.UP ? 270 : 90;
            else if (face == Direction.EAST) return side == Direction.UP ? 90 : 270;
        }
        return 0;
    }
}
