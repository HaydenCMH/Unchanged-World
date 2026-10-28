package com.sxilverr.unchangedworld.world.gen.layer;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.IntCache;

import com.sxilverr.unchangedworld.world.biome.ModdedBiomes164;

public class GenLayerBiome164 extends GenLayer {

    private static final int ANY = 0;
    private static final int DRY = GenLayerHumidity164.DRY;
    private static final int WET = GenLayerHumidity164.WET;

    private final BiomeGenBase[] allowedBiomes = { BiomeGenBase.desert, BiomeGenBase.forest, BiomeGenBase.extremeHills,
        BiomeGenBase.swampland, BiomeGenBase.plains, BiomeGenBase.taiga, BiomeGenBase.jungle };
    private final GenLayer temperature;
    private final GenLayer humidity;
    private Pool temperatePool;
    private Pool snowyPool;
    private Pool[][] bandPools;

    public GenLayerBiome164(long seed, GenLayer parent, GenLayer temperature, GenLayer humidity, boolean moddedBiomes,
        int vanillaWeight) {
        super(seed);
        this.parent = parent;
        this.temperature = temperature;
        this.humidity = temperature != null ? humidity : null;

        if (temperature != null) {
            this.createBandPools(moddedBiomes, vanillaWeight);
        } else {
            this.createPools(moddedBiomes, vanillaWeight);
        }
    }

    private void createPools(boolean moddedBiomes, int vanillaWeight) {
        int[] temperate = new int[this.allowedBiomes.length];
        int[] snowy = new int[this.allowedBiomes.length];

        for (int i = 0; i < this.allowedBiomes.length; ++i) {
            BiomeGenBase biome = this.allowedBiomes[i];
            temperate[i] = biome.biomeID;
            snowy[i] = biome == BiomeGenBase.taiga ? biome.biomeID : BiomeGenBase.icePlains.biomeID;
        }

        Map<BiomeGenBase, Integer> none = Collections.emptyMap();
        this.temperatePool = new Pool(
            temperate,
            moddedBiomes ? ModdedBiomes164.temperateWeights() : none,
            vanillaWeight);
        this.snowyPool = new Pool(snowy, moddedBiomes ? ModdedBiomes164.snowyWeights() : none, vanillaWeight);
    }

    private void createBandPools(boolean moddedBiomes, int vanillaWeight) {
        Map<BiomeGenBase, Integer> none = Collections.emptyMap();
        this.bandPools = new Pool[GenLayerTemperature164.COLD + 1][];
        this.bandPools[GenLayerTemperature164.WARM] = this.band(
            moddedBiomes ? ModdedBiomes164.warmWeights() : none,
            vanillaWeight,
            ids(BiomeGenBase.desert, BiomeGenBase.plains, BiomeGenBase.jungle),
            ids(BiomeGenBase.desert, BiomeGenBase.plains),
            ids(BiomeGenBase.jungle, BiomeGenBase.jungle, BiomeGenBase.swampland));
        this.bandPools[GenLayerTemperature164.TEMPERATE] = this.band(
            moddedBiomes ? ModdedBiomes164.temperateBandWeights() : none,
            vanillaWeight,
            ids(BiomeGenBase.forest, BiomeGenBase.plains, BiomeGenBase.swampland, BiomeGenBase.extremeHills),
            ids(BiomeGenBase.plains, BiomeGenBase.extremeHills),
            ids(BiomeGenBase.forest, BiomeGenBase.forest, BiomeGenBase.swampland));
        this.bandPools[GenLayerTemperature164.COOL] = this.band(
            moddedBiomes ? ModdedBiomes164.coolWeights() : none,
            vanillaWeight,
            ids(BiomeGenBase.taiga, BiomeGenBase.forest, BiomeGenBase.extremeHills),
            ids(BiomeGenBase.taiga, BiomeGenBase.extremeHills),
            ids(BiomeGenBase.taiga, BiomeGenBase.forest));
        this.bandPools[GenLayerTemperature164.COLD] = this.band(
            moddedBiomes ? ModdedBiomes164.coldWeights() : none,
            vanillaWeight,
            ids(BiomeGenBase.icePlains, BiomeGenBase.taiga),
            ids(BiomeGenBase.icePlains),
            ids(BiomeGenBase.icePlains, BiomeGenBase.taiga));
    }

    private Pool[] band(Map<BiomeGenBase, Integer> modded, int vanillaWeight, int[] any, int[] dry, int[] wet) {
        Pool[] pools = new Pool[WET + 1];
        pools[ANY] = new Pool(any, modded, vanillaWeight);
        pools[DRY] = new Pool(dry, humid(modded, true), vanillaWeight);
        pools[WET] = new Pool(wet, humid(modded, false), vanillaWeight);
        return pools;
    }

