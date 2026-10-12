package com.carpet.trencher.addition.mixin;

import com.carpet.trencher.addition.tick.ClientTntTickingManager;
import com.carpet.trencher.addition.track.ClientTrackManager;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(PrimedTnt.class)
public abstract class PrimedTntMixinClient {

    @Inject(
            method = "tick" ,
            at = @At("HEAD"),
            cancellable = true
    )
    private void skipClientTick(CallbackInfo ci) {
        PrimedTnt tnt = (PrimedTnt) (Object) this;
        Level level = tnt.level();
        UUID uuid = tnt.getUUID();

        if(level instanceof ClientLevel && !ClientTntTickingManager.getState(uuid)) {
            ci.cancel();
        }
    }

}
