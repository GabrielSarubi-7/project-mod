package com.etheris.gameplay;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

public final class EtherData {
    private static final String KEY = "etheris_ether";
    private static final int MAX = 100;
    public static int get(Player player) { return player.getPersistentData().getInt(KEY); }
    public static boolean spend(Player player, int amount) {
        int current = get(player);
        if (current < amount) return false;
        player.getPersistentData().putInt(KEY, current - amount);
        return true;
    }
    public static void add(Player player, int amount) { player.getPersistentData().putInt(KEY, Math.min(MAX, get(player) + amount)); }
    public static int max() { return MAX; }
    private EtherData() {}
}
