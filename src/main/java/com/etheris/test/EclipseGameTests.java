package com.etheris.test;

import com.etheris.content.ModItems;
import net.minecraft.gametest.framework.*;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.*;
import net.neoforged.neoforge.gametest.*;

@GameTestHolder("etheris")
@PrefixGameTestTemplate(false)
public final class EclipseGameTests {
    @GameTest(template="empty")
    public static void eclipseEquipmentSurvivesSerialization(GameTestHelper helper) {
        var player=helper.makeMockPlayer(net.minecraft.world.level.GameType.SURVIVAL);
        var pieces=new ArmorItem[]{ModItems.ECLIPSE_HELMET.get(),ModItems.ECLIPSE_CHESTPLATE.get(),ModItems.ECLIPSE_LEGGINGS.get(),ModItems.ECLIPSE_BOOTS.get()};
        int defense=0;
        for(var piece:pieces) {
            var stack=new ItemStack(piece); stack.setDamageValue(7);
            var restored=ItemStack.parse(helper.getLevel().registryAccess(),stack.save(helper.getLevel().registryAccess())).orElseThrow();
            helper.assertTrue(restored.is(piece)&&restored.getDamageValue()==7,"Armor item and durability must survive save/load");
            player.setItemSlot(piece.getEquipmentSlot(),restored); defense+=piece.getDefense();
        }
        helper.assertTrue(defense==20&&player.getItemBySlot(EquipmentSlot.HEAD).is(ModItems.ECLIPSE_HELMET),"Four distinct equipment slots must form a complete 20-defense set");
        helper.assertTrue(ModItems.ECLIPSE_CHESTPLATE.get().isValidRepairItem(new ItemStack(ModItems.ECLIPSE_CHESTPLATE.get()),new ItemStack(ModItems.STABLE_CRYSTAL.get())),"Stable crystals must repair the preview armor");
        helper.succeed();
    }
}
