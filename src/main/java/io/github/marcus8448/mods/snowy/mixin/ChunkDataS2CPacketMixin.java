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

import io.github.marcus8448.mods.snowy.SnowyMod;
import net.minecraft.network.packet.s2c.play.ChunkDataS2CPacket;
import net.minecraft.world.biome.source.BiomeArray;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ChunkDataS2CPacket.class)
public class ChunkDataS2CPacketMixin {
    @SuppressWarnings("UnnecessaryQualifiedMemberReference")
    @Redirect(method = "Lnet/minecraft/network/packet/s2c/play/ChunkDataS2CPacket;<init>(Lnet/minecraft/world/chunk/WorldChunk;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/biome/source/BiomeArray;toIntArray()[I"))
    private int[] offsetBiomes(BiomeArray biomeArray) {
        int[] ints = biomeArray.toIntArray();
        for (int i = 0; i < ints.length; i++) {
            ints[i] = ints[i] + SnowyMod.addition;
        }
        return ints;
    }
}
