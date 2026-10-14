package com.sxilverr.unchangedworld.mixins;

import net.minecraft.world.biome.BiomeGenBase;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.sxilverr.unchangedworld.world.WorldTypeDefault164;
import com.sxilverr.unchangedworld.world.biome.Biome164;

@Mixin(BiomeGenBase.class)
public abstract class BiomeGenBaseMixin {

    @Inject(method = "getFloatTemperature(III)F", at = @At("HEAD"), cancellable = true)
    private void unchangedworld$flatTemperatureLike164(int x, int y, int z, CallbackInfoReturnable<Float> cir) {
        if (WorldTypeDefault164.current) {
            cir.setReturnValue(Biome164.get((BiomeGenBase) (Object) this).temperature);
        }
    }

    @Inject(method = "func_150559_j()Z", at = @At("HEAD"), cancellable = true)
    private void unchangedworld$snowFlagLike164(CallbackInfoReturnable<Boolean> cir) {
        Biome164 values = Biome164.find((BiomeGenBase) (Object) this);

        if (WorldTypeDefault164.current && values != null) {
            cir.setReturnValue(values.enableSnow);
        }
    }
}
