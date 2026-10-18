package com.carpet.trencher.addition.mixin;

import com.carpet.trencher.addition.track.ClientTrackManager;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

//? if >= 1.21.9 {
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//? } else {
/*
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
*/
//? }

import java.util.UUID;

//? if <= 26.1.2 {
@Mixin(LevelRenderer.class)
//? } else {
/*
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.renderer.entity.EntityRenderer;
@Mixin(EntityRenderer.class)
*/
//? }

public abstract class LevelRendererMixin {

    //? if >= 1.21.9 <= 26.1.2 {
    @Inject(
            method = "extractEntity",
            at = @At("RETURN")
    )
    private void setTrackedEntityOutline(Entity entity, float partialTick, CallbackInfoReturnable<EntityRenderState> cir) {
        UUID uuid = entity.getUUID();
        if (!ClientTrackManager.hasColor(uuid)) {
            return;
        }
        EntityRenderState state = cir.getReturnValue();
        state.outlineColor = ClientTrackManager.getColor(uuid);
    }
    //? } elif >= 26.2 {
    /*
    @Inject(
            method = "extractRenderState",
            at = @At("TAIL")
    )
    private void setTrackedEntityOutline(Entity entity, EntityRenderState state, float partialTicks, CallbackInfo ci) {
        UUID uuid = entity.getUUID();
        if (ClientTrackManager.isTracked(uuid)) {
            state.outlineColor = ClientTrackManager.getColor(uuid);
        }
    }
    */
    //? } elif >= 1.21.2 {
    /*
    @ModifyExpressionValue(
            method = "collectVisibleEntities",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/Minecraft;shouldEntityAppearGlowing(Lnet/minecraft/world/entity/Entity;)Z"
            )
    )
    private boolean shouldRenderTrackedOutline(boolean original, @Local Entity entity) {
        return original || ClientTrackManager.hasColor(entity.getUUID());
    }

    @ModifyExpressionValue(
            method = "renderEntities",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/Minecraft;shouldEntityAppearGlowing(Lnet/minecraft/world/entity/Entity;)Z"
            )
    )
    private boolean shouldRenderTrackedOutlineInRender(boolean original, @Local Entity entity) {
        return original || ClientTrackManager.hasColor(entity.getUUID());
    }

    @ModifyExpressionValue(
            method = "renderEntities",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/Entity;getTeamColor()I"
            )
    )
    private int getTrackedOutlineColor(int original, @Local Entity entity) {
        return ClientTrackManager.getColor(entity.getUUID(), original);
    }
    */
    //? } else {
    /*
    @ModifyExpressionValue(
            method = "renderLevel",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/Minecraft;shouldEntityAppearGlowing(Lnet/minecraft/world/entity/Entity;)Z"
            )
    )
    private boolean shouldRenderTrackedOutline(boolean original, @Local Entity entity) {
        return original || ClientTrackManager.hasColor(entity.getUUID());
    }

    @ModifyExpressionValue(
            method = "renderLevel",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/Entity;getTeamColor()I"
            )
    )
    private int getTrackedOutlineColor(int original, @Local Entity entity) {
        return ClientTrackManager.getColor(entity.getUUID(), original);
    }
    */
    //? }

}