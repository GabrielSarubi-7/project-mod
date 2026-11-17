package com.etheris.item;

import com.etheris.content.EclipseArmor;
import java.util.function.Consumer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;

public final class VazioArmorItem extends ArmorItem implements GeoItem {
    private static final RawAnimation ECLIPSE = RawAnimation.begin().thenLoop("animation.vazio.eclipse");
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public VazioArmorItem(Type type, Properties properties) { super(EclipseArmor.VAZIO, type, properties); }

    @Override public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "void", 0, state -> state.setAndContinue(ECLIPSE)));
    }
    @Override public AnimatableInstanceCache getAnimatableInstanceCache() { return cache; }

    @Override public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            private GeoArmorRenderer<VazioArmorItem> renderer;
            @Override public <T extends LivingEntity> HumanoidModel<?> getGeoArmorRenderer(T entity,
                    ItemStack stack, EquipmentSlot slot, HumanoidModel<T> original) {
                if (renderer == null) renderer = new com.etheris.client.VazioArmorRenderer();
                return renderer;
            }
        });
    }
}
