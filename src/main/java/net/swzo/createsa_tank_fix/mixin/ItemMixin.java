package net.swzo.createsa_tank_fix.mixin;

import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.swzo.createsa_tank_fix.capability.TankCapability;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import javax.annotation.Nullable;

@Mixin(Item.class)
public abstract class ItemMixin {
    @Shadow @Nullable public abstract Item getCraftingRemainingItem();
    @Unique private static final int MAX_POINTS_PER_BURN = 100;

    public boolean hasCraftingRemainingItem(ItemStack stack) {
        if (createsa_tank_fix_isFuelingTank(stack)) return true;
        return this.getCraftingRemainingItem() != null;
    }

    public ItemStack getCraftingRemainingItem(ItemStack stack) {
        if (createsa_tank_fix_isFuelingTank(stack)) {
            ItemStack copy = stack.copy();

            CustomData customData = copy.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
            CompoundTag tag = customData.copyTag();

            double currentStock = tag.getDouble(TankCapability.TAG_STOCK);

            if (currentStock > 0) {
                int consumed = Math.min((int) currentStock, MAX_POINTS_PER_BURN);
                double newStock = currentStock - consumed;
                tag.putDouble(TankCapability.TAG_STOCK, newStock);
                copy.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));

                return copy;
            } else return copy;
        }

        Item remainder = this.getCraftingRemainingItem();
        return remainder == null ? ItemStack.EMPTY : new ItemStack(remainder);
    }

    @Unique
    private boolean createsa_tank_fix_isFuelingTank(ItemStack stack) {
        if (stack.isEmpty()) return false;
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(stack.getItem());

        return id != null && id.getNamespace().equals("create_sa") && id.getPath().contains("fueling_tank");
    }
}