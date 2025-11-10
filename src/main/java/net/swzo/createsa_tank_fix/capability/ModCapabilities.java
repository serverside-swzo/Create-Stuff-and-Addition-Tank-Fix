package net.swzo.createsa_tank_fix.capability;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.swzo.createsa_tank_fix.CreateSATankFix;
import net.swzo.createsa_tank_fix.config.CSACapacityConfig;

public class ModCapabilities {
    private static void registerTank(RegisterCapabilitiesEvent event, String id, int capacity, Fluid validFluid) {

        ResourceLocation itemRL = ResourceLocation.parse("create_sa:"+ id);

        if (BuiltInRegistries.ITEM.containsKey(itemRL)) {
            Item tankItem = BuiltInRegistries.ITEM.get(itemRL);

            event.registerItem(
                    Capabilities.FluidHandler.ITEM,
                    (stack, context) -> new TankCapability(stack, context, capacity, validFluid),
                    tankItem
            );
            CreateSATankFix.LOGGER.info("[CreateSATankFix] Successfully registered hacked tank capability for: " + itemRL);
        } else {

            CreateSATankFix.LOGGER.warn("[CreateSATankFix] Could not find item '" + itemRL + "'! Capability will not be registered.");
        }
    }

    public static void register(RegisterCapabilitiesEvent event) {
        registerTank(event, "small_filling_tank", CSACapacityConfig.SMALL_CAP, Fluids.WATER);
        registerTank(event, "medium_filling_tank", CSACapacityConfig.MEDIUM_CAP, Fluids.WATER);
        registerTank(event, "large_filling_tank", CSACapacityConfig.LARGE_CAP, Fluids.WATER);

        registerTank(event, "small_fueling_tank", CSACapacityConfig.SMALL_CAP, Fluids.LAVA);
        registerTank(event, "medium_fueling_tank", CSACapacityConfig.MEDIUM_CAP, Fluids.LAVA);
        registerTank(event, "large_fueling_tank", CSACapacityConfig.LARGE_CAP, Fluids.LAVA);
    }
}