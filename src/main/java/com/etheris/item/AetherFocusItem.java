package com.etheris.item;

import com.etheris.gameplay.EtherData;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public final class AetherFocusItem extends Item {
    public AetherFocusItem(Properties properties) { super(properties); }
    @Override public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide) return InteractionResultHolder.success(stack);
        if (!EtherData.spend(player, 8)) {
            player.displayClientMessage(Component.literal("O foco está sem Éter suficiente (8 necessários)."), true);
            return InteractionResultHolder.fail(stack);
        }
        ServerLevel server = (ServerLevel) level;
        server.sendParticles(ParticleTypes.END_ROD, player.getX(), player.getEyeY(), player.getZ(), 18, .5, .5, .5, .08);
        for (Entity entity : level.getEntities(player, player.getBoundingBox().inflate(4), e -> e instanceof LivingEntity && e != player)) {
            entity.hurt(level.damageSources().indirectMagic(player, player), 4.0f);
        }
        player.getCooldowns().addCooldown(this, 20);
        player.displayClientMessage(Component.literal("Faísca Astral conjurada. Éter: " + EtherData.get(player) + "/" + EtherData.max()), true);
        return InteractionResultHolder.success(stack);
    }
}
