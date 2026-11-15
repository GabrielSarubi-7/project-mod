package com.etheris.gameplay;

import com.etheris.content.ModBlocks;
import com.etheris.content.ModItems;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

public final class EtherEvents {
    public static void register() {
        NeoForge.EVENT_BUS.addListener(EtherEvents::onPlayerTick);
        NeoForge.EVENT_BUS.addListener(EtherEvents::onBlockUse);
    }
    private static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!event.getEntity().level().isClientSide && event.getEntity().tickCount % 40 == 0 && EtherData.get(event.getEntity()) < EtherData.max()) {
            EtherData.add(event.getEntity(), 1);
        }
    }
    private static void onBlockUse(PlayerInteractEvent.RightClickBlock event) {
        if (event.getLevel().isClientSide || event.getHand() != net.minecraft.world.InteractionHand.MAIN_HAND) return;
        if (!event.getItemStack().is(ModItems.ETHERIUM_FRAGMENT)) return;
        if (event.getLevel().getBlockState(event.getPos()).is(ModBlocks.ETHER_COLLECTOR)) {
            EtherData.add(event.getEntity(), 15);
            event.getItemStack().shrink(1);
            event.getEntity().displayClientMessage(Component.literal("Éter condensado: " + EtherData.get(event.getEntity()) + "/" + EtherData.max()), true);
            event.setCanceled(true);
        }
    }
    private EtherEvents() {}
}
