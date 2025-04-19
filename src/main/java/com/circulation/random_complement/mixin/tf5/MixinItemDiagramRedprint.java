package com.circulation.random_complement.mixin.tf5;

import cofh.api.item.IPlacementUtilItem;
import cofh.core.util.core.IInitializer;
import cofh.thermalfoundation.item.diagram.ItemDiagram;
import cofh.thermalfoundation.item.diagram.ItemDiagramRedprint;
import com.circulation.random_complement.RCConfig;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ItemDiagramRedprint.class)
public abstract class MixinItemDiagramRedprint extends ItemDiagram implements IInitializer, IPlacementUtilItem {

    /**
     * @author sddsd2332
     * @reason 首先判断玩家是否在蹲下，如果蹲下则判断持有手是否为主手，如果是主手则清除
     * 该修改可以帮助在批量放置TE设备时，蹲下且未能放置TE设备导致红图配置清除问题
     */
    @Redirect(method = "onItemUseFirst", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/EntityPlayer;isSneaking()Z"))
    public boolean addEnumHand(EntityPlayer instance, @Local(ordinal = 0) EnumHand hand) {
        if (RCConfig.TF5.RedDiagramOfTheDeputy) {
            return instance.isSneaking() && hand != EnumHand.OFF_HAND;
        } else {
            return instance.isSneaking();
        }

    }
}
