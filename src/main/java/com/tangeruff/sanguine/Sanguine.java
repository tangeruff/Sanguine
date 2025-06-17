package com.tangeruff.sanguine;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.common.MinecraftForge;

import com.tangeruff.sanguine.util.GoldScroller;

@Mod(modid = Sanguine.MODID, version = Sanguine.VERSION)
public class Sanguine {
    public static final String MODID = "sanguine";
    public static final String VERSION = "1.0";

    @EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new GoldScroller());
    }
}
