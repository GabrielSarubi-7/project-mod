package com.etheris.test;

import com.etheris.content.ModItems;
import com.etheris.entity.*;
import com.etheris.gameplay.EtherData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder("etheris")
@PrefixGameTestTemplate(false)
public final class VigiaGameTests {
    private static final BlockPos FLOOR = new BlockPos(2, 1, 2);
    private static ServerPlayer player(GameTestHelper helper) {
        // Vanilla's mock hardcodes isCreative() to true, even after setGameMode.
        var player = new net.neoforged.neoforge.common.util.FakePlayer(helper.getLevel(), new com.mojang.authlib.GameProfile(java.util.UUID.randomUUID(), "etheris-test"));
        player.setGameMode(net.minecraft.world.level.GameType.SURVIVAL);
        helper.getLevel().addNewPlayer(player);
        helper.assertTrue(!player.isCreative(), "Resource tests require survival semantics");
        for (int x = 0; x < 5; x++) for (int z = 0; z < 5; z++) helper.setBlock(new BlockPos(x, 1, z), Blocks.STONE);
        var pos = helper.absolutePos(new BlockPos(0, 2, 4)).getCenter(); player.setPos(pos.x, pos.y, pos.z);
        EtherData.add(player, 100); return player;
    }
    private static void proof(ServerPlayer player) {
        var advancement = player.server.getAdvancements().get(ResourceLocation.fromNamespaceAndPath("etheris", "prova_transmutacao"));
        // NeoForge intentionally blocks award() for FakePlayer; seed the prerequisite fixture.
        // Actual advancement awarding is covered by ChamberGameTests with a server mock.
        player.getAdvancements().getOrStartProgress(advancement).grantProgress("operated");
    }
    private static InteractionResult summon(GameTestHelper helper, ServerPlayer player) {
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(ModItems.VIGIA_CONTRACT.get()));
        var pos = helper.absolutePos(FLOOR);
        return ModItems.VIGIA_CONTRACT.get().useOn(new UseOnContext(player, InteractionHand.MAIN_HAND, new BlockHitResult(pos.getCenter(), Direction.UP, pos, false)));
    }
    private static VigiaEntity bound(GameTestHelper helper, ServerPlayer player) {
        return (VigiaEntity)helper.getLevel().getEntity(VigiaBonds.get(helper.getLevel()).bound(player.getUUID()));
    }
    @GameTest(template = "empty")
    public static void contractRequiresProofAndPaysOnce(GameTestHelper helper) {
        var player = player(helper);
        helper.assertTrue(summon(helper, player) == InteractionResult.FAIL && EtherData.get(player) == 100 && !player.getMainHandItem().isEmpty(), "No proof: preserve contract and Ether");
        proof(player);
        helper.assertTrue(summon(helper, player) == InteractionResult.SUCCESS && EtherData.get(player) == 40 && player.getMainHandItem().isEmpty(), "Successful creation consumes one contract and sixty Ether");
        var vigia = bound(helper, player);
        helper.assertTrue(vigia != null && vigia.ownerId().equals(player.getUUID()), "Created entity must belong to player");
        helper.assertTrue(summon(helper, player) == InteractionResult.FAIL && EtherData.get(player) == 40 && !player.getMainHandItem().isEmpty(), "Duplicate creation must not charge resources");
        vigia.discard(); helper.succeed();
    }
    @GameTest(template = "empty")
    public static void obstructedCreationDoesNotReserveBond(GameTestHelper helper) {
        var player = player(helper); proof(player); helper.setBlock(FLOOR.above(), Blocks.STONE);
        helper.assertTrue(summon(helper, player) == InteractionResult.FAIL && EtherData.get(player) == 100 && !player.getMainHandItem().isEmpty(), "Obstructed creation must not charge anything");
        helper.assertTrue(VigiaBonds.get(helper.getLevel()).bound(player.getUUID()) == null, "Rejected creation must not reserve a bond");
        helper.setBlock(FLOOR.above(), Blocks.AIR); EtherData.spend(player, 100);
        helper.assertTrue(summon(helper, player) == InteractionResult.FAIL && !player.getMainHandItem().isEmpty(), "Insufficient Ether must preserve the contract");
        helper.succeed();
    }
    @GameTest(template = "empty")
    public static void bondSurvivesUnloadingAndRejectsStaleRelease(GameTestHelper helper) {
        var player = player(helper); proof(player); summon(helper, player); var vigia = bound(helper, player);
        var bonds = VigiaBonds.get(helper.getLevel()); var id = vigia.getUUID();
        vigia.remove(Entity.RemovalReason.UNLOADED_TO_CHUNK);
        helper.assertTrue(id.equals(bonds.bound(player.getUUID())), "Chunk unloading must not free global capacity");
        var restored = VigiaBonds.load(bonds.save(new CompoundTag(), helper.getLevel().registryAccess()), helper.getLevel().registryAccess());
        helper.assertTrue(id.equals(restored.bound(player.getUUID())) && !restored.claim(player.getUUID(), java.util.UUID.randomUUID()), "Saved bond must continue to enforce capacity");
        bonds.release(player.getUUID(), java.util.UUID.randomUUID());
        helper.assertTrue(id.equals(bonds.bound(player.getUUID())), "Stale entity cannot release another entity's bond");
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(ModItems.VIGIA_CONTRACT.get()));
        player.setShiftKeyDown(true); ModItems.VIGIA_CONTRACT.get().use(helper.getLevel(), player, InteractionHand.MAIN_HAND);
        helper.assertTrue(bonds.bound(player.getUUID()) == null, "Explicit contract action must release a lost bond");
        helper.succeed();
    }
    @GameTest(template = "empty")
    public static void ordersAndFuelPersistAndAreOwnerOnly(GameTestHelper helper) {
        var player = player(helper); proof(player); summon(helper, player); var vigia = bound(helper, player);
        var stranger = helper.makeMockPlayer(net.minecraft.world.level.GameType.SURVIVAL);
        helper.assertTrue(!vigia.command(stranger, VigiaEntity.Order.PAUSE), "Stranger must not control sentinel");
        helper.assertTrue(vigia.command(player, VigiaEntity.Order.GUARD), "Owner must be able to set guard anchor");
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(ModItems.ETHERIUM_FRAGMENT.get(), 2));
        vigia.mobInteract(player, InteractionHand.MAIN_HAND);
        helper.assertTrue(vigia.fuel() == 420 && player.getMainHandItem().getCount() == 1, "One fragment buys exactly 120 active seconds");
        var saved = new CompoundTag(); vigia.addAdditionalSaveData(saved);
        var restored = ModEntities.VIGIA.get().create(helper.getLevel()); restored.readAdditionalSaveData(saved);
        helper.assertTrue(restored.ownerId().equals(player.getUUID()) && restored.order() == VigiaEntity.Order.GUARD && restored.fuel() == 420 && restored.anchor().equals(vigia.anchor()), "Owner, guard order, anchor and fuel must survive reload");
        vigia.discard(); helper.succeed();
    }
    @GameTest(template = "empty")
    public static void sentinelNeverAttacksPlayersPetsOrCreepers(GameTestHelper helper) {
        var player = player(helper); proof(player); summon(helper, player); var vigia = bound(helper, player);
        var cow = helper.spawn(EntityType.COW, new BlockPos(3, 2, 2));
        var creeper = helper.spawn(EntityType.CREEPER, new BlockPos(3, 2, 3));
        var zombie = helper.spawn(EntityType.ZOMBIE, new BlockPos(3, 2, 2)); zombie.setNoAi(true);
        helper.assertTrue(!vigia.doHurtTarget(player) && !vigia.doHurtTarget(cow) && !vigia.doHurtTarget(creeper), "Players, animals and creepers are not valid combat targets");
        helper.assertTrue(vigia.canAttack(zombie), "Nearby monster must be eligible");
        vigia.command(player, VigiaEntity.Order.PAUSE);
        helper.assertTrue(!vigia.doHurtTarget(zombie), "Pause must prevent attacks immediately");
        vigia.discard(); cow.discard(); creeper.discard(); zombie.discard(); helper.succeed();
    }
    @GameTest(template = "empty", timeoutTicks = 160)
    public static void sentinelFightsAndPauseStopsUpkeep(GameTestHelper helper) {
        var player = player(helper); proof(player); summon(helper, player); var vigia = bound(helper, player);
        var zombie = helper.spawn(EntityType.ZOMBIE, new BlockPos(3, 2, 2)); zombie.setNoAi(true);
        zombie.setItemSlot(net.minecraft.world.entity.EquipmentSlot.HEAD, new ItemStack(net.minecraft.world.item.Items.IRON_HELMET)); zombie.setHealth(2);
        helper.runAfterDelay(40, () -> {
            helper.assertTrue(!zombie.isAlive(), "Actual server AI must defeat a nearby hostile target");
            helper.assertTrue(vigia.fuel() < 300, "Active work must consume fuel");
            vigia.command(player, VigiaEntity.Order.PAUSE);
            var fuel = vigia.fuel();
            helper.runAfterDelay(50, () -> {
                helper.assertTrue(vigia.fuel() == fuel, "Paused sentinel must not consume fuel");
                vigia.discard(); helper.succeed();
            });
        });
    }
    @GameTest(template = "empty", timeoutTicks = 240)
    public static void followAndReturnUseRealNavigation(GameTestHelper helper) {
        var player = player(helper); proof(player); summon(helper, player); var vigia = bound(helper, player);
        var home = helper.absolutePos(new BlockPos(4, 2, 4));
        var ownerPosition = helper.absolutePos(new BlockPos(0, 2, 0));
        vigia.setPos(home.getX() + 0.5, home.getY(), home.getZ() + 0.5);
        player.setPos(ownerPosition.getX() + 0.5, ownerPosition.getY(), ownerPosition.getZ() + 0.5);
        // Test setup explicitly loads the local navigation envelope, as a nearby real player does.
        for (int x = -2; x <= 2; x++) for (int z = -2; z <= 2; z++) helper.getLevel().getChunk((home.getX() >> 4) + x, (home.getZ() >> 4) + z);
        vigia.command(player, VigiaEntity.Order.GUARD); vigia.command(player, VigiaEntity.Order.FOLLOW);
        double initial = vigia.distanceToSqr(player);
        helper.runAfterDelay(80, () -> {
            helper.assertTrue(vigia.distanceToSqr(player) < initial - 4, "Follow must actually move toward its owner");
            vigia.command(player, VigiaEntity.Order.RETURN);
        });
        helper.runAfterDelay(200, () -> {
            helper.assertTrue(vigia.order() == VigiaEntity.Order.PAUSE && vigia.position().distanceToSqr(net.minecraft.world.phys.Vec3.atBottomCenterOf(home)) <= 2, "Return must reach guard anchor and pause");
            vigia.discard(); helper.succeed();
        });
    }
}
