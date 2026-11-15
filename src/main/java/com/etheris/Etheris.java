package com.etheris;

import com.etheris.content.ModBlocks;
import com.etheris.content.ModCreativeTabs;
import com.etheris.content.ModItems;
import com.etheris.gameplay.EtherEvents;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(Etheris.MOD_ID)
public final class Etheris {
    public static final String MOD_ID = "etheris";

    public Etheris(IEventBus modBus) {
        ModItems.ITEMS.register(modBus);
        ModBlocks.BLOCKS.register(modBus);
        com.etheris.machine.MachineEntity.TYPES.register(modBus);
        com.etheris.world.RuinFeature.FEATURES.register(modBus);
        ModCreativeTabs.TABS.register(modBus);
        EtherEvents.register();
    }
}
