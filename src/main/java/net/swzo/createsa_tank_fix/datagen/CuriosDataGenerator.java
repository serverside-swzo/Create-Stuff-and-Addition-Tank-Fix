package net.swzo.createsa_tank_fix.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.swzo.createsa_tank_fix.CreateSATankFix;
import top.theillusivec4.curios.api.CuriosDataProvider;

import java.util.concurrent.CompletableFuture;

public class CuriosDataGenerator extends CuriosDataProvider {

    public CuriosDataGenerator(String modId, PackOutput output, ExistingFileHelper fileHelper, CompletableFuture<HolderLookup.Provider> registries) {
        super(modId, output, fileHelper, registries);
    }

    @Override
    public void generate(HolderLookup.Provider registries, ExistingFileHelper fileHelper) {
        this.createEntities("player")
                .replace(false)
                .addPlayer()
                .addSlots("tank");

        this.createSlot("tank")
                .size(4)
                .icon(ResourceLocation.fromNamespaceAndPath(CreateSATankFix.MODID, "item/empty_tank"));
    }
}