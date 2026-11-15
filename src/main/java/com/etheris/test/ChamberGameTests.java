package com.etheris.test;

import com.etheris.content.ModBlocks;
import com.etheris.content.ModItems;
import com.etheris.machine.ChamberStructure;
import com.etheris.machine.MachineEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder("etheris")
@PrefixGameTestTemplate(false)
public final class ChamberGameTests {
    private static final BlockPos CENTER = new BlockPos(2, 2, 2);
    private static MachineEntity assemble(GameTestHelper helper) {
        for (int x = -1; x <= 1; x++) for (int z = -1; z <= 1; z++) {
            helper.setBlock(CENTER.offset(x, -1, z), Blocks.POLISHED_DEEPSLATE);
            if (x != 0 || z != 0) helper.setBlock(CENTER.offset(x, 0, z), x != 0 && z != 0 ? Blocks.AMETHYST_BLOCK : Blocks.CUT_COPPER);
        }
        helper.setBlock(CENTER, ModBlocks.TRANSMUTATION_CHAMBER.get());
        return (MachineEntity) helper.getBlockEntity(CENTER);
    }
    @GameTest(template = "empty")
    public static void everyFramePartIsRequired(GameTestHelper helper) {
        var machine = assemble(helper); machine.insert(200);
        machine.inventory.insertItem(0, new ItemStack(ModItems.STABLE_CRYSTAL.get(), 4), false);
        helper.assertTrue(machine.canProcess(), "Complete frame must enable processing");
        for (int y = -1; y <= 0; y++) for (int x = -1; x <= 1; x++) for (int z = -1; z <= 1; z++) {
            if (x == 0 && z == 0) continue;
            var pos = CENTER.offset(x, y, z); var original = helper.getBlockState(pos);
            helper.setBlock(pos, Blocks.AIR);
            helper.assertTrue(!machine.processOne(), "Missing frame or base block must block production");
            helper.assertTrue(machine.energy() == 200 && machine.inventory.getStackInSlot(0).getCount() == 4 && machine.inventory.getStackInSlot(1).isEmpty(), "Broken structure must not consume or duplicate resources");
            helper.setBlock(pos, original);
        }
        helper.setBlock(CENTER.east(), Blocks.WAXED_OXIDIZED_CUT_COPPER);
        helper.assertTrue(machine.processOne(), "Waxed/oxidized copper must remain a valid conductor");
        helper.assertTrue(machine.energy() == 40 && machine.inventory.getStackInSlot(0).isEmpty() && machine.inventory.getStackInSlot(1).is(Items.ENDER_PEARL), "One operation must consume exactly four crystals and 160 Ether");
        helper.assertTrue(machine.transmutationCompleted(), "Successful operation must record discovery proof");
        helper.succeed();
    }
    @GameTest(template = "empty")
    public static void batchAndOutputChecksAreAtomic(GameTestHelper helper) {
        var machine = assemble(helper); machine.insert(200);
        machine.inventory.insertItem(0, new ItemStack(ModItems.STABLE_CRYSTAL.get(), 3), false);
        helper.assertTrue(!machine.processOne() && machine.status() == 8 && machine.energy() == 200, "Short batch must wait without cost");
        machine.inventory.insertItem(0, new ItemStack(ModItems.STABLE_CRYSTAL.get()), false);
        machine.inventory.setStackInSlot(1, new ItemStack(Items.ENDER_PEARL, 16));
        helper.assertTrue(!machine.processOne() && machine.status() == 5 && machine.energy() == 200, "Pearl stack limit is sixteen, not sixty-four");
        machine.inventory.setStackInSlot(1, new ItemStack(Items.DIRT));
        helper.assertTrue(!machine.processOne() && machine.energy() == 200, "Different output must block without cost");
        machine.inventory.setStackInSlot(1, ItemStack.EMPTY);
        machine.setPaused(true);
        helper.assertTrue(!machine.processOne(), "Pause must block the multiblock too");
        machine.setPaused(false);
        helper.assertTrue(machine.processOne(), "Clearing output and resuming must permit exactly one batch");
        var saved = machine.saveWithFullMetadata(helper.getLevel().registryAccess());
        var restored = new MachineEntity(helper.absolutePos(CENTER), machine.getBlockState());
        restored.loadWithComponents(saved, helper.getLevel().registryAccess());
        helper.assertTrue(restored.energy() == 40 && restored.transmutationCompleted() && restored.inventory.getStackInSlot(0).isEmpty() && restored.inventory.getStackInSlot(1).getCount() == 1, "Completed output, proof and costs must survive save/load");
        helper.succeed();
    }
    @GameTest(template = "empty", timeoutTicks = 240)
    public static void queuedChamberResumesAfterRepair(GameTestHelper helper) {
        var machine = assemble(helper); machine.insert(160);
        helper.setBlock(CENTER.offset(-1, -1, -1), Blocks.AIR);
        machine.inventory.insertItem(0, new ItemStack(Items.QUARTZ, 8), false);
        helper.runAfterDelay(110, () -> {
            helper.assertTrue(machine.energy() == 160 && machine.inventory.getStackInSlot(1).isEmpty(), "Broken diagonal base must suspend scheduled production");
            helper.setBlock(CENTER.offset(-1, -1, -1), Blocks.POLISHED_DEEPSLATE);
        });
        helper.runAfterDelay(220, () -> {
            helper.assertTrue(machine.energy() == 80 && machine.inventory.getStackInSlot(0).isEmpty() && machine.inventory.getStackInSlot(1).is(Items.AMETHYST_SHARD), "Queued chamber must detect repairs without adjacent neighbor updates");
            helper.succeed();
        });
    }
    @GameTest(template = "empty")
    public static void structureChecksNeverLoadChunks(GameTestHelper helper) {
        var remote = new BlockPos(20_000_000, 64, 20_000_000);
        helper.assertTrue(!helper.getLevel().hasChunkAt(remote), "Test target must start unloaded");
        helper.assertTrue(!ChamberStructure.formed(helper.getLevel(), remote), "Unloaded frame must be rejected");
        helper.assertTrue(!helper.getLevel().hasChunkAt(remote), "Inspection must not force chunk loading");
        helper.succeed();
    }
    @GameTest(template = "empty")
    public static void discoveryRequiresACompletedOperation(GameTestHelper helper) {
        var machine = assemble(helper);
        var player = helper.makeMockServerPlayerInLevel();
        var advancement = player.server.getAdvancements().get(net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("etheris", "prova_transmutacao"));
        helper.assertTrue(advancement != null, "Discovery advancement must load from resources");
        machine.grantDiscovery(player);
        helper.assertTrue(!player.getAdvancements().getOrStartProgress(advancement).isDone(), "Building alone must not award the operation proof");
        machine.insert(80); machine.inventory.insertItem(0, new ItemStack(Items.QUARTZ, 8), false);
        helper.assertTrue(machine.processOne(), "Proof requires actual production");
        machine.grantDiscovery(player);
        helper.assertTrue(player.getAdvancements().getOrStartProgress(advancement).isDone(), "Visiting an operated chamber must record discovery");
        helper.succeed();
    }
    @GameTest(template = "empty", timeoutTicks = 240)
    public static void completeMultiblockSupportsHoppers(GameTestHelper helper) {
        var machine = assemble(helper); machine.insert(160);
        helper.setBlock(CENTER.above(), Blocks.HOPPER);
        helper.setBlock(CENTER.below(), Blocks.HOPPER);
        var top = (net.minecraft.world.level.block.entity.HopperBlockEntity) helper.getBlockEntity(CENTER.above());
        top.setItem(0, new ItemStack(ModItems.STABLE_CRYSTAL.get(), 4));
        helper.runAfterDelay(220, () -> {
            var bottom = (net.minecraft.world.level.block.entity.HopperBlockEntity) helper.getBlockEntity(CENTER.below());
            helper.assertTrue(top.isEmpty() && bottom.countItem(Items.ENDER_PEARL) == 1, "Hoppers must feed a batch and drain a pearl through the complete assembly");
            helper.assertTrue(machine.energy() == 0 && machine.inventory.getStackInSlot(0).isEmpty() && machine.inventory.getStackInSlot(1).isEmpty(), "Exactly one paid batch must reach the output hopper");
            helper.succeed();
        });
    }
}
