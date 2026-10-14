package com.sxilverr.unchangedworld;

import net.minecraft.init.Blocks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.terraingen.BiomeEvent;
import net.minecraftforge.event.world.WorldEvent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sxilverr.unchangedworld.world.IntegratedConfig164;
import com.sxilverr.unchangedworld.world.WorldTypeDefault164;
import com.sxilverr.unchangedworld.world.WorldTypeIntegrated164;
import com.sxilverr.unchangedworld.world.gen.Generation164;

import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.Event.Result;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

@Mod(
    modid = UnchangedWorld.MODID,
    version = Tags.VERSION,
    name = "Unchanged World",
    acceptedMinecraftVersions = "[1.7.10]",
    guiFactory = "com.sxilverr.unchangedworld.client.ConfigGuiFactory164")
public class UnchangedWorld {

    public static final String MODID = "unchangedworld";
    public static final Logger LOG = LogManager.getLogger(MODID);

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        IntegratedConfig164.load(event.getSuggestedConfigurationFile());
        WorldTypeDefault164 defaultType = new WorldTypeDefault164();
        WorldTypeIntegrated164 integratedType = new WorldTypeIntegrated164();
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.TERRAIN_GEN_BUS.register(this);
        FMLCommonHandler.instance()
            .bus()
            .register(this);
        LOG.info(
            "Registered world types '{}' with id {} and '{}' with id {}",
            defaultType.getWorldTypeName(),
            defaultType.getWorldTypeID(),
            integratedType.getWorldTypeName(),
            integratedType.getWorldTypeID());
    }

    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (MODID.equals(event.modID)) {
            IntegratedConfig164.reload();
        }
    }

    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event) {
        if (event.world.provider.dimensionId == 0) {
            WorldTypeDefault164.current = WorldTypeDefault164.isClimate164(
                event.world.getWorldInfo()
                    .getTerrainType());
        }
    }

    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload event) {
        if (event.world.provider.dimensionId == 0) {
            WorldTypeDefault164.current = false;
        }
    }

    @SubscribeEvent
    public void onVillageBlock(BiomeEvent.GetVillageBlockID event) {
        if (event.original == Blocks.water && Generation164.isActive()) {
            event.replacement = Blocks.flowing_water;
            event.setResult(Result.DENY);
        }
    }
}
