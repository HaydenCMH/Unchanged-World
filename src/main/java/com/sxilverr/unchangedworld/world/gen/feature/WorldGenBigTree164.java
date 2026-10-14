package com.sxilverr.unchangedworld.world.gen.feature;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

public class WorldGenBigTree164 extends WorldGenerator {

    private static final byte[] OTHER_COORD_PAIRS = { (byte) 2, (byte) 0, (byte) 0, (byte) 1, (byte) 2, (byte) 1 };
    private static final int LEAF_DISTANCE_LIMIT = 5;

    private final Random rand = new Random();
    private World worldObj;
    private final int[] basePos = { 0, 0, 0 };
    private int heightLimit;
    private int height;
    private int[][] leafNodes;

    private void generateLeafNodeList() {
        this.height = (int) ((double) this.heightLimit * 0.618D);

        if (this.height >= this.heightLimit) {
            this.height = this.heightLimit - 1;
        }

        int nodesPerLayer = (int) (1.382D + Math.pow((double) this.heightLimit / 13.0D, 2.0D));

        if (nodesPerLayer < 1) {
            nodesPerLayer = 1;
        }

        int[][] nodes = new int[nodesPerLayer * this.heightLimit][4];
        int y = this.basePos[1] + this.heightLimit - LEAF_DISTANCE_LIMIT;
        int count = 1;
        int trunkTop = this.basePos[1] + this.height;
        int layer = y - this.basePos[1];
        nodes[0][0] = this.basePos[0];
        nodes[0][1] = y;
        nodes[0][2] = this.basePos[2];
        nodes[0][3] = trunkTop;
        --y;

        while (layer >= 0) {
            int i = 0;
            float size = this.layerSize(layer);

            if (size < 0.0F) {
                --y;
                --layer;
            } else {
                for (double offset = 0.5D; i < nodesPerLayer; ++i) {
                    double distance = (double) size * ((double) this.rand.nextFloat() + 0.328D);
                    double angle = (double) this.rand.nextFloat() * 2.0D * Math.PI;
                    int nodeX = MathHelper.floor_double(distance * Math.sin(angle) + (double) this.basePos[0] + offset);
                    int nodeZ = MathHelper.floor_double(distance * Math.cos(angle) + (double) this.basePos[2] + offset);
                    int[] node = { nodeX, y, nodeZ };
                    int[] nodeTop = { nodeX, y + LEAF_DISTANCE_LIMIT, nodeZ };

                    if (this.checkBlockLine(node, nodeTop) == -1) {
                        int[] branchStart = { this.basePos[0], this.basePos[1], this.basePos[2] };
                        double horizontal = Math.sqrt(
                            Math.pow((double) Math.abs(this.basePos[0] - node[0]), 2.0D)
                                + Math.pow((double) Math.abs(this.basePos[2] - node[2]), 2.0D));
                        double drop = horizontal * 0.381D;

                        if ((double) node[1] - drop > (double) trunkTop) {
                            branchStart[1] = trunkTop;
                        } else {
                            branchStart[1] = (int) ((double) node[1] - drop);
                        }

                        if (this.checkBlockLine(branchStart, node) == -1) {
                            nodes[count][0] = nodeX;
                            nodes[count][1] = y;
                            nodes[count][2] = nodeZ;
                            nodes[count][3] = branchStart[1];
                            ++count;
                        }
                    }
                }

                --y;
                --layer;
            }
        }

        this.leafNodes = new int[count][4];
        System.arraycopy(nodes, 0, this.leafNodes, 0, count);
    }

