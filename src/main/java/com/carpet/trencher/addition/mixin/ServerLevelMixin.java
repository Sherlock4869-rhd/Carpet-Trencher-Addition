package com.carpet.trencher.addition.mixin;

import com.carpet.trencher.addition.commands.track.TrackManager;
import com.carpet.trencher.addition.commands.track.EntityTrackingAccess;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerLevel.class)
public class ServerLevelMixin {

    @Inject(
            method = "tickNonPassenger",
            at = @At("TAIL")
    )
    private void getState(Entity entity, CallbackInfo ci) {
        boolean tracked = ((EntityTrackingAccess) entity).cta$isTracked();
        if (tracked) {
            if (!TrackManager.entityTrackers.containsKey(entity.getUUID())) {
                ((EntityTrackingAccess) entity).cta$setTracked(false);
            }
            else {
                TrackManager.updateTracking(entity);
            }
        }
    }

    @Inject(
            method = "tickPassenger",
            at = @At("TAIL")
    )
    private void getPassengerState(Entity entity, Entity entity2, CallbackInfo ci) {
        boolean tracked = ((EntityTrackingAccess) entity2).cta$isTracked();
        if (tracked) {
            if (!TrackManager.entityTrackers.containsKey(entity2.getUUID())) {
                ((EntityTrackingAccess) entity2).cta$setTracked(false);
            }
            else {
                TrackManager.updateTracking(entity2);
            }
        }
    }

}