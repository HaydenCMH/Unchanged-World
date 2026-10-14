package com.sxilverr.unchangedworld.world.gen.feature;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

public class WorldGenSwamp164 extends WorldGenerator {

    @Override
    public boolean generate(World world, Random rand, int x, int y, int z) {
        int height = rand.nextInt(4) + 5;

        while (world.getBlock(x, y - 1, z)
            .getMaterial() == Material.water) {
            --y;
        }

        if (y < 1 || y + height + 1 > 128) {
            return false;
        }

        for (int cy = y; cy <= y + 1 + height; ++cy) {
            int radius = cy >= y + 1 + height - 2 ? 3 : (cy == y ? 0 : 1);

            for (int cx = x - radius; cx <= x + radius; ++cx) {
                for (int cz = z - radius; cz <= z + radius; ++cz) {
                    if (cy >= 128) {
                        return false;
                    }

                    Block block = world.getBlock(cx, cy, cz);

                    if (block != Blocks.air && block != Blocks.leaves
                        && (block != Blocks.water && block != Blocks.flowing_water || cy > y)) {
                        return false;
                    }
                }
            }
        }

        Block soil = world.getBlock(x, y - 1, z);

        if (soil != Blocks.grass && soil != Blocks.dirt || y >= 128 - height - 1) {
            return false;
        }

        this.func_150515_a(world, x, y - 1, z, Blocks.dirt);

        for (int cy = y - 3 + height; cy <= y + height; ++cy) {
            int layer = cy - (y + height);
            int radius = 2 - layer / 2;

            for (int cx = x - radius; cx <= x + radius; ++cx) {
                for (int cz = z - radius; cz <= z + radius; ++cz) {
                    if ((Math.abs(cx - x) != radius || Math.abs(cz - z) != radius || rand.nextInt(2) != 0 && layer != 0)
                        && !world.getBlock(cx, cy, cz)
                            .func_149730_j()) {
                        this.func_150515_a(world, cx, cy, cz, Blocks.leaves);
                    }
                }
            }
        }

        for (int cy = 0; cy < height; ++cy) {
            Block block = world.getBlock(x, y + cy, z);

            if (block == Blocks.air || block == Blocks.leaves
                || block == Blocks.flowing_water
                || block == Blocks.water) {
                this.func_150515_a(world, x, y + cy, z, Blocks.log);
            }
        }

        WorldGenTrees164.hangVinesFromLeaves(world, rand, x, y, z, height);
        return true;
    }
}
