/*
 * Snowy
 * Copyright (C) 2020-2026 marcus8448
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

package dev.mlow.mods.snowy.compat;

import dev.mlow.mods.snowy.SnowyFabric;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.Collections;


public class ClothConfigScreen {
    public static Screen createScreenFactory(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Component.translatable("title.snowy.config"));
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();
        ConfigCategory general = builder.getOrCreateCategory(Component.translatable("category.snowy.general"));

        general.addEntry(entryBuilder.startBooleanToggle(Component.translatable("option.snowy.enable_non_overworld_biomes"), SnowyFabric.CONFIG.enableNonOverworldBiomes())
                .setDefaultValue(false)
                .setTooltip(Component.translatable("option.snowy.enable_non_overworld_biomes.tooltip"))
                .setSaveConsumer(SnowyFabric.CONFIG::enableNonOverworldBiomes)
                .build());
        general.addEntry(entryBuilder.startStrList(Component.translatable("option.snowy.force_disabled_biomes"), SnowyFabric.CONFIG.forceDisabledBiomes())
                .setDefaultValue(Collections.emptyList())
                .setTooltip(Component.translatable("option.snowy.force_disabled_biomes.tooltip"))
                .setSaveConsumer(SnowyFabric.CONFIG::forceDisabledBiomes)
                .build());
        general.addEntry(entryBuilder.startStrList(Component.translatable("option.snowy.force_enabled_biomes"), SnowyFabric.CONFIG.forceEnabledBiomes())
                .setDefaultValue(Collections.emptyList())
                .setTooltip(Component.translatable("option.snowy.force_enabled_biomes.tooltip"))
                .setSaveConsumer(SnowyFabric.CONFIG::forceEnabledBiomes)
                .build());

        general.addEntry(entryBuilder.startBooleanToggle(Component.translatable("option.snowy.enable_temperature_noise"), SnowyFabric.CONFIG.enableTemperatureNoise())
                .setDefaultValue(false)
                .setTooltip(Component.translatable("option.snowy.enable_temperature_noise.tooltip"))
                .setSaveConsumer(SnowyFabric.CONFIG::enableTemperatureNoise)
                .build());
        general.addEntry(entryBuilder.startBooleanToggle(Component.translatable("option.snowy.enable_constant_snow"), SnowyFabric.CONFIG.enableConstantSnow())
                .setDefaultValue(true)
                .setTooltip(Component.translatable("option.snowy.enable_constant_snow.tooltip"))
                .setSaveConsumer(SnowyFabric.CONFIG::enableConstantSnow)
                .build());

        builder.setSavingRunnable(SnowyFabric.CONFIG::save);
        return builder.build();
    }
}
