package dev.su5ed.gregtechmod.network;

import com.mojang.serialization.Codec;
import cpw.mods.modlauncher.api.LamdbaExceptionUtils;
import dev.su5ed.gregtechmod.api.util.TriConsumer;
import dev.su5ed.gregtechmod.api.util.TriFunction;
import dev.su5ed.gregtechmod.util.Try;
import io.netty.buffer.Unpooled;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.forgespi.language.ModFileScanData;
import one.util.streamex.EntryStream;
import one.util.streamex.StreamEx;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Type;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

public final class NetworkHandler {
    private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();
    private static final Map<Class<?>, SerializationHandler<Object, Object>> SERIALIZERS = new HashMap<>();
    private static final Map<Class<?>, Map<String, SerializationHandler<Object, Object>>> CUSTOM_HANDLERS = new HashMap<>();
    private static final Map<Class<?>, Map<String, FieldHandle>> HANDLES = new HashMap<>();

    static {
        registerSerializer(byte.class, (buf, val) -> buf.writeByte(val), FriendlyByteBuf::readByte);
        registerSerializer(short.class, (buf, val) -> buf.writeShort(val), FriendlyByteBuf::readShort);
        registerSerializer(int.class, FriendlyByteBuf::writeInt, FriendlyByteBuf::readInt);
        registerSerializer(long.class, FriendlyByteBuf::writeLong, FriendlyByteBuf::readLong);
        registerSerializer(float.class, FriendlyByteBuf::writeFloat, FriendlyByteBuf::readFloat);
        registerSerializer(double.class, FriendlyByteBuf::writeDouble, FriendlyByteBuf::readDouble);
        registerSerializer(boolean.class, FriendlyByteBuf::writeBoolean, FriendlyByteBuf::readBoolean);
        registerSerializer(byte[].class, FriendlyByteBuf::writeByteArray, FriendlyByteBuf::readByteArray);
        registerSerializer(String.class, FriendlyByteBuf::writeUtf, FriendlyByteBuf::readUtf);
        registerSerializer(ItemStack.class, FriendlyByteBuf::writeItem, FriendlyByteBuf::readItem);
        registerSerializer(BlockPos.class, FriendlyByteBuf::writeBlockPos, FriendlyByteBuf::readBlockPos);
        //noinspection unchecked
        registerTypeSerializer(Enum.class, FriendlyByteBuf::writeEnum, FriendlyByteBuf::readEnum);
    }

    public static void init() {
        Map<Class<?>, List<String>> data = gatherAnnotationData();
        EntryStream.of(data)
            .removeKeys(HANDLES::containsKey)
            .mapToValue((clazz, list) -> StreamEx.of(list)
                .mapToEntry(Try.<String, FieldHandle>of(name -> {
                        Field field = clazz.getDeclaredField(name);
                        VarHandle varHandle = MethodHandles.privateLookupIn(clazz, LOOKUP).unreflectVarHandle(field);
                        return new FieldHandle(name, field.getType(), varHandle);
                    })
                    .catching(str -> "Unable to create handle for field " + clazz.getName() + "#" + str))
                .toMap())
            .forKeyValue(HANDLES::put);
    }

