package com.circulation.random_complement.mixin.botania.ce;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vazkii.botania.client.model.ModelBrewery;
import vazkii.botania.client.render.tile.RenderTileBrewery;
import vazkii.botania.common.block.tile.TileBrewery;

@Mixin(value = ModelBrewery.class, remap = false)
public class MixinModelBrewery {
    @Shadow
    @Final
    ModelRenderer top;
    @Shadow
    @Final
    ModelRenderer pole;
    @Shadow
    @Final
    ModelRenderer bottom;
    @Shadow
    @Final
    ModelRenderer plate;

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void render(RenderTileBrewery render, double time, CallbackInfo ci) {
        rc$render(render, render.brewery, time);
        ci.cancel();
    }

    @Unique
    public void rc$render(RenderTileBrewery render, TileBrewery brewery, double time) {
        float f = 1F / 16F;

        float offset = (float) Math.sin(time / 40) * 0.1F + 0.05F;
        boolean hasTile = brewery != null;
        int plates = hasTile ? brewery.getSizeInventory() - 1 : 7;
        float deg = (float) time / 16F;
        float polerot = -deg * 25.0F;

        GlStateManager.translate(0F, offset, 0F);
        GlStateManager.rotate(polerot, 0F, 1F, 0F);
        if (hasTile && !brewery.getItemHandler().getStackInSlot(0).isEmpty()) {
            GlStateManager.rotate(180F, 1F, 0F, 0F);
            GlStateManager.translate(0, -0.45, 0);
            render.renderItemStack(brewery.getItemHandler().getStackInSlot(0));
            GlStateManager.translate(0, 0.45, 0);
            GlStateManager.rotate(-180F, 1F, 0F, 0F);
        }

        this.pole.render(f);
        this.top.render(f);
        this.bottom.render(f);
        GlStateManager.rotate(-polerot, 0.0F, 1.0F, 0.0F);
        float degper = ((float) Math.PI * 2F) / (float) plates;

        for (int i = 0; i < plates; i++) {
            float offset1 = (float) Math.sin(time / 20 + i * 40F) * 0.2F - 0.2F;
            if (time == -1)
                offset1 = 0F;

            GlStateManager.translate(0F, offset1, 0F);
            if (hasTile && !brewery.getItemHandler().getStackInSlot(i + 1).isEmpty()) {
                float rot = deg * 180F / (float) Math.PI;
                float transX = 0.3125F;
                float transY = 1.06F;
                float transZ = 0.1245F;
                GlStateManager.rotate(rot, 0F, 1F, 0F);
                GlStateManager.translate(transX, transY, transZ);
                GlStateManager.rotate(-90F, 1F, 0F, 0F);
                GlStateManager.translate(0.125, 0.125, 0);
                render.renderItemStack(brewery.getItemHandler().getStackInSlot(i + 1));
                GlStateManager.translate(-0.125, -0.125, 0);
                GlStateManager.rotate(90F, 1F, 0F, 0F);
                GlStateManager.translate(-transX, -transY, -transZ);
                GlStateManager.rotate(-rot, 0F, 1F, 0F);
            }
            plate.rotateAngleY = deg;
            plate.render(f);
            GlStateManager.translate(0F, -offset1, 0F);

            deg += degper;
        }
        GlStateManager.translate(0F, -offset, 0F);
    }
}
