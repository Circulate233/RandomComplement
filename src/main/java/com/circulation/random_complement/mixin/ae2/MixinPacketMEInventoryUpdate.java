package com.circulation.random_complement.mixin.ae2;

import appeng.api.storage.data.IAEItemStack;
import appeng.client.gui.implementations.GuiMEMonitorable;
import appeng.core.sync.AppEngPacket;
import appeng.core.sync.network.INetworkInfo;
import appeng.core.sync.packets.PacketMEInventoryUpdate;
import appeng.util.item.AEItemStack;
import com.circulation.random_complement.client.CraftableItem;
import com.circulation.random_complement.common.interfaces.SpecialLogic;
import com.circulation.random_complement.common.interfaces.SpecialPacket;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
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

    @Final
    @Shadow
    private List<IAEItemStack> list;

    @Shadow
    @Final
    private ByteBuf data;

    @Inject(method = "<init>(Lio/netty/buffer/ByteBuf;)V",at = @At("TAIL"))
    public void onInit(ByteBuf stream, CallbackInfo ci) {
        if (!this.list.isEmpty()) {
            var item = this.list.get(0).getDefinition();
            if (item.getItem() == Items.APPLE && item.hasTagCompound() && item.getTagCompound().hasKey("randomComplement$id")) {
                this.randomComplement$id = item.getTagCompound().getInteger("randomComplement$id");
                this.list.remove(0);
            }
        }
    }

    @Unique
    private int randomComplement$id;

    @Override
    @Unique
    public int r$getId() {
        return this.randomComplement$id;
    }

    @Override
    @Unique
    public void r$setId(int id) {
        this.randomComplement$id = id;
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setInteger("randomComplement$id",this.randomComplement$id);
        var fakeItem = new ItemStack(Items.APPLE);
        fakeItem.setTagCompound(nbt);
        this.appendItem(AEItemStack.fromItemStack(fakeItem));
    }

    @Shadow
    public abstract void appendItem(IAEItemStack is);

    @Unique
    private Set<Integer> randomComplement$modes = new HashSet<>(Arrays.asList(2));

    @SideOnly(Side.CLIENT)
    @Inject(method = "clientPacketData",at = @At("HEAD"), cancellable = true)
    public void clientPacketDataMixin(INetworkInfo network, AppEngPacket packet, EntityPlayer player, CallbackInfo ci) {
        if (this.r$getId() != 0 && randomComplement$modes.contains(this.r$getId())) {
            switch (this.r$getId()) {
                case 2:
                    GuiScreen gui = Minecraft.getMinecraft().currentScreen;
                    if (gui instanceof GuiMEMonitorable) {
                        ((SpecialLogic) gui).r$setList(this.list.stream()
                                .map(itemStack -> CraftableItem.getInstance(itemStack.getDefinition()))
                                .collect(Collectors.toSet()));
                    }
            }
            ci.cancel();
        }
    }
}