    private static Map<BiomeGenBase, Integer> humid(Map<BiomeGenBase, Integer> modded, boolean dry) {
        Map<BiomeGenBase, Integer> filtered = new LinkedHashMap<BiomeGenBase, Integer>();

        for (Map.Entry<BiomeGenBase, Integer> entry : modded.entrySet()) {
            if (ModdedBiomes164.isDry(entry.getKey()) == dry) {
                filtered.put(entry.getKey(), entry.getValue());
            }
        }

        return filtered;
    }

    private static int[] ids(BiomeGenBase... biomes) {
        int[] ids = new int[biomes.length];

        for (int i = 0; i < biomes.length; ++i) {
            ids[i] = biomes[i].biomeID;
        }

        return ids;
    }

    @Override
    public void initWorldGenSeed(long seed) {
        if (this.temperature != null) {
            this.temperature.initWorldGenSeed(seed);
        }

        if (this.humidity != null) {
            this.humidity.initWorldGenSeed(seed);
        }

        super.initWorldGenSeed(seed);
    }

    @Override
    public int[] getInts(int areaX, int areaY, int areaWidth, int areaHeight) {
        if (this.temperature != null) {
            return this.getBandInts(areaX, areaY, areaWidth, areaHeight);
        }

        int[] parentInts = this.parent.getInts(areaX, areaY, areaWidth, areaHeight);
        int[] out = IntCache.getIntCache(areaWidth * areaHeight);

        for (int y = 0; y < areaHeight; ++y) {
            for (int x = 0; x < areaWidth; ++x) {
                this.initChunkSeed(x + areaX, y + areaY);
                int value = parentInts[x + y * areaWidth];

                if (value == 0) {
                    out[x + y * areaWidth] = 0;
                } else if (value == BiomeGenBase.mushroomIsland.biomeID) {
                    out[x + y * areaWidth] = value;
                } else if (value == 1) {
                    out[x + y * areaWidth] = this.temperatePool.pick(this.nextInt(this.temperatePool.totalWeight));
                } else {
                    out[x + y * areaWidth] = this.snowyPool.pick(this.nextInt(this.snowyPool.totalWeight));
                }
            }
        }

        return out;
    }

    private int[] getBandInts(int areaX, int areaY, int areaWidth, int areaHeight) {
        int[] parentInts = this.parent.getInts(areaX, areaY, areaWidth, areaHeight);
        int[] bands = this.temperature.getInts(areaX, areaY, areaWidth, areaHeight);
        int[] humidities = this.humidity != null ? this.humidity.getInts(areaX, areaY, areaWidth, areaHeight) : null;
        int[] out = IntCache.getIntCache(areaWidth * areaHeight);

        for (int i = 0; i < areaWidth * areaHeight; ++i) {
            this.initChunkSeed(i % areaWidth + areaX, i / areaWidth + areaY);
            int value = parentInts[i];

            if (value == 0 || value == BiomeGenBase.mushroomIsland.biomeID) {
                out[i] = value;
            } else {
                Pool pool = this.bandPools[bands[i]][humidities != null ? humidities[i] : ANY];
                out[i] = pool.pick(this.nextInt(pool.totalWeight));
            }
        }

        return out;
    }

    private static final class Pool {

        private final int[] ids;
        private final int[] weights;
        private final int totalWeight;

        private Pool(int[] vanillaIds, Map<BiomeGenBase, Integer> modded, int vanillaWeight) {
            int size = vanillaIds.length + modded.size();
            this.ids = new int[size];
            this.weights = new int[size];
            int index = 0;

            for (int id : vanillaIds) {
                this.ids[index] = id;
                this.weights[index++] = vanillaWeight;
            }

            for (Map.Entry<BiomeGenBase, Integer> entry : modded.entrySet()) {
                this.ids[index] = entry.getKey().biomeID;
                this.weights[index++] = entry.getValue();
            }

            int divisor = 0;

            for (int weight : this.weights) {
                divisor = gcd(divisor, weight);
            }

            int total = 0;

            for (int i = 0; i < size; ++i) {
                this.weights[i] /= divisor;
                total += this.weights[i];
            }

            this.totalWeight = total;
        }

        private int pick(int roll) {
            for (int i = 0; i < this.ids.length; ++i) {
                roll -= this.weights[i];

                if (roll < 0) {
                    return this.ids[i];
                }
            }

            return this.ids[this.ids.length - 1];
        }

        private static int gcd(int a, int b) {
            return b == 0 ? a : gcd(b, a % b);
        }
    }
}
