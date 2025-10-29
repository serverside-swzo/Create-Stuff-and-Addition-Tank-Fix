package net.swzo.createsa_tank_fix.config;

import com.electronwill.nightconfig.core.file.FileConfig;
import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

import java.nio.file.Files;
import java.nio.file.Path;

public class CSACapacityConfig {

    private static final Logger LOGGER = LogUtils.getLogger();

    public static int SMALL_CAP = 800;
    public static int MEDIUM_CAP = 1600;
    public static int LARGE_CAP = 3200;

    public static void init() {

        Path path = Path.of("config/create-server.toml");
        if (!Files.exists(path)) {
            LOGGER.warn("[CreateSATankFix] Could not find 'config/create-server.toml'. Using default tank capacities (800, 1600, 3200).");
            return;
        }

        try (FileConfig config = FileConfig.of(path)) {
            config.load();
            SMALL_CAP = config.<Double>getOptional("Fuel/Water Capacity.smallTankCapacity")
                    .orElse((double)SMALL_CAP).intValue();
            MEDIUM_CAP = config.<Double>getOptional("Fuel/Water Capacity.mediumTankCapacity")
                    .orElse((double)MEDIUM_CAP).intValue();
            LARGE_CAP = config.<Double>getOptional("Fuel/Water Capacity.largeTankCapacity")
                    .orElse((double)LARGE_CAP).intValue();
            LOGGER.info("[CreateSATankFix] Loaded Create SA tank capacities from config: S={}, M={}, L={}",
                    SMALL_CAP, MEDIUM_CAP, LARGE_CAP);
        } catch (Exception e) {
            LOGGER.error("[CreateSATankFix] Failed to read 'config/create-server.toml'. Using default capacities.", e);
        }
    }
}