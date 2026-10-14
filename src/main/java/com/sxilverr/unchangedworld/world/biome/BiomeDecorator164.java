package com.sxilverr.unchangedworld.world.biome;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.feature.WorldGenBigMushroom;
import net.minecraft.world.gen.feature.WorldGenCactus;
import net.minecraft.world.gen.feature.WorldGenClay;
import net.minecraft.world.gen.feature.WorldGenDesertWells;
import net.minecraft.world.gen.feature.WorldGenLiquids;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraft.world.gen.feature.WorldGenPumpkin;
import net.minecraft.world.gen.feature.WorldGenReed;
import net.minecraft.world.gen.feature.WorldGenSand;
import net.minecraft.world.gen.feature.WorldGenVines;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent.Decorate.EventType;
import net.minecraftforge.event.terraingen.OreGenEvent;
import net.minecraftforge.event.terraingen.OreGenEvent.GenerateMinable;
import net.minecraftforge.event.terraingen.TerrainGen;

import com.sxilverr.unchangedworld.world.gen.feature.WorldGenBigTree164;
import com.sxilverr.unchangedworld.world.gen.feature.WorldGenHugeTrees164;
import com.sxilverr.unchangedworld.world.gen.feature.WorldGenPlant164;
import com.sxilverr.unchangedworld.world.gen.feature.WorldGenShrub164;
import com.sxilverr.unchangedworld.world.gen.feature.WorldGenSwamp164;
import com.sxilverr.unchangedworld.world.gen.feature.WorldGenTaiga164;
import com.sxilverr.unchangedworld.world.gen.feature.WorldGenTaiga2164;
import com.sxilverr.unchangedworld.world.gen.feature.WorldGenTrees164;

public class BiomeDecorator164 {

    private final WorldGenerator clayGen = new WorldGenClay(4);
    private final WorldGenerator sandGen = new WorldGenSand(Blocks.sand, 7);
    private final WorldGenerator lapisGen = new WorldGenMinable(Blocks.lapis_ore, 6);
    private final WorldGenerator silverfishGen = new WorldGenMinable(Blocks.monster_egg, 8);
    private final WorldGenerator plantYellowGen = new WorldGenPlant164(Blocks.yellow_flower, 0);
    private final WorldGenerator plantRedGen = new WorldGenPlant164(Blocks.red_flower, 0);
    private final WorldGenerator mushroomBrownGen = new WorldGenPlant164(Blocks.brown_mushroom, 0);
    private final WorldGenerator mushroomRedGen = new WorldGenPlant164(Blocks.red_mushroom, 0);
    private final WorldGenerator deadBushGen = new WorldGenPlant164(Blocks.deadbush, 0);
    private final WorldGenerator waterlilyGen = new WorldGenPlant164(Blocks.waterlily, 0);
    private final WorldGenerator bigMushroomGen = new WorldGenBigMushroom();
    private final WorldGenerator reedGen = new WorldGenReed();
    private final WorldGenerator cactusGen = new WorldGenCactus();
    protected final WorldGenerator treesGen = new WorldGenTrees164(4, 0, false, false);
    protected final WorldGenerator forestGen = new WorldGenTrees164(5, 2, false, true);
    private final WorldGenerator swampGen = new WorldGenSwamp164();
    private final WorldGenBigTree164[] bigTreeGens = new WorldGenBigTree164[BiomeGenBase.getBiomeGenArray().length];

    protected World world;
    protected Random rand;
    protected int chunkX;
    protected int chunkZ;
    protected Decoration164 settings;
    protected BiomeGenBase biome;

    public void decorate(World world, Random rand, BiomeGenBase biome, int chunkX, int chunkZ) {
        if (this.world != null) {
            throw new RuntimeException("Already decorating!!");
        }

        this.world = world;
        this.rand = rand;
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
        this.settings = Decoration164.get(biome);
        this.biome = biome;
        this.decorateBefore();
        this.decorate();
        this.decorateAfter();
        this.decorateExtra();
        this.world = null;
    }

