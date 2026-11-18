package com.etheris.gameplay;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredRegister;
import java.util.function.Supplier;

/** Selection stays authoritative on the server; opening never unlocks a spell. */
public final class FirmamentMenu extends AbstractContainerMenu {
    public static final DeferredRegister<MenuType<?>> TYPES = DeferredRegister.create(Registries.MENU, "etheris");
    public static final Supplier<MenuType<FirmamentMenu>> TYPE = TYPES.register("firmament", () -> new MenuType<>(FirmamentMenu::new, FeatureFlags.DEFAULT_FLAGS));
    private final ContainerData data;
    public FirmamentMenu(int id, Inventory inventory) { this(id, inventory, new SimpleContainerData(3)); }
    public static FirmamentMenu server(int id, Inventory inventory) {
        var player = inventory.player;
        return new FirmamentMenu(id, inventory, new ContainerData() {
            public int get(int index) { return switch(index) {
                case 0 -> Math.clamp(player.getPersistentData().getInt("etheris_unlocked"), 0, 7);
                case 1 -> Math.clamp(player.getPersistentData().getInt("etheris_spell"), 0, Math.clamp(player.getPersistentData().getInt("etheris_unlocked"), 0, 7));
                default -> EtherData.get(player);
            }; }
            public void set(int index, int value) {}
            public int getCount() { return 3; }
        });
    }
    private FirmamentMenu(int id, Inventory inventory, ContainerData data) { super(TYPE.get(), id); this.data = data; addDataSlots(data); }
    public int unlocked() { return data.get(0); }
    public int selected() { return data.get(1); }
    public int energy() { return data.get(2); }
    @Override public boolean stillValid(Player player) { return player.isAlive() && !player.isSpectator() && (player.getMainHandItem().is(com.etheris.content.ModItems.AETHER_FOCUS) || player.getOffhandItem().is(com.etheris.content.ModItems.AETHER_FOCUS)); }
    @Override public boolean clickMenuButton(Player player, int id) {
        if (!stillValid(player) || id < 0 || id > unlocked()) return false;
        player.getPersistentData().putInt("etheris_spell", id); broadcastChanges(); return true;
    }
    @Override public ItemStack quickMoveStack(Player player, int index) { return ItemStack.EMPTY; }
}
