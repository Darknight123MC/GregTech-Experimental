package dev.su5ed.gregtechmod.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraftforge.client.model.IModelLoader;
import one.util.streamex.StreamEx;

import java.util.Map;

import static dev.su5ed.gregtechmod.api.util.Reference.location;

public class ConnectedModelLoader implements IModelLoader<ConnectedModelGeometry> {
    private final Map<String, ResourceLocation> textures;
    private final ResourceLocation particle;
    
    public ConnectedModelLoader(String name) {
        this.textures = StreamEx.of(ConnectedModel.TEXTURE_PARTS)
            .mapToEntry(part -> name + "_" + part)
            .prepend("", name)
            .mapValues(texture -> location("block", "connected", name, texture))
            .toImmutableMap();
        this.particle = this.textures.get("");
    }

    @Override
    public ConnectedModelGeometry read(JsonDeserializationContext deserializationContext, JsonObject modelContents) {
        return new ConnectedModelGeometry(this.particle, this.textures);
    }

    @Override
    public void onResourceManagerReload(ResourceManager pResourceManager) {}
}