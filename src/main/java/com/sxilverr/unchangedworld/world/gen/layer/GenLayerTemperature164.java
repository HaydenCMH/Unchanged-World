package com.sxilverr.unchangedworld.world.gen.layer;

import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.IntCache;

public class GenLayerTemperature164 extends GenLayer {

    public static final int WARM = 1;
    public static final int TEMPERATE = 2;
    public static final int COOL = 3;
    public static final int COLD = 4;

    public GenLayerTemperature164(long seed) {
        super(seed);
    }

    @Override
    public int[] getInts(int areaX, int areaY, int areaWidth, int areaHeight) {
        int[] out = IntCache.getIntCache(areaWidth * areaHeight);

        for (int y = 0; y < areaHeight; ++y) {
            for (int x = 0; x < areaWidth; ++x) {
                this.initChunkSeed(areaX + x, areaY + y);
                out[x + y * areaWidth] = this.nextInt(8) < 3 ? COLD : WARM;
            }
        }

        return out;
    }
}
