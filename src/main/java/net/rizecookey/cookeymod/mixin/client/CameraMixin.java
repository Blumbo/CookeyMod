package net.rizecookey.cookeymod.mixin.client;

import net.minecraft.client.Camera;
import net.minecraft.world.entity.Entity;
import net.rizecookey.cookeymod.CookeyMod;
import net.rizecookey.cookeymod.config.AnimationOptions;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public abstract class CameraMixin {
    @Shadow private Entity entity;

    @Shadow private float eyeHeight;

    @Shadow public abstract Entity getEntity();

    @Shadow private float eyeHeightOld;

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    public void disableSneakAnimation(CallbackInfo ci) {
        if (this.getSneakAnimationSpeed() == 0.0) {
            this.eyeHeight = this.getEntity().getEyeHeight();
            this.eyeHeightOld = this.eyeHeight;
            ci.cancel();
        }
    }

    @Redirect(method = "tick", at = @At(value = "FIELD", target = "Lnet/minecraft/client/Camera;eyeHeight:F", opcode = Opcodes.PUTFIELD))
    public void setSneakAnimationSpeed(Camera camera, float value) {
        this.eyeHeight += (this.entity.getEyeHeight() - this.eyeHeight) * 0.5F * (float) this.getSneakAnimationSpeed();
    }

    public double getSneakAnimationSpeed() {
        return CookeyMod.getInstance().getConfig().getCategory(AnimationOptions.class).getSneakAnimationSpeed();
    }
}