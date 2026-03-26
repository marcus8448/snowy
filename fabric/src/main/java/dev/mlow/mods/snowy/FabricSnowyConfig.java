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

import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class FabricSnowyConfig implements SnowyConfig {
    private boolean enableNonOverworldBiomes = false;
    private List<String> forceDisabledBiomes = Collections.emptyList();
    private List<String> forceEnabledBiomes = Collections.emptyList();
    private boolean enableTemperatureNoise = false;
    private boolean enableConstantSnow = true;

    @Override
    public boolean enableNonOverworldBiomes() {
        return this.enableNonOverworldBiomes;
    }

    @Override
    public List<String> forceDisabledBiomes() {
        return this.forceDisabledBiomes;
    }

    @Override
    public List<String> forceEnabledBiomes() {
        return this.forceEnabledBiomes;
    }

    @Override
    public boolean enableTemperatureNoise() {
        return this.enableTemperatureNoise;
    }

    @Override
    public boolean enableConstantSnow() {
        return this.enableConstantSnow;
    }

    @Override
    public void enableNonOverworldBiomes(boolean value) {
        this.enableNonOverworldBiomes = value;
    }

    @Override
    public void forceDisabledBiomes(List<String> list) {
        this.forceDisabledBiomes = list;
    }

    @Override
    public void forceEnabledBiomes(List<String> list) {
        this.forceEnabledBiomes = list;
    }

    @Override
    public void enableTemperatureNoise(boolean value) {
        this.enableTemperatureNoise = value;
    }

    @Override
    public void enableConstantSnow(boolean value) {
        this.enableConstantSnow = value;
    }

    @Override
    public void save() {
        if (!SnowyFabric.CONFIG_FILE.isFile()) {
            SnowyFabric.CONFIG_FILE.delete();
        }
        try (FileWriter writer = new FileWriter(SnowyFabric.CONFIG_FILE)) {
            SnowyFabric.GSON.toJson(this, writer);
        } catch (IOException e) {
            Constant.LOGGER.error("Failed to save configuration file!", e);
        }
    }
}
