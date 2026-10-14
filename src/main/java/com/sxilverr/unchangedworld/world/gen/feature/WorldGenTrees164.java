package com.sxilverr.unchangedworld.world.gen.feature;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.Direction;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

public class WorldGenTrees164 extends WorldGenerator {

    private final int minTreeHeight;
    private final int meta;
    private final boolean vinesGrow;
    private final boolean birch;

    public WorldGenTrees164(int minTreeHeight, int meta, boolean vinesGrow, boolean birch) {
        this.minTreeHeight = minTreeHeight;
        this.meta = meta;
        this.vinesGrow = vinesGrow;
        this.birch = birch;
    }

    @Override
    public boolean generate(World world, Random rand, int x, int y, int z) {
        int height = rand.nextInt(3) + this.minTreeHeight;

        if (y < 1 || y + height + 1 > 256) {
            return false;
        }

        for (int cy = y; cy <= y + 1 + height; ++cy) {
            int radius = cy >= y + 1 + height - 2 ? 2 : (cy == y ? 0 : 1);

            for (int cx = x - radius; cx <= x + radius; ++cx) {
                for (int cz = z - radius; cz <= z + radius; ++cz) {
                    if (cy >= 256 || !this.canGrowThrough(world.getBlock(cx, cy, cz))) {
                        return false;
                    }
                }
            }
        }

        Block soil = world.getBlock(x, y - 1, z);

        if (soil != Blocks.grass && soil != Blocks.dirt || y >= 256 - height - 1) {
            return false;
        }

        this.func_150515_a(world, x, y - 1, z, Blocks.dirt);

        for (int cy = y - 3 + height; cy <= y + height; ++cy) {
            int layer = cy - (y + height);
            int radius = 1 - layer / 2;

            for (int cx = x - radius; cx <= x + radius; ++cx) {
                for (int cz = z - radius; cz <= z + radius; ++cz) {
                    if (Math.abs(cx - x) != radius || Math.abs(cz - z) != radius
                        || rand.nextInt(2) != 0 && layer != 0) {
                        Block block = world.getBlock(cx, cy, cz);

                        if (block == Blocks.air || block == Blocks.leaves) {
                            this.setBlockAndNotifyAdequately(world, cx, cy, cz, Blocks.leaves, this.meta);
                        }
                    }
                }
            }
        }

        for (int cy = 0; cy < height; ++cy) {
            Block block = world.getBlock(x, y + cy, z);

            if (block == Blocks.air || block == Blocks.leaves) {
                this.setBlockAndNotifyAdequately(world, x, y + cy, z, Blocks.log, this.meta);

                if (this.vinesGrow && cy > 0) {
                    if (rand.nextInt(3) > 0 && world.isAirBlock(x - 1, y + cy, z)) {
                        this.setBlockAndNotifyAdequately(world, x - 1, y + cy, z, Blocks.vine, 8);
                    }

                    if (rand.nextInt(3) > 0 && world.isAirBlock(x + 1, y + cy, z)) {
                        this.setBlockAndNotifyAdequately(world, x + 1, y + cy, z, Blocks.vine, 2);
                    }

                    if (rand.nextInt(3) > 0 && world.isAirBlock(x, y + cy, z - 1)) {
                        this.setBlockAndNotifyAdequately(world, x, y + cy, z - 1, Blocks.vine, 1);
                    }

                    if (rand.nextInt(3) > 0 && world.isAirBlock(x, y + cy, z + 1)) {
                        this.setBlockAndNotifyAdequately(world, x, y + cy, z + 1, Blocks.vine, 4);
                    }
                }
            }
        }

        if (this.vinesGrow) {
            hangVinesFromLeaves(world, rand, x, y, z, height);

            if (rand.nextInt(5) == 0 && height > 5) {
                for (int i = 0; i < 2; ++i) {
                    for (int side = 0; side < 4; ++side) {
                        if (rand.nextInt(4 - i) == 0) {
                            this.setBlockAndNotifyAdequately(
                                world,
                                x + Direction.offsetX[Direction.rotateOpposite[side]],
                                y + height - 5 + i,
                                z + Direction.offsetZ[Direction.rotateOpposite[side]],
                                Blocks.cocoa,
                                rand.nextInt(3) << 2 | side);
                        }
                    }
                }
            }
        }

        return true;
    }

    private boolean canGrowThrough(Block block) {
        return block == Blocks.air || block == Blocks.leaves
            || !this.birch && (block == Blocks.grass || block == Blocks.dirt || block == Blocks.log);
    }

    static void hangVinesFromLeaves(World world, Random rand, int x, int y, int z, int height) {
        for (int cy = y - 3 + height; cy <= y + height; ++cy) {
            int radius = 2 - (cy - (y + height)) / 2;

            for (int cx = x - radius; cx <= x + radius; ++cx) {
                for (int cz = z - radius; cz <= z + radius; ++cz) {
                    if (world.getBlock(cx, cy, cz) == Blocks.leaves) {
                        if (rand.nextInt(4) == 0 && world.getBlock(cx - 1, cy, cz) == Blocks.air) {
                            hangVine(world, cx - 1, cy, cz, 8);
                        }

                        if (rand.nextInt(4) == 0 && world.getBlock(cx + 1, cy, cz) == Blocks.air) {
                            hangVine(world, cx + 1, cy, cz, 2);
                        }

                        if (rand.nextInt(4) == 0 && world.getBlock(cx, cy, cz - 1) == Blocks.air) {
                            hangVine(world, cx, cy, cz - 1, 1);
                        }

                        if (rand.nextInt(4) == 0 && world.getBlock(cx, cy, cz + 1) == Blocks.air) {
                            hangVine(world, cx, cy, cz + 1, 4);
                        }
                    }
                }
            }
        }
    }

    private static void hangVine(World world, int x, int y, int z, int meta) {
        world.setBlock(x, y, z, Blocks.vine, meta, 2);

        for (int remaining = 4; world.getBlock(x, --y, z) == Blocks.air && remaining > 0; --remaining) {
            world.setBlock(x, y, z, Blocks.vine, meta, 2);
        }
    }
}
