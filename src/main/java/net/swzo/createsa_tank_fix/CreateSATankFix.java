package net.swzo.createsa_tank_fix;

import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.swzo.createsa_tank_fix.capability.ModCapabilities;
import net.swzo.createsa_tank_fix.config.CSACapacityConfig;
import org.slf4j.Logger;

@Mod(CreateSATankFix.MODID)
public class CreateSATankFix {
    public static final String MODID = "createsa_tank_fix";
    private static final Logger LOGGER = LogUtils.getLogger();

    public CreateSATankFix(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::setup);
        modEventBus.addListener(ModCapabilities::register);
    }

    private void setup(final FMLCommonSetupEvent event) {
        LOGGER.info("Starting Create SA Tank Fix setup...");
        CSACapacityConfig.init();
    }
}