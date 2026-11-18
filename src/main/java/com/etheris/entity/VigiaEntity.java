package com.etheris.entity;

import com.etheris.content.ModItems;
import java.util.Comparator;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/** First bounded guardian family. Vanilla golem geometry is a temporary visual. */
public final class VigiaEntity extends IronGolem {
    public enum Order { FOLLOW, GUARD, RETURN, PAUSE }
    private UUID owner;
    private BlockPos anchor = BlockPos.ZERO;
    private Order order = Order.FOLLOW;
    private int fuel = 300;
    private int nextAttack;
    public VigiaEntity(EntityType<? extends IronGolem> type, Level level) { super(type, level); setPersistenceRequired(); setPlayerCreated(true); }
    @Override protected void registerGoals() {} // No vanilla village defence, retaliation or random wandering.
    public UUID ownerId() { return owner; }
    public Order order() { return order; }
    public int fuel() { return fuel; }
    public BlockPos anchor() { return anchor; }
    public void bind(UUID player, BlockPos home) { owner = player; anchor = home.immutable(); }
    public Player ownerPlayer() { return owner == null ? null : level().getPlayerByUUID(owner); }
    private boolean active() {
        var player = ownerPlayer();
        return fuel > 0 && order != Order.PAUSE && player != null && player.isAlive() && !player.isSpectator() && distanceToSqr(player) <= 24 * 24
            && level() instanceof ServerLevel server && getUUID().equals(VigiaBonds.get(server).bound(owner));
    }
    public boolean command(Player player, Order next) {
        if (!player.getUUID().equals(owner) || player.isSpectator() || player.level() != level() || distanceToSqr(player) > 64) return false;
        order = next;
        if (next == Order.GUARD) anchor = blockPosition();
        setTarget(null); getNavigation().stop(); return true;
    }
    @Override public boolean canAttack(LivingEntity target) {
        if (!active() || order == Order.RETURN || !(target instanceof Monster) || target instanceof Creeper || !target.isAlive() || isAlliedTo(target)
            || (target instanceof net.minecraft.world.entity.OwnableEntity pet && pet.getOwnerUUID() != null)) return false;
        var center = order == Order.GUARD ? anchor.getCenter() : ownerPlayer().position();
        return target.position().distanceToSqr(center) <= 64 && distanceToSqr(target) <= 100 && target.level() == level();
    }
    @Override public boolean doHurtTarget(Entity target) {
        if (!(target instanceof LivingEntity living) || !canAttack(living) || distanceToSqr(target) > 4 || !getSensing().hasLineOfSight(living)) return false;
        level().broadcastEntityEvent(this, (byte)4);
        return target.hurt(damageSources().mobAttack(this), 4);
    }
    @Override protected void doPush(Entity other) {} // IronGolem otherwise acquires collision targets outside our policy.
    @Override public void aiStep() {
        super.aiStep();
        if (!(level() instanceof ServerLevel server) || tickCount % 10 != 0) return;
        if (owner == null || !getUUID().equals(VigiaBonds.get(server).bound(owner))) { discard(); return; }
        if (!active()) { setTarget(null); getNavigation().stop(); return; }
        if (tickCount % 20 == 0) fuel--;
        if (fuel <= 0) { setTarget(null); getNavigation().stop(); return; }
        var target = getTarget();
        if (target == null || !canAttack(target)) {
            target = level().getEntitiesOfClass(Monster.class, getBoundingBox().inflate(10), this::canAttack).stream().min(Comparator.comparingDouble(this::distanceToSqr)).orElse(null);
            setTarget(target);
        }
        if (target != null) {
            navigate(target.position());
            getLookControl().setLookAt(target, 30, 30);
            if (tickCount >= nextAttack && doHurtTarget(target)) nextAttack = tickCount + 20;
        } else {
            Vec3 destination = order == Order.FOLLOW ? ownerPlayer().position() : Vec3.atBottomCenterOf(anchor);
            if (position().distanceToSqr(destination) > (order == Order.FOLLOW ? 9 : 2)) navigate(destination);
            else { getNavigation().stop(); if (order == Order.RETURN) order = Order.PAUSE; }
        }
    }
    private void navigate(Vec3 destination) {
        // Vanilla's local path region fits inside this loaded-chunk envelope at follow range 16.
        if (level().hasChunksAt(blockPosition().offset(-32, -32, -32), blockPosition().offset(32, 32, 32)))
            getNavigation().moveTo(destination.x, destination.y, destination.z, 1.0);
        else getNavigation().stop();
    }
    @Override public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (level().isClientSide) return InteractionResult.SUCCESS;
        if (!player.getUUID().equals(owner) || player.isSpectator() || distanceToSqr(player) > 64) { player.displayClientMessage(Component.translatable("message.etheris.vigia.not_owner"), true); return InteractionResult.FAIL; }
        var stack = player.getItemInHand(hand);
        if (stack.is(ModItems.ETHERIUM_FRAGMENT)) {
            if (fuel <= 480) { fuel += 120; heal(4); if (!player.isCreative()) stack.shrink(1); }
        } else if (stack.isEmpty() && !player.isShiftKeyDown()) command(player, Order.values()[(order.ordinal() + 1) % Order.values().length]);
        player.displayClientMessage(Component.translatable("message.etheris.vigia.status", Component.translatable("order.etheris." + order.name().toLowerCase(java.util.Locale.ROOT)), fuel), false);
        return InteractionResult.SUCCESS;
    }
    @Override public void die(DamageSource source) {
        if (level() instanceof ServerLevel server && owner != null) VigiaBonds.get(server).release(owner, getUUID());
        super.die(source);
    }
    @Override public void remove(RemovalReason reason) {
        if (reason.shouldDestroy() && level() instanceof ServerLevel server && owner != null) VigiaBonds.get(server).release(owner, getUUID());
        super.remove(reason);
    }
    @Override public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        if (owner != null) tag.putUUID("etherisOwner", owner);
        tag.putLong("etherisAnchor", anchor.asLong()); tag.putInt("etherisOrder", order.ordinal()); tag.putInt("etherisFuel", fuel);
    }
    @Override public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        owner = tag.hasUUID("etherisOwner") ? tag.getUUID("etherisOwner") : null;
        anchor = BlockPos.of(tag.getLong("etherisAnchor"));
        order = Order.values()[Math.clamp(tag.getInt("etherisOrder"), 0, Order.values().length - 1)];
        fuel = Math.clamp(tag.getInt("etherisFuel"), 0, 600);
    }
}