    protected void decorateBefore() {}

    protected void decorateAfter() {}

    protected int treesPerChunk() {
        return this.settings.treesPerChunk;
    }

    protected boolean sandPatches() {
        return true;
    }

    protected int flowersPerChunk() {
        return this.settings.flowersPerChunk;
    }

    protected int grassPerChunk() {
        return this.settings.grassPerChunk;
    }

    protected int mushroomsPerChunk() {
        return this.settings.mushroomsPerChunk;
    }

    protected WorldGenerator secondSandGen() {
        return this.sandGen;
    }

    protected boolean jungleVines() {
        return true;
    }

    protected int randX() {
        return this.chunkX + this.rand.nextInt(16) + 8;
    }

    protected int randZ() {
        return this.chunkZ + this.rand.nextInt(16) + 8;
    }

    protected void decorateFlowers() {
        for (int i = 0; i < this.flowersPerChunk(); ++i) {
            this.scatter(this.plantYellowGen, 1);

            if (this.rand.nextInt(4) == 0) {
                this.scatter(this.plantRedGen, 1);
            }
        }
    }

    protected void decorateGrass() {
        for (int i = 0; i < this.grassPerChunk(); ++i) {
            int x = this.randX();
            int y = this.rand.nextInt(128);
            int z = this.randZ();
            this.getRandomWorldGenForGrass()
                .generate(this.world, this.rand, x, y, z);
        }
    }

    private void decorate() {
        MinecraftForge.EVENT_BUS.post(new DecorateBiomeEvent.Pre(this.world, this.rand, this.chunkX, this.chunkZ));
        this.generateOres();

        if (this.decorates(EventType.SAND)) {
            this.onSurface(this.sandGen, this.sandPatches() ? 3 : 0, true);
        }

        if (this.decorates(EventType.CLAY)) {
            this.onSurface(this.clayGen, 1, true);
        }

        if (this.decorates(EventType.SAND_PASS2)) {
            this.onSurface(this.secondSandGen(), this.sandPatches() ? 1 : 0, true);
        }

        int trees = this.treesPerChunk();

        if (this.rand.nextInt(10) == 0) {
            ++trees;
        }

        if (this.decorates(EventType.TREE)) {
            for (int i = 0; i < trees; ++i) {
                int x = this.randX();
                int z = this.randZ();
                this.getRandomWorldGenForTrees()
                    .generate(this.world, this.rand, x, this.world.getHeightValue(x, z), z);
            }
        }

        if (this.decorates(EventType.BIG_SHROOM)) {
            this.onSurface(this.bigMushroomGen, this.settings.bigMushroomsPerChunk, false);
        }

        if (this.decorates(EventType.FLOWERS)) {
            this.decorateFlowers();
        }

        if (this.decorates(EventType.GRASS)) {
            this.decorateGrass();
        }

        if (this.decorates(EventType.DEAD_BUSH)) {
            this.scatter(this.deadBushGen, this.settings.deadBushPerChunk);
        }

        if (this.decorates(EventType.LILYPAD)) {
            for (int i = 0; i < this.settings.waterlilyPerChunk; ++i) {
                int x = this.randX();
                int z = this.randZ();
                int y = this.rand.nextInt(128);

                while (y > 0 && this.world.getBlock(x, y - 1, z) == Blocks.air) {
                    --y;
                }

                this.waterlilyGen.generate(this.world, this.rand, x, y, z);
            }
        }

        if (this.decorates(EventType.SHROOM)) {
            for (int i = 0; i < this.mushroomsPerChunk(); ++i) {
                if (this.rand.nextInt(4) == 0) {
                    this.onSurface(this.mushroomBrownGen, 1, false);
                }

                if (this.rand.nextInt(8) == 0) {
                    this.scatterAfterZ(this.mushroomRedGen, 1);
                }
            }

            if (this.rand.nextInt(4) == 0) {
                this.scatter(this.mushroomBrownGen, 1);
            }

            if (this.rand.nextInt(8) == 0) {
                this.scatter(this.mushroomRedGen, 1);
            }
        }

        if (this.decorates(EventType.REED)) {
            this.scatterAfterZ(this.reedGen, this.settings.reedsPerChunk);
            this.scatter(this.reedGen, 10);
        }

        if (this.decorates(EventType.PUMPKIN) && this.rand.nextInt(32) == 0) {
            this.scatter(new WorldGenPumpkin(), 1);
        }

        if (this.decorates(EventType.CACTUS)) {
            this.scatter(this.cactusGen, this.settings.cactiPerChunk);
        }

        if (this.decorates(EventType.LAKE)) {
            for (int i = 0; i < 50; ++i) {
                new WorldGenLiquids(Blocks.flowing_water).generate(
                    this.world,
                    this.rand,
                    this.randX(),
                    this.rand.nextInt(this.rand.nextInt(120) + 8),
                    this.randZ());
            }

            for (int i = 0; i < 20; ++i) {
                new WorldGenLiquids(Blocks.flowing_lava).generate(
                    this.world,
                    this.rand,
                    this.randX(),
                    this.rand.nextInt(this.rand.nextInt(this.rand.nextInt(112) + 8) + 8),
                    this.randZ());
            }
        }

        MinecraftForge.EVENT_BUS.post(new DecorateBiomeEvent.Post(this.world, this.rand, this.chunkX, this.chunkZ));
    }

