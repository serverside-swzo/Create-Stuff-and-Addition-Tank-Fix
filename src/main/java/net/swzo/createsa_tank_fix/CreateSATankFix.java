package net.swzo.createsa_tank_fix;

import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.swzo.createsa_tank_fix.capability.ModCapabilities;
import net.swzo.createsa_tank_fix.config.CSACapacityConfig;
import net.swzo.createsa_tank_fix.datagen.CuriosDataGenerator;
import org.slf4j.Logger;

@Mod(CreateSATankFix.MODID)
public class CreateSATankFix {
    public static final String MODID = "createsa_tank_fix";
    public static final Logger LOGGER = LogUtils.getLogger();

    public CreateSATankFix(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::setup);
        modEventBus.addListener(ModCapabilities::register);
        modEventBus.addListener(this::gatherData);
    }

    private void setup(final FMLCommonSetupEvent event) {
        LOGGER.info("Starting Create SA Tank Fix setup...");
        CSACapacityConfig.init();
    }

    @SubscribeEvent
    public void gatherData(GatherDataEvent event) {
        event.getGenerator().addProvider(
                event.includeServer(),
                new CuriosDataGenerator(
                        MODID,
                        event.getGenerator().getPackOutput(),
                        event.getExistingFileHelper(),
                        event.getLookupProvider()
                )
        );
    }
}