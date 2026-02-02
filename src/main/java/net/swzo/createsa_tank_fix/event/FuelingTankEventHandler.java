package net.swzo.createsa_tank_fix.event;

import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.furnace.FurnaceFuelBurnTimeEvent;
import net.swzo.createsa_tank_fix.capability.TankCapability;

public class FuelingTankEventHandler {
    private static final int TICKS_PER_POINT = 200;
    private static final int MAX_POINTS_PER_BURN = 100;

    @SubscribeEvent
    public static void onFurnaceFuelBurnTime(FurnaceFuelBurnTimeEvent event) {
        ItemStack stack = event.getItemStack();
        if (stack.isEmpty()) return;
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(stack.getItem());
        if (id != null && id.getNamespace().equals("create_sa") && id.getPath().contains("fueling_tank")) {
            CustomData customData = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
            if (customData.isEmpty()) return;
            double currentStock = customData.copyTag().getDouble(TankCapability.TAG_STOCK);

            if (currentStock >= 1) {
                int pointsToConsume = Math.min((int) currentStock, MAX_POINTS_PER_BURN);
                event.setBurnTime(pointsToConsume * TICKS_PER_POINT);
            }
        }
    }
}