    private void decorateExtra() {
        switch (this.settings.kind) {
            case HILLS:
                for (int i = 0, count = 3 + this.rand.nextInt(6); i < count; ++i) {
                    int x = this.chunkX + this.rand.nextInt(16);
                    int y = this.rand.nextInt(28) + 4;
                    int z = this.chunkZ + this.rand.nextInt(16);

                    if (this.world.getBlock(x, y, z) == Blocks.stone) {
                        this.world.setBlock(x, y, z, Blocks.emerald_ore, 0, 2);
                    }
                }

                for (int i = 0; i < 7; ++i) {
                    this.silverfishGen.generate(
                        this.world,
                        this.rand,
                        this.chunkX + this.rand.nextInt(16),
                        this.rand.nextInt(64),
                        this.chunkZ + this.rand.nextInt(16));
                }

                break;
            case DESERT:
                if (this.rand.nextInt(1000) == 0) {
                    int x = this.randX();
                    int z = this.randZ();
                    new WorldGenDesertWells()
                        .generate(this.world, this.rand, x, this.world.getHeightValue(x, z) + 1, z);
                }

                break;
            case JUNGLE:
                if (this.jungleVines()) {
                    WorldGenVines vines = new WorldGenVines();

                    for (int i = 0; i < 50; ++i) {
                        vines.generate(this.world, this.rand, this.randX(), 64, this.randZ());
                    }
                }

                break;
            default:
                break;
        }
    }

    protected WorldGenerator getRandomWorldGenForTrees() {
        switch (this.settings.kind) {
            case FOREST:
                return this.rand.nextInt(5) == 0 ? this.forestGen
                    : (this.rand.nextInt(10) == 0 ? this.bigTree() : this.treesGen);
            case TAIGA:
                return this.rand.nextInt(3) == 0 ? new WorldGenTaiga164() : new WorldGenTaiga2164();
            case SWAMP:
                return this.swampGen;
            case JUNGLE:
                return this.rand.nextInt(10) == 0 ? this.bigTree()
                    : (this.rand.nextInt(2) == 0 ? new WorldGenShrub164()
                        : (this.rand.nextInt(3) == 0 ? new WorldGenHugeTrees164(10 + this.rand.nextInt(20))
                            : new WorldGenTrees164(4 + this.rand.nextInt(7), 3, true, false)));
            default:
                return this.rand.nextInt(10) == 0 ? this.bigTree() : this.treesGen;
        }
    }

