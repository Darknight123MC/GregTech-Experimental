package dev.su5ed.gregtechmod.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import dev.su5ed.gregtechmod.util.ModelUtil;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraftforge.client.model.IModelLoader;

import java.util.Map;

public class OreModelLoader implements IModelLoader<OreModelGeometry> {

    @Override
    public OreModelGeometry read(JsonDeserializationContext deserializationContext, JsonObject modelContents) {
        JsonObject json = modelContents.getAsJsonObject("textures");
        ResourceLocation particle = new ResourceLocation(json.get("particle").getAsString());
        Map<Direction, ResourceLocation> textures = ModelUtil.generateTextureMap(json);
        Map<Direction, ResourceLocation> texturesNether = generateTextureMap(modelContents, "textures_nether");
        Map<Direction, ResourceLocation> texturesEnd = generateTextureMap(modelContents, "textures_end");
        return new OreModelGeometry(particle, textures, texturesNether, texturesEnd);
    }

    public Map<Direction, ResourceLocation> generateTextureMap(JsonObject json, String elementName) {
        return ModelUtil.generateTextureMap(json.getAsJsonObject(elementName));
    }

    @Override
    public void onResourceManagerReload(ResourceManager pResourceManager) {}
}
