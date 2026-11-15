package com.etheris.machine;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
import net.neoforged.neoforge.registries.DeferredRegister;
import java.util.function.Supplier;

public final class MachineMenu extends AbstractContainerMenu {
    public static final DeferredRegister<MenuType<?>> TYPES = DeferredRegister.create(Registries.MENU, "etheris");
    public static final Supplier<MenuType<MachineMenu>> TYPE = TYPES.register("machine", () -> new MenuType<>(MachineMenu::new, FeatureFlags.DEFAULT_FLAGS));
    private final MachineEntity machine;
    private final ContainerData data;

    public MachineMenu(int id, Inventory playerInventory) { this(id, playerInventory, null, new ItemStackHandler(2), new SimpleContainerData(4)); }
    public MachineMenu(int id, Inventory playerInventory, MachineEntity machine) {
        this(id, playerInventory, machine, machine.inventory, new ContainerData() {
            public int get(int index) { return switch (index) { case 0 -> machine.energy(); case 1 -> machine.capacity(); case 2 -> machine.paused() ? 1 : 0; case 3 -> machine.status(); default -> 0; }; }
            public void set(int index, int value) {} // Clients cannot write server energy/control state.
            public int getCount() { return 4; }
        });
    }
    private MachineMenu(int id, Inventory playerInventory, MachineEntity machine, ItemStackHandler items, ContainerData data) {
        super(TYPE.get(), id); this.machine = machine; this.data = data;
        addSlot(new SlotItemHandler(items, 0, 44, 47) {
            @Override public boolean mayPickup(Player player) { return true; }
            @Override public void setChanged() { super.setChanged(); if (machine != null) { machine.setChanged(); machine.wake(); } }
            @Override public ItemStack remove(int amount) {
                var source = items.getStackInSlot(0).copy();
                var removed = source.split(amount);
                items.setStackInSlot(0, source);
                return removed;
            }
        });
        addSlot(new SlotItemHandler(items, 1, 116, 47) {
            @Override public boolean mayPlace(ItemStack stack) { return false; }
        });
        for (int row = 0; row < 3; row++) for (int col = 0; col < 9; col++)
            addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 112 + row * 18));
        for (int col = 0; col < 9; col++) addSlot(new Slot(playerInventory, col, 8 + col * 18, 170));
        addDataSlots(data);
    }
    public int energy() { return data.get(0); }
    public int capacity() { return Math.max(1, data.get(1)); }
    public boolean paused() { return data.get(2) != 0; }
    public int status() { return data.get(3); }
    @Override public boolean stillValid(Player player) {
        return machine == null || (!machine.isRemoved() && machine.getLevel() == player.level()
            && player.level().getBlockEntity(machine.getBlockPos()) == machine
            && player.distanceToSqr(machine.getBlockPos().getCenter()) <= 64
            && player.level().mayInteract(player, machine.getBlockPos()));
    }
    @Override public boolean clickMenuButton(Player player, int id) {
        if (machine == null || !stillValid(player) || player.isSpectator()) return false;
        if (id == 0) machine.setPaused(!machine.paused());
        else if (id == 1) machine.clearLinks();
        else return false;
        broadcastChanges(); return true;
    }
    @Override public ItemStack quickMoveStack(Player player, int index) {
        if (!stillValid(player) || player.isSpectator() || index < 0 || index >= slots.size()) return ItemStack.EMPTY;
        var slot = slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;
        var stack = slot.getItem(); var original = stack.copy();
        if (index < 2) {
            if (!moveItemStackTo(stack, 2, slots.size(), true)) return ItemStack.EMPTY;
        } else if (!moveItemStackTo(stack, 0, 1, false)) return ItemStack.EMPTY;
        if (stack.isEmpty()) slot.setByPlayer(ItemStack.EMPTY); else slot.setChanged();
        slot.onTake(player, stack);
        return original;
    }
}
