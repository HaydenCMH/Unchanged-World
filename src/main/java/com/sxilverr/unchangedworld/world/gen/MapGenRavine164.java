package com.sxilverr.unchangedworld.world.gen;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class MapGenRavine164 extends MapGenCaves164 {

    private final float[] widthScale = new float[1024];

    protected void generateRavine(long seed, int chunkX, int chunkZ, Block[] blocks, double x, double y, double z,
        float radius, float yaw, float pitch, int step, int steps, double heightScale) {
        Random random = new Random(seed);
        double centerX = (double) (chunkX * 16 + 8);
        double centerZ = (double) (chunkZ * 16 + 8);
        float yawDelta = 0.0F;
        float pitchDelta = 0.0F;

        if (steps <= 0) {
            int maxSteps = this.range * 16 - 16;
            steps = maxSteps - random.nextInt(maxSteps / 4);
        }

        boolean single = false;

        if (step == -1) {
            step = steps / 2;
            single = true;
        }

        float scale = 1.0F;

        for (int i = 0; i < 128; ++i) {
            if (i == 0 || random.nextInt(3) == 0) {
                scale = 1.0F + random.nextFloat() * random.nextFloat() * 1.0F;
            }

            this.widthScale[i] = scale * scale;
        }

        for (; step < steps; ++step) {
            double horizontal = 1.5D
                + (double) (MathHelper.sin((float) step * (float) Math.PI / (float) steps) * radius * 1.0F);
            double vertical = horizontal * heightScale;
            horizontal *= (double) random.nextFloat() * 0.25D + 0.75D;
            vertical *= (double) random.nextFloat() * 0.25D + 0.75D;
            float cosPitch = MathHelper.cos(pitch);
            float sinPitch = MathHelper.sin(pitch);
            x += (double) (MathHelper.cos(yaw) * cosPitch);
            y += (double) sinPitch;
            z += (double) (MathHelper.sin(yaw) * cosPitch);
            pitch *= 0.7F;
            pitch += pitchDelta * 0.05F;
            yaw += yawDelta * 0.05F;
            pitchDelta *= 0.8F;
            yawDelta *= 0.5F;
            pitchDelta += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 2.0F;
            yawDelta += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 4.0F;

            if (single || random.nextInt(4) != 0) {
                double offsetX = x - centerX;
                double offsetZ = z - centerZ;
                double remaining = (double) (steps - step);
                double reach = (double) (radius + 2.0F + 16.0F);

                if (offsetX * offsetX + offsetZ * offsetZ - remaining * remaining > reach * reach) {
                    return;
                }

                if (this.carve(chunkX, chunkZ, blocks, x, y, z, horizontal, vertical) && single) {
                    break;
                }
            }
        }
    }

    @Override
    protected boolean inside(double nx, double ny, double nz, int y) {
        return (nx * nx + nz * nz) * (double) this.widthScale[y] + ny * ny / 6.0D < 1.0D;
    }

    @Override
    protected void func_151538_a(World world, int sourceX, int sourceZ, int chunkX, int chunkZ, Block[] blocks) {
        if (this.rand.nextInt(50) == 0) {
            double x = (double) (sourceX * 16 + this.rand.nextInt(16));
            double y = (double) (this.rand.nextInt(this.rand.nextInt(40) + 8) + 20);
            double z = (double) (sourceZ * 16 + this.rand.nextInt(16));
            float yaw = this.rand.nextFloat() * (float) Math.PI * 2.0F;
            float pitch = (this.rand.nextFloat() - 0.5F) * 2.0F / 8.0F;
            float radius = (this.rand.nextFloat() * 2.0F + this.rand.nextFloat()) * 2.0F;
            this.generateRavine(this.rand.nextLong(), chunkX, chunkZ, blocks, x, y, z, radius, yaw, pitch, 0, 0, 3.0D);
        }
    }
}
