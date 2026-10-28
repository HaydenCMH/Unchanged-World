package com.sxilverr.unchangedworld.world.gen.layer;

import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.IntCache;

public class GenLayerHumidity164 extends GenLayer {

    public static final int DRY = 1;
    public static final int WET = 2;

    public GenLayerHumidity164(long seed) {
        super(seed);
    }

    @Override
    public int[] getInts(int areaX, int areaY, int areaWidth, int areaHeight) {
        int[] out = IntCache.getIntCache(areaWidth * areaHeight);

        for (int y = 0; y < areaHeight; ++y) {
            for (int x = 0; x < areaWidth; ++x) {
                this.initChunkSeed(areaX + x, areaY + y);
                out[x + y * areaWidth] = this.nextInt(2) == 0 ? DRY : WET;
            }
        }

        return out;
    }
}
