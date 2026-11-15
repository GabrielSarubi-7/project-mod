package com.etheris.gameplay;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

public final class EtherData {
    private static final String KEY = "etheris_ether";
    private static final int MAX = 100;
    public static int get(Player player) { return Math.clamp(player.getPersistentData().getInt(KEY), 0, MAX); }
    public static boolean spend(Player player, int amount) {
        int current = get(player);
        if (amount < 0 || current < amount) return false;
        player.getPersistentData().putInt(KEY, current - amount);
        return true;
    }
    public static void add(Player player, int amount) { player.getPersistentData().putInt(KEY, (int)Math.clamp((long)get(player) + amount, 0, MAX)); }
    public static int max() { return MAX; }
    private EtherData() {}
}
