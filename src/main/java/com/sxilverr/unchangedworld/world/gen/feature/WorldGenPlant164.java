package com.sxilverr.unchangedworld.world.gen.feature;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

public class WorldGenPlant164 extends WorldGenerator {

    private final Block block;
    private final int meta;

    public WorldGenPlant164(Block block, int meta) {
        this.block = block;
        this.meta = meta;
    }

    @Override
    public boolean generate(World world, Random rand, int x, int y, int z) {
        boolean grounded = this.block == Blocks.tallgrass || this.block == Blocks.deadbush;
        Block current;

        while (grounded && ((current = world.getBlock(x, y, z)) == Blocks.air || current == Blocks.leaves) && y > 0) {
            --y;
        }

        int attempts = this.block == Blocks.tallgrass ? 128
            : this.block == Blocks.deadbush ? 4 : this.block == Blocks.waterlily ? 10 : 64;

        for (int i = 0; i < attempts; ++i) {
            int px = x + rand.nextInt(8) - rand.nextInt(8);
            int py = y + rand.nextInt(4) - rand.nextInt(4);
            int pz = z + rand.nextInt(8) - rand.nextInt(8);

            if (world.isAirBlock(px, py, pz) && this.canStay(world, px, py, pz)) {
                world.setBlock(px, py, pz, this.block, this.meta, 2);
            }
        }

        return true;
    }

    private boolean canStay(World world, int x, int y, int z) {
        if (this.block == Blocks.waterlily) {
            return world.getBlock(x, y - 1, z) == Blocks.water;
        }

        if (this.block != Blocks.tallgrass && this.block != Blocks.deadbush && world.provider.hasNoSky && y >= 127) {
            return false;
        }

        if (this.block == Blocks.brown_mushroom || this.block == Blocks.red_mushroom) {
            if (y < 0 || y >= 256) {
                return false;
            }

            Block soil = world.getBlock(x, y - 1, z);
            return soil == Blocks.mycelium || world.getFullBlockLightValue(x, y, z) < 13 && soil.func_149730_j();
        }

        if (world.getFullBlockLightValue(x, y, z) < 8 && !world.canBlockSeeTheSky(x, y, z)) {
            return false;
        }

        Block soil = world.getBlock(x, y - 1, z);
        return this.block == Blocks.deadbush ? soil == Blocks.sand
            : soil == Blocks.grass || soil == Blocks.dirt || soil == Blocks.farmland;
    }
}
