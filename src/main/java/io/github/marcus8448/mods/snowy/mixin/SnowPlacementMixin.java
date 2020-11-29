/*
 * Snowy - A snowy Minecraft mod
 * Copyright (C) 2020  marcus8448
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package io.github.marcus8448.mods.snowy.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LightType;
import net.minecraft.world.WorldView;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.FreezeTopLayerFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = FreezeTopLayerFeature.class)
public class SnowPlacementMixin {
    @Redirect(method = "generate(Lnet/minecraft/world/StructureWorldAccess;Lnet/minecraft/world/gen/chunk/ChunkGenerator;Ljava/util/Random;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/gen/feature/DefaultFeatureConfig;)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/biome/Biome;canSetSnow(Lnet/minecraft/world/WorldView;Lnet/minecraft/util/math/BlockPos;)Z"))
    private boolean placeSnow(Biome biome, WorldView world, BlockPos blockPos) {
        if (biome.getPrecipitation() != Biome.Precipitation.NONE) {
            if (blockPos.getY() >= 0 && blockPos.getY() < 256 && world.getLightLevel(LightType.BLOCK, blockPos) < 10) {
                BlockState blockState = world.getBlockState(blockPos);
                return blockState.isAir() && Blocks.SNOW.getDefaultState().canPlaceAt(world, blockPos);
            }
        }
        return false;
    }

    @Redirect(method = "generate(Lnet/minecraft/world/StructureWorldAccess;Lnet/minecraft/world/gen/chunk/ChunkGenerator;Ljava/util/Random;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/gen/feature/DefaultFeatureConfig;)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/biome/Biome;canSetIce(Lnet/minecraft/world/WorldView;Lnet/minecraft/util/math/BlockPos;Z)Z"))
    private boolean placeIce(Biome biome, WorldView world, BlockPos pos, boolean doWaterCheck) {
        if (biome.getPrecipitation() != Biome.Precipitation.NONE) {
            if (pos.getY() >= 0 && pos.getY() < 256 && world.getLightLevel(LightType.BLOCK, pos) < 10) {
                BlockState blockState = world.getBlockState(pos);
                FluidState fluidState = world.getFluidState(pos);
                if (fluidState.getFluid() == Fluids.WATER && blockState.getBlock() instanceof FluidBlock) {
                    if (!doWaterCheck) {
                        return true;
                    }

                    return !world.isWater(pos.west()) && world.isWater(pos.east()) && world.isWater(pos.north()) && world.isWater(pos.south());

                }
            }
        }
        return false;
    }
}
