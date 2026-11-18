package com.etheris.entity;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import java.util.function.Supplier;

public final class ModEntities {
    public static final DeferredRegister<EntityType<?>> TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, "etheris");
    public static final Supplier<EntityType<VigiaEntity>> VIGIA = TYPES.register("vigia", () -> EntityType.Builder.of(VigiaEntity::new, MobCategory.CREATURE).sized(0.84f, 1.62f).clientTrackingRange(8).build("etheris:vigia"));
    public static void attributes(EntityAttributeCreationEvent event) {
        event.put(VIGIA.get(), VigiaEntity.createAttributes().add(Attributes.MAX_HEALTH, 30).add(Attributes.ATTACK_DAMAGE, 4).add(Attributes.MOVEMENT_SPEED, 0.25).add(Attributes.FOLLOW_RANGE, 16).build());
    }
    private ModEntities() {}
}
