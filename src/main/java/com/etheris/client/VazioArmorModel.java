package com.etheris.client;

import com.etheris.animation.AstraClothMotion;
import com.etheris.item.VazioArmorItem;
import java.util.Map;
import java.util.WeakHashMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;

public final class VazioArmorModel extends GeoModel<VazioArmorItem> {
    // Reuse the verified secondary-motion solver; Astra's geometry/tuning remain unchanged.
    private final Map<LivingEntity, AstraClothMotion> cloth = new WeakHashMap<>();

    @Override public void setCustomAnimations(VazioArmorItem item, long instanceId, AnimationState<VazioArmorItem> state) {
        if (state.getData(DataTickets.EQUIPMENT_SLOT) != EquipmentSlot.CHEST
                || !(state.getData(DataTickets.ENTITY) instanceof LivingEntity wearer)) return;
        float partial = state.getPartialTick();
        AstraClothMotion motion = cloth.computeIfAbsent(wearer, key -> new AstraClothMotion());
        motion.update(new AstraClothMotion.Sample((wearer.tickCount + (double)partial) / 20,
                Mth.lerp(partial, wearer.xo, wearer.getX()), Mth.lerp(partial, wearer.yo, wearer.getY()),
                Mth.lerp(partial, wearer.zo, wearer.getZ()), Mth.rotLerp(partial, wearer.yBodyRotO, wearer.yBodyRot),
                Mth.rotLerp(partial, wearer.yHeadRotO, wearer.yHeadRot),
                wearer.walkAnimation.position(partial) * .6662, wearer.walkAnimation.speed(partial),
                wearer.isCrouching(), wearer.isVisuallySwimming() || wearer.isFallFlying(), !wearer.onGround()));
        pose("cape_root", motion.joint("cape_root"), null, 1);
        for (String column : new String[]{"right", "center", "left"}) {
            AstraClothMotion.Joint parent = null;
            for (int i = 0; i < 4; i++) {
                String name = "cape_" + column + "_" + i;
                AstraClothMotion.Joint joint = motion.joint(name);
                pose(name, joint, parent, .88f); parent = joint;
            }
        }
        for (String panel : new String[]{"robe_right", "robe_left", "stole_front"}) {
            AstraClothMotion.Joint parent = null;
            for (int i = 0; i < 3; i++) {
                String name = AstraClothMotion.segment(panel, i);
                AstraClothMotion.Joint joint = motion.joint(name);
                pose(name, joint, parent, 1); parent = joint;
            }
        }
        // The detached bracer fins are metal: restrained movement, not cloth-like flapping.
        pose("sleeve_right", motion.joint("sleeve_right"), null, .35f);
        pose("sleeve_left", motion.joint("sleeve_left"), null, .35f);
    }

    private void pose(String name, AstraClothMotion.Joint joint, AstraClothMotion.Joint parent, float weight) {
        getBone(name).ifPresent(bone -> {
            bone.setRotX((float)-(joint.pitch() - (parent == null ? 0 : parent.pitch())) * weight);
            bone.setRotY((float)(joint.yaw() - (parent == null ? 0 : parent.yaw())) * weight);
            bone.setRotZ((float)(joint.roll() - (parent == null ? 0 : parent.roll())) * weight);
        });
    }
    @Override public ResourceLocation getModelResource(VazioArmorItem item) {
        return ResourceLocation.fromNamespaceAndPath("etheris", "geo/armor/vazio.geo.json");
    }
    @Override public ResourceLocation getTextureResource(VazioArmorItem item) {
        return ResourceLocation.fromNamespaceAndPath("etheris", "textures/armor/vazio.png");
    }
    @Override public ResourceLocation getAnimationResource(VazioArmorItem item) {
        return ResourceLocation.fromNamespaceAndPath("etheris", "animations/armor/vazio.animation.json");
    }
}