    public static FriendlyByteBuf serializeClass(Object instance) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        serializeClass(buf, instance);
        return buf;
    }
    
    public static void serializeClass(FriendlyByteBuf buf, Object instance) {
        withParents(instance.getClass())
            .flatMapToEntry(clazz -> HANDLES.getOrDefault(clazz, Map.of()))
            .values()
            .forEach(field -> serializeField(buf, instance, field));
    }
    
    public static FriendlyByteBuf serializeField(Object instance, String field) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        serializeField(buf, instance, field);
        return buf;
    }

    public static void serializeField(FriendlyByteBuf buf, Object instance, String field) {
        FieldHandle handle = withParents(instance.getClass())
            .mapPartial(clazz -> findFieldHandle(clazz, field))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Handle for field " + field + " not found"));
        serializeField(buf, instance, handle);
    }

    public static void serializeField(FriendlyByteBuf buf, Object instance, FieldHandle field) {
        SerializationHandler<Object, Object> handler = getHandler(instance.getClass(), field.name)
            .orElseGet(() -> getSerializer(field.type));
        
        if (handler != null) {
            Object value = field.getValue(instance).orElseThrow();
            buf.writeUtf(field.name);
            handler.serializer.accept(instance, buf, value);   
        } else if (HANDLES.containsKey(field.type)) {
            buf.writeUtf(field.name);
            serializeClass(buf, field.getValue(instance).orElseThrow());
        }
        else throw new RuntimeException("Could not serialize field " + field.name);
    }

    public static void deserializeClass(FriendlyByteBuf buf, Object instance) {
        Map<String, FieldHandle> handles = withParents(instance.getClass())
            .flatMapToEntry(clazz -> HANDLES.getOrDefault(clazz, Map.of()))
            .toMap();
        
        while (buf.isReadable()) {
            String field = buf.readUtf();
            FieldHandle handle = handles.get(field);
            if (handle != null) deserializeField(buf, instance, handle);
        }
    }

    public static void deserializeField(FriendlyByteBuf buf, Object instance, FieldHandle field) {
        SerializationHandler<Object, Object> handler = getHandler(instance.getClass(), field.name)
            .orElseGet(() -> getSerializer(field.type));
        
        if (handler != null) {
            Object value = handler.deserializer.apply(instance, buf, field.type);
            field.setValue(instance, value);
            if (instance instanceof FieldUpdateListener listener) {
                listener.onFieldUpdate(field.name);
            }
        }
        else if (HANDLES.containsKey(field.type)) {
            deserializeClass(buf, field.getValue(instance).orElseThrow());
        }
        else throw new RuntimeException("Could not deserialize field " + field.name);
    }

    private static Map<Class<?>, List<String>> gatherAnnotationData() {
        Type annotationType = Type.getType(Networked.class);
        ModFileScanData scan = ModLoadingContext.get().getActiveContainer().getModInfo().getOwningFile().getFile().getScanResult();
        return StreamEx.of(scan.getAnnotations())
            .filter(data -> data.annotationType().equals(annotationType))
            .<Class<?>, String>mapToEntry(LamdbaExceptionUtils.rethrowFunction(data -> Class.forName(data.clazz().getClassName())), ModFileScanData.AnnotationData::memberName)
            .collapseKeys()
            .toMap();
    }

    @SuppressWarnings("unchecked")
    public static <T, U> void registerHandler(Class<? super T> clazz, String field, Function<T, Codec<? extends U>> factory) {
        SerializationHandler<T, U> handler = new SerializationHandler<>(
            (instance, buf, obj) -> buf.writeWithCodec((Codec<U>) factory.apply(instance), obj),
            (instance, buf, type) -> buf.readWithCodec(factory.apply(instance))
        );
        Map<String, SerializationHandler<Object, Object>> handlers = CUSTOM_HANDLERS.computeIfAbsent(clazz, cls -> new HashMap<>());
        handlers.put(field, (SerializationHandler<Object, Object>) handler);
    }

    public static Optional<SerializationHandler<Object, Object>> getHandler(Class<?> clazz, String field) {
        return withParents(clazz)
            .mapPartial(cls -> Optional.ofNullable(CUSTOM_HANDLERS.get(cls))
                .map(map -> map.get(field)))
            .findFirst();
    }

    private static <U> void registerSerializer(Class<U> clazz, BiConsumer<FriendlyByteBuf, U> serializer, Function<FriendlyByteBuf, U> deserializer) {
        registerTypeSerializer(clazz, serializer, (buf, type) -> deserializer.apply(buf));
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static <U> void registerTypeSerializer(Class<U> clazz, BiConsumer<FriendlyByteBuf, U> serializer, BiFunction<FriendlyByteBuf, Class, U> deserializer) {
        SerializationHandler<?, U> handler = new SerializationHandler<>(
            (instance, buf, obj) -> serializer.accept(buf, obj),
            (instance, buf, type) -> deserializer.apply(buf, type)
        );
        SERIALIZERS.put(clazz, (SerializationHandler<Object, Object>) handler);
    }

    @Nullable
    private static SerializationHandler<Object, Object> getSerializer(Class<?> clazz) {
        return EntryStream.of(SERIALIZERS)
            .filterKeys(cls -> cls.isAssignableFrom(clazz))
            .values()
            .findFirst()
            .orElse(null);
    }

    private static Optional<FieldHandle> findFieldHandle(Class<?> clazz, String name) {
        return Optional.ofNullable(HANDLES.get(clazz))
            .map(handles -> handles.get(name));
    }

    public static StreamEx<Class<?>> withParents(Class<?> clazz) {
        return StreamEx.<Class<?>>iterate(clazz, Objects::nonNull, Class::getSuperclass)
            .without(Object.class);
    }

    private NetworkHandler() {}

    private record SerializationHandler<T, U>(TriConsumer<T, FriendlyByteBuf, U> serializer, TriFunction<T, FriendlyByteBuf, Class<?>, U> deserializer) {}
}
