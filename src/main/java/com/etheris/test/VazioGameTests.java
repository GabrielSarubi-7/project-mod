package com.etheris.test;

import com.etheris.content.ModItems;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder("etheris")
@PrefixGameTestTemplate(false)
public final class VazioGameTests {
    @GameTest(template="empty")
    public static void vazioEquipmentAndMixedSetPersist(GameTestHelper helper) {
        var player=helper.makeMockPlayer(GameType.SURVIVAL);
        var pieces=new ArmorItem[]{ModItems.VAZIO_HELMET.get(),ModItems.VAZIO_CHESTPLATE.get(),
                ModItems.VAZIO_LEGGINGS.get(),ModItems.VAZIO_BOOTS.get()};
        var slots=java.util.EnumSet.noneOf(EquipmentSlot.class);
        int defense=0;
        for(var piece:pieces) {
            var stack=new ItemStack(piece); stack.setDamageValue(11);
            var restored=ItemStack.parse(helper.getLevel().registryAccess(),stack.save(helper.getLevel().registryAccess())).orElseThrow();
            helper.assertTrue(restored.is(piece)&&restored.getDamageValue()==11,"Vazio piece/damage must survive serialization");
            helper.assertTrue(slots.add(piece.getEquipmentSlot()),"Every Vazio piece needs a different equipment slot");
            player.setItemSlot(piece.getEquipmentSlot(),restored); defense+=piece.getDefense();
            helper.assertTrue(piece.isValidRepairItem(restored,new ItemStack(ModItems.STABLE_CRYSTAL.get())),"Crystal repairs Vazio armor");
        }
        helper.assertTrue(defense==20&&slots.size()==4,"Vazio preview should form a complete 20-defense set");
        player.setItemSlot(EquipmentSlot.HEAD,new ItemStack(ModItems.ASTRA_HELMET.get()));
        helper.assertTrue(player.getItemBySlot(EquipmentSlot.HEAD).is(ModItems.ASTRA_HELMET)
                &&player.getItemBySlot(EquipmentSlot.CHEST).is(ModItems.VAZIO_CHESTPLATE),"Schools may be mixed without replacing other slots");
        helper.succeed();
    }
}