    protected WorldGenerator bigTree() {
        WorldGenBigTree164 generator = this.bigTreeGens[this.biome.biomeID];

        if (generator == null) {
            generator = new WorldGenBigTree164();
            this.bigTreeGens[this.biome.biomeID] = generator;
        }

        return generator;
    }

    private WorldGenerator getRandomWorldGenForGrass() {
        return new WorldGenPlant164(
            Blocks.tallgrass,
            this.settings.kind == Decoration164.Kind.JUNGLE && this.rand.nextInt(4) == 0 ? 2 : 1);
    }

    private boolean decorates(EventType type) {
        return TerrainGen.decorate(this.world, this.rand, this.chunkX, this.chunkZ, type);
    }

    private void scatter(WorldGenerator generator, int count) {
        for (int i = 0; i < count; ++i) {
            generator.generate(this.world, this.rand, this.randX(), this.rand.nextInt(128), this.randZ());
        }
    }

    private void scatterAfterZ(WorldGenerator generator, int count) {
        for (int i = 0; i < count; ++i) {
            int x = this.randX();
            int z = this.randZ();
            generator.generate(this.world, this.rand, x, this.rand.nextInt(128), z);
        }
    }

    private void onSurface(WorldGenerator generator, int count, boolean solid) {
        for (int i = 0; i < count; ++i) {
            int x = this.randX();
            int z = this.randZ();
            int y = solid ? this.world.getTopSolidOrLiquidBlock(x, z) : this.world.getHeightValue(x, z);
            generator.generate(this.world, this.rand, x, y, z);
        }
    }

    private void generateOres() {
        MinecraftForge.ORE_GEN_BUS.post(new OreGenEvent.Pre(this.world, this.rand, this.chunkX, this.chunkZ));
        this.ore(Blocks.dirt, 32, GenerateMinable.EventType.DIRT, 20, 128);
        this.ore(Blocks.gravel, 32, GenerateMinable.EventType.GRAVEL, 10, 128);
        this.ore(Blocks.coal_ore, 16, GenerateMinable.EventType.COAL, 20, 128);
        this.ore(Blocks.iron_ore, 8, GenerateMinable.EventType.IRON, 20, 64);
        this.ore(Blocks.gold_ore, 8, GenerateMinable.EventType.GOLD, 2, 32);
        this.ore(Blocks.redstone_ore, 7, GenerateMinable.EventType.REDSTONE, 8, 16);
        this.ore(Blocks.diamond_ore, 7, GenerateMinable.EventType.DIAMOND, 1, 16);

        if (TerrainGen.generateOre(
            this.world,
            this.rand,
            this.lapisGen,
            this.chunkX,
            this.chunkZ,
            GenerateMinable.EventType.LAPIS)) {
            this.lapisGen.generate(
                this.world,
                this.rand,
                this.chunkX + this.rand.nextInt(16),
                this.rand.nextInt(16) + this.rand.nextInt(16),
                this.chunkZ + this.rand.nextInt(16));
        }

        MinecraftForge.ORE_GEN_BUS.post(new OreGenEvent.Post(this.world, this.rand, this.chunkX, this.chunkZ));
    }

    private void ore(Block block, int size, GenerateMinable.EventType type, int count, int maxY) {
        WorldGenerator generator = new WorldGenMinable(block, size);

        if (TerrainGen.generateOre(this.world, this.rand, generator, this.chunkX, this.chunkZ, type)) {
            for (int i = 0; i < count; ++i) {
                generator.generate(
                    this.world,
                    this.rand,
                    this.chunkX + this.rand.nextInt(16),
                    this.rand.nextInt(maxY),
                    this.chunkZ + this.rand.nextInt(16));
            }
        }
    }
}
