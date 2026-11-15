package com.etheris.gameplay;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;

public final class Spells {
    public static final String[] NAMES = {"Faísca Astral", "Olho do Firmamento", "Toque Vital", "Transmutar Fragmento", "Impulso de Fluxo", "Lança Fulgente", "Semente Quimérica", "Liga de Éter"};
    private static final int[] COST = {8, 15, 20, 15, 10, 18, 12, 25};
    public static boolean use(Player player, Item focus) {
        var level = (ServerLevel)player.level();
        int unlocked = Math.clamp(player.getPersistentData().getInt("etheris_unlocked"), 0, 7);
        int spell = Math.clamp(player.getPersistentData().getInt("etheris_spell"), 0, unlocked);
        if (player.isShiftKeyDown()) {
            spell = (spell + 1) % (unlocked + 1);
            player.getPersistentData().putInt("etheris_spell", spell);
            player.displayClientMessage(Component.literal(NAMES[spell] + " — " + COST[spell] + " Éter"), true);
            return true;
        }
        if (player.getCooldowns().isOnCooldown(focus)) return false;
        var offhand = player.getOffhandItem();
        if (spell == 2 && player.getHealth() >= player.getMaxHealth()) return false;
        if ((spell == 3 && !offhand.is(Items.COBBLESTONE)) || (spell == 7 && (!offhand.is(Items.RAW_IRON) || offhand.getCount() < 1))) {
            player.displayClientMessage(Component.literal("Reagente na mão secundária: " + (spell == 3 ? "pedregulho" : "ferro bruto")), true); return false;
        }
        var cropHit = player.pick(5, 0, false);
        net.minecraft.core.BlockPos cropPos = cropHit instanceof net.minecraft.world.phys.BlockHitResult block ? block.getBlockPos() : player.blockPosition();
        var cropState = level.getBlockState(cropPos);
        if (spell == 6 && (!(cropState.getBlock() instanceof net.minecraft.world.level.block.CropBlock crop) || crop.isMaxAge(cropState) || !level.mayInteract(player, cropPos))) return false;
        if (!EtherData.spend(player, COST[spell])) { player.displayClientMessage(Component.literal("Éter insuficiente. Recarregue no Foco de Vinculação."), true); return false; }
        switch (spell) {
            case 0, 5 -> {
                double range = spell == 5 ? 16 : 6;
                var origin = player.getEyePosition(); var end = origin.add(player.getLookAngle().scale(range));
                var wall = level.clip(new ClipContext(origin, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));
                if (wall.getType() != HitResult.Type.MISS) end = wall.getLocation();
                LivingEntity nearest = null; double distance = origin.distanceToSqr(end);
                for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(range), e -> e instanceof Enemy && e.isAlive())) {
                    var hit = entity.getBoundingBox().inflate(.25).clip(origin, end);
                    if (hit.isPresent() && origin.distanceToSqr(hit.get()) < distance) { nearest = entity; distance = origin.distanceToSqr(hit.get()); }
                }
                if (nearest != null) nearest.hurt(level.damageSources().indirectMagic(player, player), spell == 5 ? 8 : 4);
                for (int i = 1; i <= 16; i++) { var point = origin.lerp(end, i / 16.0); level.sendParticles(ParticleTypes.END_ROD, point.x, point.y, point.z, 1, 0, 0, 0, 0); }
            }
            case 1 -> player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 1200, 0));
            case 2 -> player.heal(6);
            case 3, 7 -> { offhand.shrink(1); var result = new ItemStack(spell == 3 ? Items.STONE : Items.IRON_INGOT); if (!player.getInventory().add(result)) player.drop(result, false); }
            case 4 -> { player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 200, 1)); player.addEffect(new MobEffectInstance(MobEffects.JUMP, 200, 0)); }
            case 6 -> { var crop = (net.minecraft.world.level.block.CropBlock)cropState.getBlock(); level.setBlock(cropPos, crop.getStateForAge(crop.getAge(cropState) + 1), 3); }
        }
        player.getCooldowns().addCooldown(focus, 30);
        player.displayClientMessage(Component.literal(NAMES[spell] + " | Éter " + EtherData.get(player) + "/100"), true);
        return true;
    }
    private Spells() {}
}
