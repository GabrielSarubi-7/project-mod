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
        com.etheris.entity.ModEntities.TYPES.register(modBus);
        modBus.addListener(com.etheris.entity.ModEntities::attributes);
        ModItems.ITEMS.register(modBus);
        com.etheris.gameplay.FirmamentMenu.TYPES.register(modBus);
        ModBlocks.BLOCKS.register(modBus);
        com.etheris.machine.MachineEntity.TYPES.register(modBus);
        com.etheris.machine.MachineMenu.TYPES.register(modBus);
        com.etheris.world.RuinFeature.FEATURES.register(modBus);
        ModCreativeTabs.TABS.register(modBus);
        EtherEvents.register();
        modBus.addListener((net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent event) -> event.registerBlockEntity(net.neoforged.neoforge.capabilities.Capabilities.ItemHandler.BLOCK, com.etheris.machine.MachineEntity.TYPE.get(), (machine, side) -> machine.inventory));
    }
}
