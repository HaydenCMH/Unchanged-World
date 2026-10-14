package com.sxilverr.unchangedworld.world.biome;

import net.minecraft.world.biome.BiomeGenBase;

public final class Decoration164 {

    public enum Kind {
        DEFAULT,
        PLAINS,
        DESERT,
        HILLS,
        SNOW,
        FOREST,
        TAIGA,
        SWAMP,
        JUNGLE
    }

    private static final Decoration164[] TABLE = new Decoration164[BiomeGenBase.getBiomeGenArray().length];
    private static final Decoration164 DEFAULT = new Decoration164(Kind.DEFAULT, 0, 2, 1, 0, 0, 0, 0, 0, 0);

    static {
        put(new Decoration164(Kind.PLAINS, -999, 4, 10, 0, 0, 0, 0, 0, 0), BiomeGenBase.plains);
        put(
            new Decoration164(Kind.DESERT, -999, 2, 1, 2, 0, 50, 10, 0, 0),
            BiomeGenBase.desert,
            BiomeGenBase.desertHills);
        put(
            new Decoration164(Kind.HILLS, 0, 2, 1, 0, 0, 0, 0, 0, 0),
            BiomeGenBase.extremeHills,
            BiomeGenBase.extremeHillsEdge);
        put(new Decoration164(Kind.FOREST, 10, 2, 2, 0, 0, 0, 0, 0, 0), BiomeGenBase.forest, BiomeGenBase.forestHills);
        put(new Decoration164(Kind.TAIGA, 10, 2, 1, 0, 0, 0, 0, 0, 0), BiomeGenBase.taiga, BiomeGenBase.taigaHills);
        put(new Decoration164(Kind.SWAMP, 2, -999, 1, 1, 8, 10, 0, 0, 4), BiomeGenBase.swampland);
        put(
            new Decoration164(Kind.DEFAULT, -100, -100, -100, 0, 1, 0, 0, 1, 0),
            BiomeGenBase.mushroomIsland,
            BiomeGenBase.mushroomIslandShore);
        put(new Decoration164(Kind.DEFAULT, -999, 2, 1, 0, 0, 0, 0, 0, 0), BiomeGenBase.beach);
        put(new Decoration164(Kind.SNOW, 0, 2, 1, 0, 0, 0, 0, 0, 0), BiomeGenBase.icePlains, BiomeGenBase.iceMountains);
        put(new Decoration164(Kind.JUNGLE, 50, 4, 25, 0, 0, 0, 0, 0, 0), BiomeGenBase.jungle, BiomeGenBase.jungleHills);
    }

    public final Kind kind;
    public final int treesPerChunk;
    public final int flowersPerChunk;
    public final int grassPerChunk;
    public final int deadBushPerChunk;
    public final int mushroomsPerChunk;
    public final int reedsPerChunk;
    public final int cactiPerChunk;
    public final int bigMushroomsPerChunk;
    public final int waterlilyPerChunk;

    private Decoration164(Kind kind, int trees, int flowers, int grass, int deadBushes, int mushrooms, int reeds,
        int cacti, int bigMushrooms, int waterlilies) {
        this.kind = kind;
        this.treesPerChunk = trees;
        this.flowersPerChunk = flowers;
        this.grassPerChunk = grass;
        this.deadBushPerChunk = deadBushes;
        this.mushroomsPerChunk = mushrooms;
        this.reedsPerChunk = reeds;
        this.cactiPerChunk = cacti;
        this.bigMushroomsPerChunk = bigMushrooms;
        this.waterlilyPerChunk = waterlilies;
    }

    private static void put(Decoration164 decoration, BiomeGenBase... biomes) {
        for (BiomeGenBase biome : biomes) {
            TABLE[biome.biomeID] = decoration;
        }
    }

    public static Decoration164 get(BiomeGenBase biome) {
        Decoration164 decoration = biome == null ? null : TABLE[biome.biomeID];
        return decoration == null ? DEFAULT : decoration;
    }
}
