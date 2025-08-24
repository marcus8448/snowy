/*
 * Snowy
 * Copyright (C) 2019-2025 marcus8448
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see https://www.gnu.org/licenses/.
 */

package dev.mlow.mods.snowy;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectionContext;
import net.fabricmc.fabric.api.biome.v1.ModificationPhase;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.data.worldgen.placement.MiscOverworldPlacements;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.GenerationStep;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class SnowyFabric implements ModInitializer {
    public static final File CONFIG_FILE = FabricLoader.getInstance().getConfigDir().resolve("snowy.json").toFile();
    public static final Gson GSON = new GsonBuilder()
            .disableHtmlEscaping()
            .setPrettyPrinting()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create();
    public static final SnowyConfig CONFIG;

    @Override
    public void onInitialize() {
        BiomeModifications.create(ResourceLocation.fromNamespaceAndPath(Constant.MOD_ID, "freeze_surface")).add(ModificationPhase.ADDITIONS, biome -> canModify(biome) && !biome.hasPlacedFeature(MiscOverworldPlacements.FREEZE_TOP_LAYER), context -> {
            context.getGenerationSettings().addFeature(GenerationStep.Decoration.TOP_LAYER_MODIFICATION, MiscOverworldPlacements.FREEZE_TOP_LAYER);
        });

        BiomeModifications.create(ResourceLocation.fromNamespaceAndPath(Constant.MOD_ID, "change_weather")).add(ModificationPhase.POST_PROCESSING, SnowyFabric::canModify, context -> {
            context.getWeather().setTemperature(0.0f);
            context.getWeather().setPrecipitation(true);
            context.getWeather().setDownfall(0.5f);
            context.getWeather().setTemperatureModifier(CONFIG.enableTemperatureNoise() ? Biome.TemperatureModifier.FROZEN : Biome.TemperatureModifier.NONE);
        });

        ServerTickEvents.START_WORLD_TICK.register(level -> {
            if (CONFIG.enableConstantSnow()) {
                if (CONFIG.enableNonOverworldBiomes() || level.dimension().equals(Level.OVERWORLD)) {
                    level.setWeatherParameters(0, 6000, true, false);
                }
            }
        });
    }

    private static boolean canModify(@NotNull BiomeSelectionContext biome) {
        String id = biome.getBiomeKey().location().toString();
        if (CONFIG.forceEnabledBiomes().contains(id)) return true;
        if (CONFIG.forceDisabledBiomes().contains(id)) return false;

        return CONFIG.enableNonOverworldBiomes() || biome.canGenerateIn(LevelStem.OVERWORLD);
    }

    static {
        if (SnowyFabric.CONFIG_FILE.isFile()) {
            try (FileReader reader = new FileReader(SnowyFabric.CONFIG_FILE)) {
                CONFIG = SnowyFabric.GSON.fromJson(reader, FabricSnowyConfig.class);
            } catch (IOException e) {
                throw new RuntimeException("Snowy failed to read its configuration file!", e);
            }
        } else {
            SnowyFabric.CONFIG_FILE.delete();
            CONFIG = new FabricSnowyConfig();
            try (FileWriter writer = new FileWriter(SnowyFabric.CONFIG_FILE)) {
                SnowyFabric.GSON.toJson(CONFIG, writer);
            } catch (IOException e) {
                throw new RuntimeException("Snowy failed to create its configuration file!", e);
            }
        }
    }
}
