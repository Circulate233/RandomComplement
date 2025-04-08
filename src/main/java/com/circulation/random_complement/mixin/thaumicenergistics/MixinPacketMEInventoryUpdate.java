package com.circulation.random_complement.mixin.thaumicenergistics;

import appeng.api.storage.data.IAEItemStack;
import appeng.core.sync.AppEngPacket;
import appeng.core.sync.network.INetworkInfo;
import appeng.core.sync.packets.PacketMEInventoryUpdate;
import com.circulation.random_complement.client.CraftableItem;
import com.circulation.random_complement.common.interfaces.SpecialLogic;
import com.circulation.random_complement.common.interfaces.SpecialPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mixin(value = PacketMEInventoryUpdate.class,remap = false)
public abstract class MixinPacketMEInventoryUpdate implements SpecialPacket {

    @Shadow
    @Final
    private List<IAEItemStack> list;

    @Unique
    private Set<Integer> randomComplement$modes = new HashSet<>(Arrays.asList(3));

    @SideOnly(Side.CLIENT)
    @Inject(method = "clientPacketData",at = @At("HEAD"), cancellable = true)
    public void clientPacketDataMixin(INetworkInfo network, AppEngPacket packet, EntityPlayer player, CallbackInfo ci) {
        if (this.r$getId() != 0 && randomComplement$modes.contains(this.r$getId())) {
            switch (this.r$getId()) {
                case 3:
                    GuiScreen guiS = Minecraft.getMinecraft().currentScreen;
                    if (guiS instanceof SpecialLogic gui) {
                        gui.r$addAllList(this.list.stream()
                                .map(itemStack -> CraftableItem.getInstance(itemStack.getDefinition()))
                                .collect(Collectors.toSet()));
                    }
            }
            ci.cancel();
        }
    }

}
