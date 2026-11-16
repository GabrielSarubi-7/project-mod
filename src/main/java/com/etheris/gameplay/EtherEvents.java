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
        NeoForge.EVENT_BUS.addListener(EtherEvents::clonePlayer);
        NeoForge.EVENT_BUS.addListener(CodexCommands::register);
        NeoForge.EVENT_BUS.addListener(VisualPreviewCommands::register);
    }
    private static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!event.getEntity().level().isClientSide && event.getEntity().tickCount % 40 == 0 && EtherData.get(event.getEntity()) < EtherData.max()) {
            EtherData.add(event.getEntity(), 1);
        }
        var player = event.getEntity();
        if (!player.level().isClientSide && player.tickCount % 40 == 0 && player.getMainHandItem().is(ModItems.AETHER_FOCUS)) {
            int selected = Math.clamp(player.getPersistentData().getInt("etheris_spell"), 0, 7);
            player.displayClientMessage(Component.literal(Spells.NAMES[selected] + " | Éter " + EtherData.get(player) + "/" + EtherData.max()), true);
        }
    }
    private static void clonePlayer(net.neoforged.neoforge.event.entity.player.PlayerEvent.Clone event) {
        for (String key : new String[]{"etheris_ether", "etheris_unlocked", "etheris_spell"})
            event.getEntity().getPersistentData().putInt(key, event.getOriginal().getPersistentData().getInt(key));
    }
    private EtherEvents() {}
}
