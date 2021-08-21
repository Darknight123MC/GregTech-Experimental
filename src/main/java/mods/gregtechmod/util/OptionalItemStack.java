package mods.gregtechmod.util;

import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class OptionalItemStack {
    public static final OptionalItemStack EMPTY = new OptionalItemStack(ItemStack.EMPTY);
    
    @Nonnull
    private final ItemStack value;
    
    private OptionalItemStack(ItemStack stack) {
        this.value = stack;
    }

    public static OptionalItemStack of(ItemStack value) {
        return value.isEmpty() ? EMPTY : new OptionalItemStack(value.copy());
    }

    public static OptionalItemStack when(OptionalItemStack... optionals) {
        return Arrays.stream(optionals)
                .filter(OptionalItemStack::isPresent)
                .findFirst()
                .orElse(EMPTY);
    }

    @SafeVarargs
    public static OptionalItemStack either(Supplier<OptionalItemStack>... suppliers) {
        return Arrays.stream(suppliers)
                .map(Supplier::get)
                .filter(OptionalItemStack::isPresent)
                .findFirst()
                .orElse(EMPTY);
    }
    
    public ItemStack get() {
        if (!isPresent()) throw new NoSuchElementException("Value not present");
        return this.value.copy();
    }
    
    public boolean isPresent() {
        return !this.value.isEmpty();
    }
    
    public OptionalItemStack flatMap(Function<ItemStack, OptionalItemStack> mapper) {
        if (isPresent()) return mapper.apply(this.value);
        else return EMPTY;
    }
    
    public boolean ifPresent(Consumer<ItemStack> consumer) {
        if (isPresent()) {
            consumer.accept(this.value);
            return true;
        }
        return false;
    }
    
    public boolean itemEquals(ItemStack stack) {
        return this.isPresent() && this.value.isItemEqual(stack);
    }
}