    private void genTreeLayer(int x, int y, int z, float radius, byte axis, Block block) {
        int range = (int) ((double) radius + 0.618D);
        byte axisA = OTHER_COORD_PAIRS[axis];
        byte axisB = OTHER_COORD_PAIRS[axis + 3];
        int[] center = { x, y, z };
        int[] pos = { 0, 0, 0 };
        int a = -range;
        int b = -range;

        for (pos[axis] = center[axis]; a <= range; ++a) {
            pos[axisA] = center[axisA] + a;
            b = -range;

            while (b <= range) {
                double distance = Math.pow((double) Math.abs(a) + 0.5D, 2.0D)
                    + Math.pow((double) Math.abs(b) + 0.5D, 2.0D);

                if (distance > (double) (radius * radius)) {
                    ++b;
                } else {
                    pos[axisB] = center[axisB] + b;
                    Block existing = this.worldObj.getBlock(pos[0], pos[1], pos[2]);

                    if (existing != Blocks.air && existing != Blocks.leaves) {
                        ++b;
                    } else {
                        this.setBlockAndNotifyAdequately(this.worldObj, pos[0], pos[1], pos[2], block, 0);
                        ++b;
                    }
                }
            }
        }
    }

    private float layerSize(int layer) {
        if ((double) layer < (double) ((float) this.heightLimit) * 0.3D) {
            return -1.618F;
        }

        float half = (float) this.heightLimit / 2.0F;
        float offset = (float) this.heightLimit / 2.0F - (float) layer;
        float size;

        if (offset == 0.0F) {
            size = half;
        } else if (Math.abs(offset) >= half) {
            size = 0.0F;
        } else {
            size = (float) Math
                .sqrt(Math.pow((double) Math.abs(half), 2.0D) - Math.pow((double) Math.abs(offset), 2.0D));
        }

        size *= 0.5F;
        return size;
    }

    private float leafSize(int layer) {
        return layer >= 0 && layer < LEAF_DISTANCE_LIMIT
            ? (layer != 0 && layer != LEAF_DISTANCE_LIMIT - 1 ? 3.0F : 2.0F)
            : -1.0F;
    }

    private void generateLeafNode(int x, int y, int z) {
        int cy = y;

        for (int top = y + LEAF_DISTANCE_LIMIT; cy < top; ++cy) {
            float size = this.leafSize(cy - y);
            this.genTreeLayer(x, cy, z, size, (byte) 1, Blocks.leaves);
        }
    }

    private void placeBlockLine(int[] from, int[] to, Block block) {
        int[] delta = { 0, 0, 0 };
        byte axis = 0;
        byte major;

        for (major = 0; axis < 3; ++axis) {
            delta[axis] = to[axis] - from[axis];

            if (Math.abs(delta[axis]) > Math.abs(delta[major])) {
                major = axis;
            }
        }

        if (delta[major] != 0) {
            byte axisA = OTHER_COORD_PAIRS[major];
            byte axisB = OTHER_COORD_PAIRS[major + 3];
            byte step;

            if (delta[major] > 0) {
                step = 1;
            } else {
                step = -1;
            }

            double slopeA = (double) delta[axisA] / (double) delta[major];
            double slopeB = (double) delta[axisB] / (double) delta[major];
            int[] pos = { 0, 0, 0 };
            int i = 0;

            for (int end = delta[major] + step; i != end; i += step) {
                pos[major] = MathHelper.floor_double((double) (from[major] + i) + 0.5D);
                pos[axisA] = MathHelper.floor_double((double) from[axisA] + (double) i * slopeA + 0.5D);
                pos[axisB] = MathHelper.floor_double((double) from[axisB] + (double) i * slopeB + 0.5D);
                byte meta = 0;
                int dx = Math.abs(pos[0] - from[0]);
                int dz = Math.abs(pos[2] - from[2]);
                int max = Math.max(dx, dz);

                if (max > 0) {
                    if (dx == max) {
                        meta = 4;
                    } else if (dz == max) {
                        meta = 8;
                    }
                }

                this.setBlockAndNotifyAdequately(this.worldObj, pos[0], pos[1], pos[2], block, meta);
            }
        }
    }

