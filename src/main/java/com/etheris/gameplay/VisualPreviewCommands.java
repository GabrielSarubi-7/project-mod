package com.etheris.gameplay;

import com.etheris.content.ModItems;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

public final class VisualPreviewCommands {
    public static void register(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("etheris").then(Commands.literal("vazio").requires(source -> source.hasPermission(2)).executes(context -> {
            var player=context.getSource().getPlayerOrException();
            for(var item:new net.minecraft.world.item.Item[]{ModItems.VAZIO_HELMET.get(),ModItems.VAZIO_CHESTPLATE.get(),ModItems.VAZIO_LEGGINGS.get(),ModItems.VAZIO_BOOTS.get()}) {
                var stack=new ItemStack(item); if(!player.getInventory().add(stack)) player.drop(stack,false);
            }
            player.sendSystemMessage(Component.translatable("message.etheris.vazio_preview"));
            return 1;
        })));
        event.getDispatcher().register(Commands.literal("etheris").then(Commands.literal("astra").requires(source -> source.hasPermission(2)).executes(context -> {
            var player=context.getSource().getPlayerOrException();
            for(var item:new net.minecraft.world.item.Item[]{ModItems.ASTRA_HELMET.get(),ModItems.ASTRA_CHESTPLATE.get(),ModItems.ASTRA_LEGGINGS.get(),ModItems.ASTRA_BOOTS.get()}) {
                var stack=new ItemStack(item); if(!player.getInventory().add(stack)) player.drop(stack,false);
            }
            player.sendSystemMessage(Component.translatable("message.etheris.astra_preview"));
            return 1;
        })));
        event.getDispatcher().register(Commands.literal("etheris").then(Commands.literal("atelier").requires(source -> source.hasPermission(2)).executes(context -> {
            var player=context.getSource().getPlayerOrException();
            for(var item:new net.minecraft.world.item.Item[]{ModItems.ECLIPSE_HELMET.get(),ModItems.ECLIPSE_CHESTPLATE.get(),ModItems.ECLIPSE_LEGGINGS.get(),ModItems.ECLIPSE_BOOTS.get(),ModItems.AETHER_FOCUS.get()}) {
                var stack=new ItemStack(item); if(!player.getInventory().add(stack)) player.drop(stack,false);
            }
            player.sendSystemMessage(Component.literal("Véu do Eclipse recebido. Equipe as quatro peças e use F5 para observar. Agache + use o foco para abrir o Firmamento."));
            return 1;
        })));
    }
    private VisualPreviewCommands() {}
}
