package com.circulation.random_complement.mixin.ae2fc;

import appeng.api.storage.data.IAEItemStack;
import appeng.core.sync.AppEngPacket;
import appeng.core.sync.network.INetworkInfo;
import appeng.core.sync.packets.PacketMEInventoryUpdate;
import com.circulation.random_complement.client.CraftableItem;
import com.glodblock.github.client.GuiUltimateEncoder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;

@Mixin(value = PacketMEInventoryUpdate.class,remap = false)
public class MixinPacketMEInventoryUpdate {

    @Shadow
    @Final
    private List<IAEItemStack> list;

    @SideOnly(Side.CLIENT)
    @Inject(method = "clientPacketData",at = @At("TAIL"))
    public void clientPacketData(INetworkInfo network, AppEngPacket packet, EntityPlayer player, CallbackInfo ci) throws NoSuchFieldException, IllegalAccessException {
        GuiScreen gui = Minecraft.getMinecraft().currentScreen;
        if (gui instanceof GuiUltimateEncoder){
            Field craftableCacheField = GuiUltimateEncoder.class.getDeclaredField("randomComplement$craftableCache");
            craftableCacheField.setAccessible(true);
            craftableCacheField.set(gui,this.list.stream()
                    .map(itemStack -> new CraftableItem(itemStack.getDefinition()))
                    .collect(Collectors.toSet()));
        }
    }

}
