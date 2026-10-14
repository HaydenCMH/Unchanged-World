package com.sxilverr.unchangedworld.world.biome;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;

public final class Climate164 {

    private Climate164() {}

    public static float getTemperature(World world, int x, int z) {
        return Biome164.get(world.getBiomeGenForCoords(x, z)).temperature;
    }

    public static boolean canBlockFreeze(World world, int x, int y, int z) {
        if (!isCold(world, x, y, z)) {
            return false;
        }

        Block block = world.getBlock(x, y, z);
        return (block == Blocks.water || block == Blocks.flowing_water) && world.getBlockMetadata(x, y, z) == 0;
    }

    public static boolean canSnowAt(World world, int x, int y, int z) {
        if (!isCold(world, x, y, z)) {
            return false;
        }

        Block below = world.getBlock(x, y - 1, z);
        return world.getBlock(x, y, z) == Blocks.air && Blocks.snow_layer.canPlaceBlockAt(world, x, y, z)
            && below != Blocks.air
            && below != Blocks.ice
            && below.getMaterial()
                .blocksMovement();
    }

    private static boolean isCold(World world, int x, int y, int z) {
        return getTemperature(world, x, z) <= 0.15F && y >= 0
            && y < 256
            && world.getSavedLightValue(EnumSkyBlock.Block, x, y, z) < 10;
    }
}
