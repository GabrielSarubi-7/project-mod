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

public final class AstraArmorItem extends ArmorItem implements GeoItem {
    private static final RawAnimation ORBIT = RawAnimation.begin().thenLoop("animation.astra.orbit");
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public AstraArmorItem(Type type, Properties properties) {
        super(EclipseArmor.ASTRA, type, properties);
    }

    @Override public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "firmament", 0, state -> state.setAndContinue(ORBIT)));
    }

    @Override public AnimatableInstanceCache getAnimatableInstanceCache() { return cache; }

    // GeckoLib invokes this provider on the client only. Do not construct renderers in item fields.
    @Override public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            private GeoArmorRenderer<AstraArmorItem> renderer;
            @Override public <T extends LivingEntity> HumanoidModel<?> getGeoArmorRenderer(T entity, ItemStack stack, EquipmentSlot slot, HumanoidModel<T> original) {
                if (renderer == null) renderer = new com.etheris.client.AstraArmorRenderer();
                return renderer;
            }
        });
    }
}