    private void generateLeaves() {
        int i = 0;

        for (int count = this.leafNodes.length; i < count; ++i) {
            int x = this.leafNodes[i][0];
            int y = this.leafNodes[i][1];
            int z = this.leafNodes[i][2];
            this.generateLeafNode(x, y, z);
        }
    }

    private boolean leafNodeNeedsBase(int layer) {
        return (double) layer >= (double) this.heightLimit * 0.2D;
    }

    private void generateTrunk() {
        int x = this.basePos[0];
        int bottom = this.basePos[1];
        int top = this.basePos[1] + this.height;
        int z = this.basePos[2];
        int[] from = { x, bottom, z };
        int[] to = { x, top, z };
        this.placeBlockLine(from, to, Blocks.log);
    }

    private void generateLeafNodeBases() {
        int i = 0;
        int count = this.leafNodes.length;

        for (int[] from = { this.basePos[0], this.basePos[1], this.basePos[2] }; i < count; ++i) {
            int[] node = this.leafNodes[i];
            int[] to = { node[0], node[1], node[2] };
            from[1] = node[3];
            int layer = from[1] - this.basePos[1];

            if (this.leafNodeNeedsBase(layer)) {
                this.placeBlockLine(from, to, Blocks.log);
            }
        }
    }

    private int checkBlockLine(int[] from, int[] to) {
        int[] delta = { 0, 0, 0 };
        byte axis = 0;
        byte major;

        for (major = 0; axis < 3; ++axis) {
            delta[axis] = to[axis] - from[axis];

            if (Math.abs(delta[axis]) > Math.abs(delta[major])) {
                major = axis;
            }
        }

        if (delta[major] == 0) {
            return -1;
        }

        byte axisA = OTHER_COORD_PAIRS[major];
        byte axisB = OTHER_COORD_PAIRS[major + 3];
        byte step;

        if (delta[major] > 0) {
            step = 1;
        } else {
            step = -1;
        }

        double slopeA = (double) delta[axisA] / (double) delta[major];
        double slopeB = (double) delta[axisB] / (double) delta[major];
        int[] pos = { 0, 0, 0 };
        int i = 0;
        int end;

        for (end = delta[major] + step; i != end; i += step) {
            pos[major] = from[major] + i;
            pos[axisA] = MathHelper.floor_double((double) from[axisA] + (double) i * slopeA);
            pos[axisB] = MathHelper.floor_double((double) from[axisB] + (double) i * slopeB);
            Block block = this.worldObj.getBlock(pos[0], pos[1], pos[2]);

            if (block != Blocks.air && block != Blocks.leaves) {
                break;
            }
        }

        return i == end ? -1 : Math.abs(i);
    }

    private boolean validTreeLocation() {
        int[] from = { this.basePos[0], this.basePos[1], this.basePos[2] };
        int[] to = { this.basePos[0], this.basePos[1] + this.heightLimit - 1, this.basePos[2] };
        Block soil = this.worldObj.getBlock(this.basePos[0], this.basePos[1] - 1, this.basePos[2]);

        if (soil != Blocks.grass && soil != Blocks.dirt) {
            return false;
        }

        int obstruction = this.checkBlockLine(from, to);

        if (obstruction == -1) {
            return true;
        } else if (obstruction < 6) {
            return false;
        } else {
            this.heightLimit = obstruction;
            return true;
        }
    }

    @Override
    public boolean generate(World world, Random rand, int x, int y, int z) {
        this.worldObj = world;
        long seed = rand.nextLong();
        this.rand.setSeed(seed);
        this.basePos[0] = x;
        this.basePos[1] = y;
        this.basePos[2] = z;

        if (this.heightLimit == 0) {
            this.heightLimit = 5 + this.rand.nextInt(12);
        }

        if (!this.validTreeLocation()) {
            return false;
        }

        this.generateLeafNodeList();
        this.generateLeaves();
        this.generateTrunk();
        this.generateLeafNodeBases();
        return true;
    }
}
