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

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.ModifiableBiomeInfo;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

@Mod(Constant.MOD_ID)
public class SnowyNeoForge {
    private static final ForgeSnowyConfig CONFIG = new ForgeSnowyConfig();
    private static final DeferredRegister<MapCodec<? extends BiomeModifier>> BIOME_MODIFIER_SERIALIZERS = DeferredRegister.create(NeoForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, Constant.MOD_ID);
    private static final DeferredRegister<BiomeModifier> BIOME_MODIFIERS = DeferredRegister.create(NeoForgeRegistries.Keys.BIOME_MODIFIERS, Constant.MOD_ID);
    private static final DeferredHolder<MapCodec<? extends BiomeModifier>, MapCodec<SnowyBiomeModifier>> BIOME_MODIFIER_CODEC = BIOME_MODIFIER_SERIALIZERS.register("freeze_biome", () -> RecordCodecBuilder.mapCodec(instance -> instance.group(
            PlacedFeature.CODEC.fieldOf("feature").forGetter(SnowyBiomeModifier::freezeTop)
    ).apply(instance, SnowyBiomeModifier::new)));

    public SnowyNeoForge(IEventBus modEventBus, Dist dist, ModContainer container) {
        container.registerConfig(ModConfig.Type.COMMON, CONFIG.commonSpec);
        modEventBus.addListener(CONFIG::onLoad);

        NeoForge.EVENT_BUS.addListener(this::serverTick);

        BIOME_MODIFIER_SERIALIZERS.register(modEventBus);
        BIOME_MODIFIERS.register(modEventBus);
    }

    public void serverTick(LevelTickEvent.Pre event) {
        if (event.getLevel() instanceof ServerLevel level) {
            if (CONFIG.enableConstantSnow()) {
                if (CONFIG.enableNonOverworldBiomes() || level.dimension().equals(Level.OVERWORLD)) {
                    level.setWeatherParameters(0, 6000, true, false);
                }
            }
        }
    }

    private static boolean canModify(Holder<Biome> biome) {
        for (String forceEnabledBiome : CONFIG.forceEnabledBiomes()) {
            if (biome.is(ResourceLocation.parse(forceEnabledBiome))) return true;
        }
        for (String forceDisabledBiome : CONFIG.forceDisabledBiomes()) {
            if (biome.is(ResourceLocation.parse(forceDisabledBiome))) return false;
        }

        return CONFIG.enableNonOverworldBiomes() || biome.is(BiomeTags.IS_OVERWORLD);
    }

    private record SnowyBiomeModifier(Holder<PlacedFeature> freezeTop) implements BiomeModifier {
        @Override
        public void modify(Holder<Biome> biome, Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
            if (phase == Phase.ADD) {
                if (canModify(biome)) {
                    for (Holder<PlacedFeature> feature : builder.getGenerationSettings().getFeatures(GenerationStep.Decoration.TOP_LAYER_MODIFICATION)) {
                        if (this.freezeTop.is(feature)) {
                            return;
                        }
                    }
                    builder.getGenerationSettings().addFeature(GenerationStep.Decoration.TOP_LAYER_MODIFICATION, this.freezeTop);
                }
            } else if (phase == Phase.MODIFY) {
                if (canModify(biome)) {
                    builder.getClimateSettings().setHasPrecipitation(true);
                    builder.getClimateSettings().setTemperature(0.0f);
                    builder.getClimateSettings().setDownfall(0.5f);
                    builder.getClimateSettings().setTemperatureModifier(CONFIG.enableTemperatureNoise() ? Biome.TemperatureModifier.FROZEN : Biome.TemperatureModifier.NONE);
                }
            }
        }

        @Override
        public MapCodec<? extends BiomeModifier> codec() {
            return BIOME_MODIFIER_CODEC.get();
        }
    }
}