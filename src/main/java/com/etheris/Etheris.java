package com.etheris;

import com.etheris.content.ModBlocks;
import com.etheris.content.ModCreativeTabs;
import com.etheris.content.ModItems;
import com.etheris.gameplay.EtherEvents;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Etheris.MOD_ID)
public final class Etheris {
    public static final String MOD_ID = "etheris";

    public Etheris() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModItems.ITEMS.register(modBus);
        ModBlocks.BLOCKS.register(modBus);
        ModCreativeTabs.TABS.register(modBus);
        EtherEvents.register();
    }
}
