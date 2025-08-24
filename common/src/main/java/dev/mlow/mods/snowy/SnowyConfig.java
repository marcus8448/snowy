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

import java.util.List;

public interface SnowyConfig {
    boolean enableNonOverworldBiomes();

    List<String> forceDisabledBiomes();

    List<String> forceEnabledBiomes();

    boolean enableTemperatureNoise();

    boolean enableConstantSnow();

    void enableNonOverworldBiomes(boolean value);

    void forceDisabledBiomes(List<String> list);

    void forceEnabledBiomes(List<String> list);

    void enableTemperatureNoise(boolean value);

    void enableConstantSnow(boolean value);

    void save();
}
