package com.carpet.trencher.addition.mixin;

import com.carpet.trencher.addition.commands.track.TrackManager;
import com.carpet.trencher.addition.commands.track.TrackableEntity;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

//? if >=1.21.6 {
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
//? } else {
/*
import net.minecraft.nbt.CompoundTag;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
*/
//? }


@Mixin(Entity.class)
public abstract class EntityMixin implements TrackableEntity {

    @Shadow
    public abstract UUID getUUID();

    @Unique
    private boolean cta$tracked;

    @Override
    public boolean cta$isTracked(){
        return cta$tracked;
    }

    @Override
    public void cta$setTracked(boolean tracked) {
        this.cta$tracked = tracked;
    }

    @Inject(
            method = "remove",
            at = @At("TAIL")
    )
    private void removeTrackedEntity(Entity.RemovalReason reason, CallbackInfo ci) {
        if (cta$tracked && reason != null && reason.shouldDestroy()){
            TrackManager.cleanup((Entity) (Object)this);
        }
    }

    @Inject(
            method = "saveWithoutId",
            at = @At("TAIL")
    )
    private void saveTracked(
        //? if >= 1.21.6 {
        ValueOutput valueOutput, CallbackInfo ci
        //? } else {
        /*
        CompoundTag compoundTag, CallbackInfoReturnable<CompoundTag> cir
        */
        //? }
    ) {
        if (cta$tracked) {
            //? if >= 1.21.6 {
            valueOutput.putBoolean("cta$tracked", true);
            //? } else {
            /*
            compoundTag.putBoolean("cta$tracked", true);
             */
            //? }
        }
    }

    @Inject(
            method = "load",
            at = @At("TAIL")
    )
    private void loadTracked(
            //? if >= 1.21.6 {
            ValueInput valueInput, CallbackInfo ci
            //? } else {
            /*
            CompoundTag compoundTag, CallbackInfo ci
            */
            //? }
    ) {
        //? if >= 1.21.6 {
        cta$tracked = valueInput.getBooleanOr("cta$tracked", false);
        //? } elif = 1.21.5 {
        /*
        cta$tracked = compoundTag.getBoolean("cta$tracked").orElse(false);
        */
        //? } else {
        /*
        cta$tracked = compoundTag.getBoolean("cta$tracked");
        */
        //? }
    }

}