package com.etheris.client;

import com.etheris.animation.AstraClothMotion;
import com.etheris.item.AstraArmorItem;
import java.util.Map;
import java.util.WeakHashMap;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.constant.DataTickets;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.util.Mth;

public final class AstraArmorModel extends GeoModel<AstraArmorItem> {
    // Each wearer has independent inertia, without retaining entities after they leave the world.
    private final Map<LivingEntity, AstraClothMotion> cloth = new WeakHashMap<>();

    @Override public void setCustomAnimations(AstraArmorItem item, long instanceId, AnimationState<AstraArmorItem> state) {
        if (!(state.getData(DataTickets.ENTITY) instanceof LivingEntity wearer)) return;
        EquipmentSlot slot = state.getData(DataTickets.EQUIPMENT_SLOT);
        if (slot != EquipmentSlot.HEAD && slot != EquipmentSlot.CHEST) return;
        float partial = state.getPartialTick();
        AstraClothMotion motion = cloth.computeIfAbsent(wearer, key -> new AstraClothMotion());
        // GeoArmorRenderer's limb parameters can be zero: use the interpolated wearer instead.
        motion.update(new AstraClothMotion.Sample((wearer.tickCount + (double)partial) / 20,
                Mth.lerp(partial, wearer.xo, wearer.getX()), Mth.lerp(partial, wearer.yo, wearer.getY()),
                Mth.lerp(partial, wearer.zo, wearer.getZ()), Mth.rotLerp(partial, wearer.yBodyRotO, wearer.yBodyRot),
                Mth.rotLerp(partial, wearer.yHeadRotO, wearer.yHeadRot),
                wearer.walkAnimation.position(partial) * .6662, wearer.walkAnimation.speed(partial),
                wearer.isCrouching(), wearer.isVisuallySwimming() || wearer.isFallFlying(), !wearer.onGround()));
        if (slot == EquipmentSlot.HEAD) {
            pose("talisman_right", motion.joint("talisman_right"), null);
            pose("talisman_left", motion.joint("talisman_left"), null);
            return;
        }
        pose("cape_root", motion.joint("cape_root"), null);
        for (String column : new String[]{"right", "center", "left"}) {
            AstraClothMotion.Joint parent = null;
            for (int i = 0; i < 4; i++) {
                String name = "cape_" + column + "_" + i;
                AstraClothMotion.Joint joint = motion.joint(name);
                pose(name, joint, parent); parent = joint;
            }
        }
        for (String panel : new String[]{"robe_right", "robe_left", "stole_front", "stole_back"}) {
            AstraClothMotion.Joint parent = null;
            for (int i = 0; i < 3; i++) {
                String name = AstraClothMotion.segment(panel, i);
                AstraClothMotion.Joint joint = motion.joint(name);
                pose(name, joint, parent); parent = joint;
            }
        }
        pose("sleeve_right", motion.joint("sleeve_right"), null);
        pose("sleeve_left", motion.joint("sleeve_left"), null);
    }

    private void pose(String name, AstraClothMotion.Joint joint, AstraClothMotion.Joint parent) {
        getBone(name).ifPresent(bone -> {
            // Y is up in Gecko geometry: negative X lifts hanging fabric towards the back.
            // Absolute assignment avoids accumulation across wearers and emissive passes.
            bone.setRotX((float)-(joint.pitch() - (parent == null ? 0 : parent.pitch())));
            bone.setRotY((float)(joint.yaw() - (parent == null ? 0 : parent.yaw())));
            bone.setRotZ((float)(joint.roll() - (parent == null ? 0 : parent.roll())));
        });
    }
    @Override public ResourceLocation getModelResource(AstraArmorItem item) {
        return ResourceLocation.fromNamespaceAndPath("etheris", "geo/armor/astra.geo.json");
    }
    @Override public ResourceLocation getTextureResource(AstraArmorItem item) {
        return ResourceLocation.fromNamespaceAndPath("etheris", "textures/armor/astra.png");
    }
    @Override public ResourceLocation getAnimationResource(AstraArmorItem item) {
        return ResourceLocation.fromNamespaceAndPath("etheris", "animations/armor/astra.animation.json");
    }
}
