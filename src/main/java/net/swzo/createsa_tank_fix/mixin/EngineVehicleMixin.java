package net.swzo.createsa_tank_fix.mixin;

import immersive_aircraft.entity.EngineVehicle;
import immersive_aircraft.entity.inventory.VehicleInventoryDescription;
import immersive_aircraft.entity.inventory.slots.SlotDescription;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.swzo.createsa_tank_fix.capability.TankCapability;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Restriction(require = { @Condition("immersive_aircraft") })
@Mixin(EngineVehicle.class)
public abstract class EngineVehicleMixin {
    @Final @Shadow(remap = false) private int[] fuel;
    @Unique private static final int TICKS_PER_POINT = 200;
    @Unique private static final int MAX_POINTS_PER_TRANSFER = 100;

    @Inject(method = "refuel(I)V", at = @At("HEAD"), cancellable = true, remap = false)
    private void createsa_tank_fix_refuel(int i, CallbackInfo ci) {
        EngineVehicle vehicle = (EngineVehicle) (Object) this;
        List<SlotDescription> slots = vehicle.getInventoryDescription().getSlots(VehicleInventoryDescription.BOILER);
        if (i < 0 || i >= slots.size()) return;

        int slotIndex = slots.get(i).index();
        ItemStack stack = vehicle.getInventory().getItem(slotIndex);

        if (stack.isEmpty()) return;
        if (createsa_tank_fix_isFuelingTank(stack)) {
            ci.cancel();

            if (this.fuel[i] >= 1000) return;

            CustomData customData = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
            CompoundTag tag = customData.copyTag();
            double currentStock = tag.getDouble(TankCapability.TAG_STOCK);

            if (currentStock < 1) return;
            int currentAircraftFuel = this.fuel[i];
            int maxAircraftFuel = 1000;
            int spaceInAircraft = maxAircraftFuel - currentAircraftFuel;

            int pointsAvailable = (int) currentStock;
            int fuelPerPoint = TICKS_PER_POINT;

            int pointsNeeded = (spaceInAircraft + fuelPerPoint - 1) / fuelPerPoint;
            int pointsToConsume = Math.min(pointsAvailable, MAX_POINTS_PER_TRANSFER);
            pointsToConsume = Math.min(pointsToConsume, pointsNeeded);

            if (pointsToConsume > 0) {
                this.fuel[i] += pointsToConsume * fuelPerPoint;
                double newStock = currentStock - pointsToConsume;
                tag.putDouble(TankCapability.TAG_STOCK, newStock);
                stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
                vehicle.getInventory().setItem(slotIndex, stack);
            }
        }
    }

    @Unique
    private boolean createsa_tank_fix_isFuelingTank(ItemStack stack) {
        if (stack.isEmpty()) return false;
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(stack.getItem());
        return id != null && id.getNamespace().equals("create_sa") && id.getPath().contains("fueling_tank");
    }
}