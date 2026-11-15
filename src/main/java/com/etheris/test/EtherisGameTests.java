package com.etheris.test;
import com.etheris.content.ModBlocks;
import com.etheris.machine.MachineEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder("etheris")
@PrefixGameTestTemplate(false)
public final class EtherisGameTests {
    @GameTest(template = "empty")
    public static void sintonizationRequiresRefinedCrystal(GameTestHelper helper) {
        var pos = new BlockPos(1, 1, 1); helper.setBlock(pos, ModBlocks.AETHER_FOCUS_BLOCK.get());
        var machine = (MachineEntity)helper.getBlockEntity(pos); machine.insert(100);
        var player = helper.makeMockPlayer(net.minecraft.world.level.GameType.SURVIVAL);
        player.getPersistentData().putInt("etheris_unlocked", 4);
        var absolute = helper.absolutePos(pos);
        var hit = new net.minecraft.world.phys.BlockHitResult(net.minecraft.world.phys.Vec3.atCenterOf(absolute), net.minecraft.core.Direction.UP, absolute, false);
        var rune = new net.minecraft.world.item.ItemStack(com.etheris.content.ModItems.RUNE.get());
        machine.getBlockState().useItemOn(rune, helper.getLevel(), player, net.minecraft.world.InteractionHand.MAIN_HAND, hit);
        helper.assertTrue(rune.getCount() == 1 && machine.energy() == 100 && player.getPersistentData().getInt("etheris_unlocked") == 4, "Rune must not bypass Sintonization");
        var crystal = new net.minecraft.world.item.ItemStack(com.etheris.content.ModItems.STABLE_CRYSTAL.get());
        machine.getBlockState().useItemOn(crystal, helper.getLevel(), player, net.minecraft.world.InteractionHand.MAIN_HAND, hit);
        helper.assertTrue(crystal.isEmpty() && machine.energy() == 60 && player.getPersistentData().getInt("etheris_unlocked") == 5, "Stable crystal unlocks next spell for exactly 40 Ether");
        helper.succeed();
    }
    @GameTest(template = "empty")
    public static void processingIsAtomicWhenOutputBlocked(GameTestHelper helper) {
        var pos = new BlockPos(1, 1, 1); helper.setBlock(pos, ModBlocks.PRISMATIC_CRUSHER.get());
        var machine = (MachineEntity)helper.getBlockEntity(pos);
        machine.inventory.insertItem(0, new net.minecraft.world.item.ItemStack(Items.RAW_IRON, 2), false);
        machine.insert(100);
        machine.inventory.setStackInSlot(1, new net.minecraft.world.item.ItemStack(com.etheris.content.ModItems.IRON_DUST.get(), 63));
        helper.assertTrue(!machine.processOne(), "A two-item recipe cannot fit into one free slot");
        helper.assertTrue(machine.energy() == 100 && machine.inventory.getStackInSlot(0).getCount() == 2, "Blocked output must preserve all inputs");
        machine.inventory.extractItem(1, 63, false);
        helper.assertTrue(machine.processOne(), "Recipe resumes when output is free");
        helper.assertTrue(machine.energy() == 70 && machine.inventory.getStackInSlot(0).getCount() == 1 && machine.inventory.getStackInSlot(1).getCount() == 2, "Recipe must exchange exactly its input, energy and result");
        var saved = machine.saveWithFullMetadata(helper.getLevel().registryAccess());
        var restored = new MachineEntity(helper.absolutePos(pos), machine.getBlockState()); restored.loadWithComponents(saved, helper.getLevel().registryAccess());
        helper.assertTrue(restored.energy() == 70 && restored.inventory.getStackInSlot(0).getCount() == 1 && restored.inventory.getStackInSlot(1).getCount() == 2, "Inventory and energy must survive together");
        helper.succeed();
    }
    @GameTest(template = "empty", timeoutTicks = 240)
    public static void hoppersFeedAndDrainForge(GameTestHelper helper) {
        var pos = new BlockPos(2, 2, 2); helper.setBlock(pos, ModBlocks.RUNE_FORGE.get());
        helper.setBlock(pos.above(), net.minecraft.world.level.block.Blocks.HOPPER);
        helper.setBlock(pos.below(), net.minecraft.world.level.block.Blocks.HOPPER);
        var top = (net.minecraft.world.level.block.entity.HopperBlockEntity)helper.getBlockEntity(pos.above());
        top.setItem(0, new net.minecraft.world.item.ItemStack(Items.COPPER_INGOT));
        ((MachineEntity)helper.getBlockEntity(pos)).insert(20);
        helper.runAfterDelay(220, () -> {
            var bottom = (net.minecraft.world.level.block.entity.HopperBlockEntity)helper.getBlockEntity(pos.below());
            helper.assertTrue(bottom.countItem(com.etheris.content.ModItems.RUNE.get()) == 1, "Physical hoppers must transport one input through the forge to one rune");
            helper.assertTrue(top.isEmpty(), "Input hopper must have supplied copper");
            helper.assertTrue(((MachineEntity)helper.getBlockEntity(pos)).energy() == 0, "Automation must pay energy cost");
            helper.succeed();
        });
    }
    @GameTest(template = "empty")
    public static void automationRejectsInvalidOperations(GameTestHelper helper) {
        var pos = new BlockPos(1, 1, 1); helper.setBlock(pos, ModBlocks.RUNE_FORGE.get());
        var machine = (MachineEntity)helper.getBlockEntity(pos);
        var cap = helper.getLevel().getCapability(net.neoforged.neoforge.capabilities.Capabilities.ItemHandler.BLOCK, helper.absolutePos(pos), net.minecraft.core.Direction.UP);
        helper.assertTrue(cap != null, "Automation capability must exist");
        var copper = new net.minecraft.world.item.ItemStack(Items.COPPER_INGOT, 3);
        helper.assertTrue(cap.insertItem(0, copper, true).isEmpty() && machine.inventory.getStackInSlot(0).isEmpty(), "Simulated insertion must not mutate inventory");
        helper.assertTrue(cap.insertItem(1, copper, false).getCount() == 3, "Output insertion must be rejected");
        helper.assertTrue(cap.insertItem(0, new net.minecraft.world.item.ItemStack(Items.DIAMOND), false).getCount() == 1, "Invalid reagent must be rejected");
        cap.insertItem(0, copper, false);
        helper.assertTrue(cap.extractItem(0, 3, false).isEmpty(), "Hoppers must not steal unfinished input");
        helper.assertTrue(!machine.processOne() && machine.inventory.getStackInSlot(0).getCount() == 3, "Unpowered machine must preserve queued input");
        helper.succeed();
    }
    @GameTest(template = "empty")
    public static void nodeAlternatesDestinations(GameTestHelper helper) {
        var center = new BlockPos(2, 1, 2); var a = center.west(); var b = center.east();
        helper.setBlock(center, ModBlocks.DISTRIBUTION_NODE.get()); helper.setBlock(a, ModBlocks.RUNE_FORGE.get()); helper.setBlock(b, ModBlocks.CRYSTAL_REFINERY.get());
        var node = (MachineEntity)helper.getBlockEntity(center); node.insert(40);
        node.linkTo(helper.absolutePos(a)); node.linkTo(helper.absolutePos(b));
        node.distribute(); node.distribute();
        helper.assertTrue(((MachineEntity)helper.getBlockEntity(a)).energy() == 8 && ((MachineEntity)helper.getBlockEntity(b)).energy() == 8 && node.energy() == 24, "Two cycles must serve two consumers and conserve energy");
        helper.succeed();
    }
    @GameTest(template = "empty")
    public static void codexCreatesReadableBook(GameTestHelper helper) {
        var player = helper.makeMockPlayer(net.minecraft.world.level.GameType.SURVIVAL);
        var item = com.etheris.content.ModItems.CODEX.get();
        player.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND, new net.minecraft.world.item.ItemStack(item));
        item.use(helper.getLevel(), player, net.minecraft.world.InteractionHand.MAIN_HAND);
        var book = player.getMainHandItem();
        helper.assertTrue(book.is(Items.WRITTEN_BOOK), "Codex must become a native readable book");
        var contents = book.get(net.minecraft.core.component.DataComponents.WRITTEN_BOOK_CONTENT);
        helper.assertTrue(contents != null && contents.pages().size() == 12, "Codex must contain twelve guide pages");
        helper.succeed();
    }
    @GameTest(template = "empty")
    public static void linkingWandReachesItemHandler(GameTestHelper helper) {
        var pos = new BlockPos(1, 1, 1);
        helper.setBlock(pos, ModBlocks.ETHER_COLLECTOR.get());
        var player = helper.makeMockPlayer(net.minecraft.world.level.GameType.SURVIVAL);
        var wand = new net.minecraft.world.item.ItemStack(com.etheris.content.ModItems.LINK_WAND.get());
        var absolute = helper.absolutePos(pos);
        var hit = new net.minecraft.world.phys.BlockHitResult(net.minecraft.world.phys.Vec3.atCenterOf(absolute), net.minecraft.core.Direction.UP, absolute, false);
        var result = helper.getBlockState(pos).useItemOn(wand, helper.getLevel(), player, net.minecraft.world.InteractionHand.MAIN_HAND, hit);
        helper.assertTrue(result == net.minecraft.world.ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION, "Machine must not swallow linking-wand interaction");
        helper.succeed();
    }
    @GameTest(template = "empty")
    public static void forgeExchangesOnlyWithEnergy(GameTestHelper helper) {
        var pos = new BlockPos(1, 1, 1);
        helper.setBlock(pos, ModBlocks.RUNE_FORGE.get());
        var machine = (MachineEntity)helper.getBlockEntity(pos);
        var player = helper.makeMockPlayer(net.minecraft.world.level.GameType.SURVIVAL);
        var copper = new net.minecraft.world.item.ItemStack(Items.COPPER_INGOT, 2);
        player.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND, copper);
        var absolute = helper.absolutePos(pos);
        var hit = new net.minecraft.world.phys.BlockHitResult(net.minecraft.world.phys.Vec3.atCenterOf(absolute), net.minecraft.core.Direction.UP, absolute, false);
        var state = machine.getBlockState();
        state.useItemOn(copper, helper.getLevel(), player, net.minecraft.world.InteractionHand.MAIN_HAND, hit);
        helper.assertTrue(copper.getCount() == 2, "Unpowered forge must not consume copper");
        machine.insert(20);
        state.useItemOn(copper, helper.getLevel(), player, net.minecraft.world.InteractionHand.MAIN_HAND, hit);
        helper.assertTrue(copper.getCount() == 1 && machine.energy() == 0, "Forge must consume exactly one copper and 20 Ether");
        helper.assertTrue(player.getInventory().countItem(com.etheris.content.ModItems.RUNE.get()) == 1, "Forge must give exactly one rune");
        helper.succeed();
    }
    @GameTest(template = "empty")
    public static void healingCostsOnlyWhenNeeded(GameTestHelper helper) {
        var player = helper.makeMockPlayer(net.minecraft.world.level.GameType.SURVIVAL);
        player.getPersistentData().putInt("etheris_unlocked", 2);
        player.getPersistentData().putInt("etheris_spell", 2);
        com.etheris.gameplay.EtherData.add(player, 100);
        var focus = com.etheris.content.ModItems.AETHER_FOCUS.get();
        helper.assertTrue(!com.etheris.gameplay.Spells.use(player, focus), "Full-health healing must fail without cost");
        helper.assertTrue(com.etheris.gameplay.EtherData.get(player) == 100, "No-op must not consume Ether");
        player.setHealth(10);
        helper.assertTrue(com.etheris.gameplay.Spells.use(player, focus), "Wounded player can heal");
        helper.assertTrue(player.getHealth() == 16 && com.etheris.gameplay.EtherData.get(player) == 80, "Healing restores six health for 20 Ether");
        helper.assertTrue(!com.etheris.gameplay.Spells.use(player, focus), "Cooldown must block repeated cast");
        helper.succeed();
    }
    @GameTest(template = "empty")
    public static void reservoirPersistsAndBoundsEnergy(GameTestHelper helper) {
        var pos = new BlockPos(1, 1, 1);
        helper.setBlock(pos, ModBlocks.AMBER_RESERVOIR.get());
        var machine = (MachineEntity)helper.getBlockEntity(pos);
        helper.assertTrue(machine.insert(1500) == 1000, "Reservoir must cap input at capacity");
        helper.assertTrue(!machine.consume(1001) && machine.energy() == 1000, "Rejected extraction must conserve energy");
        helper.assertTrue(!machine.consume(-1), "Negative extraction must be rejected");
        machine.consume(75);
        var saved = machine.saveWithFullMetadata(helper.getLevel().registryAccess());
        var restored = new MachineEntity(helper.absolutePos(pos), ModBlocks.AMBER_RESERVOIR.get().defaultBlockState());
        restored.loadWithComponents(saved, helper.getLevel().registryAccess());
        helper.assertTrue(restored.energy() == 925, "Energy must survive save and reload");
        helper.assertTrue(ModBlocks.AMBER_RESERVOIR.get().asItem() != Items.AIR, "Machine must be placeable as item");
        helper.succeed();
    }
    @GameTest(template = "empty", timeoutTicks = 160)
    public static void networkConservesEnergy(GameTestHelper helper) {
        var a = new BlockPos(1, 1, 1); var b = new BlockPos(2, 1, 1);
        helper.setBlock(a, ModBlocks.AMBER_RESERVOIR.get()); helper.setBlock(b, ModBlocks.RUNE_FORGE.get());
        var source = (MachineEntity)helper.getBlockEntity(a); var target = (MachineEntity)helper.getBlockEntity(b);
        source.insert(100); target.insert(197);
        helper.assertTrue(source.transferTo(target, 8) == 3, "Transfer must respect destination capacity");
        helper.assertTrue(source.energy() + target.energy() == 297, "Transfer must conserve total energy");
        helper.assertTrue(source.transferTo(source, 8) == 0, "Self transfer must be rejected");
        helper.succeed();
    }
    @GameTest(template = "empty", timeoutTicks = 160)
    public static void collectorGeneratesUnderSky(GameTestHelper helper) {
        var pos = new BlockPos(2, 2, 2);
        helper.setBlock(pos, ModBlocks.ETHER_COLLECTOR.get());
        helper.runAfterDelay(110, () -> {
            helper.assertTrue(((MachineEntity)helper.getBlockEntity(pos)).energy() > 0, "Scheduled collector must collect environmental Ether");
            helper.succeed();
        });
    }
}
