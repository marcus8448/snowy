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

package dev.mlow.mods.snowy;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.config.IConfigSpec;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

public class ForgeSnowyConfig implements SnowyConfig {
    public final Common common;
    public final IConfigSpec commonSpec;

    @SubscribeEvent
    public void onLoad(ModConfigEvent.Loading configEvent) {
        Constant.LOGGER.debug("Successfully loaded Snowy's config file!");
    }

    public ForgeSnowyConfig() {
        Pair<Common, ModConfigSpec> configure = new ModConfigSpec.Builder().configure(Common::new);
        this.common = configure.getLeft();
        this.commonSpec = configure.getRight();
    }

    @Override
    public boolean enableNonOverworldBiomes() {
        return this.common.enableNonOverworldBiomes.get();
    }

    @Override
    public List<String> forceDisabledBiomes() {
        return (List<String>) this.common.forceDisabledBiomes.get();
    }

    @Override
    public List<String> forceEnabledBiomes() {
        return (List<String>) this.common.forceEnabledBiomes.get();
    }

    @Override
    public boolean enableTemperatureNoise() {
        return this.common.enableTemperatureNoise.get();
    }

    @Override
    public boolean enableConstantSnow() {
        return this.common.enableConstantSnow.get();
    }

    @Override
    public void enableNonOverworldBiomes(boolean value) {
        this.common.enableNonOverworldBiomes.set(value);
    }

    @Override
    public void forceDisabledBiomes(List<String> list) {
        this.common.forceDisabledBiomes.set(list);
    }

    @Override
    public void forceEnabledBiomes(List<String> list) {
        this.common.forceEnabledBiomes.set(list);
    }

    @Override
    public void enableTemperatureNoise(boolean value) {
        this.common.enableTemperatureNoise.set(value);
    }

    @Override
    public void enableConstantSnow(boolean value) {
        this.common.enableConstantSnow.set(value);
    }

    @Override
    public void save() {
        //no-op on forge
    }

    public static class Common {
        final ModConfigSpec.BooleanValue enableNonOverworldBiomes;
        final ModConfigSpec.ConfigValue<List<? extends String>> forceDisabledBiomes;
        final ModConfigSpec.ConfigValue<List<? extends String>> forceEnabledBiomes;
        final ModConfigSpec.BooleanValue enableTemperatureNoise;
        final ModConfigSpec.BooleanValue enableConstantSnow;

        Common(@Nonnull ModConfigSpec.Builder builder) {
            builder.comment("Snowy config").push("general");
            this.enableNonOverworldBiomes = builder.comment("Enable non-overworld biomes").translation("option.snowy.enable_non_overworld_biomes").worldRestart().define("enable_non_overworld_biomes", false);
            this.forceDisabledBiomes = builder.comment("Force disabled biomes").translation("option.snowy.force_disabled_biomes").worldRestart().defineListAllowEmpty(Collections.singletonList("force_disabled_biomes"), Collections::emptyList, o -> true);
            this.forceEnabledBiomes = builder.comment("Force enabled biomes").translation("option.snowy.force_enabled_biomes").worldRestart().defineListAllowEmpty(Collections.singletonList("force_enabled_biomes"), Collections::emptyList, o -> true);
            this.enableTemperatureNoise = builder.comment("Enable temperature noise").translation("option.snowy.enable_temperature_noise").worldRestart().define("enable_temperature_noise", false);
            this.enableConstantSnow = builder.comment("Enable constant snow").translation("option.snowy.enable_constant_snow").define("enable_constant_snow", true);
            builder.pop();
        }
    }
}
