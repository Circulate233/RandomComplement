package com.circulation.random_complement.mixin.mmce_old;

import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.api.parts.IPart;
import appeng.container.AEBaseContainer;
import appeng.container.implementations.ContainerInterfaceTerminal;
import appeng.helpers.DualityInterface;
import appeng.helpers.IInterfaceHost;
import appeng.helpers.WirelessTerminalGuiObject;
import appeng.parts.reporting.PartInterfaceTerminal;
import com.circulation.random_complement.common.interfaces.SpecialInvTracker;
import github.kasuminova.mmce.common.tile.MEPatternProvider;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.items.IItemHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

@Mixin(value = ContainerInterfaceTerminal.class)
public class MixinContainerInterfaceTerminal extends AEBaseContainer {

    @Shadow(remap = false)
    private IGrid grid;

    @Shadow(remap = false)
    @Final
    @Mutable
    private Map<IInterfaceHost,Object> diList;

    @Shadow(remap = false)
    @Final
    @Mutable
    private Map<Long, Object> byId;

    @Unique
    public boolean randomComplement$missing = false;

    @Unique
    public int randomComplement$total = 0;

    @Unique
    private static Constructor<?> randomComplement$constructor;

    @Inject(method = "<init>(Lnet/minecraft/entity/player/InventoryPlayer;Lappeng/helpers/WirelessTerminalGuiObject;Z)V",at = @At("TAIL"))
    private void onInit(InventoryPlayer ip, WirelessTerminalGuiObject guiObject, boolean bindInventory, CallbackInfo ci){
        diList = new Object2ObjectOpenHashMap<>();
        byId = new Long2ObjectOpenHashMap<>();
    }

    @Inject(method = "<init>(Lnet/minecraft/entity/player/InventoryPlayer;Lappeng/parts/reporting/PartInterfaceTerminal;)V",at = @At("TAIL"))
    private void onInit(InventoryPlayer ip, PartInterfaceTerminal anchor, CallbackInfo ci){
        diList = new Object2ObjectOpenHashMap<>();
        byId = new Long2ObjectOpenHashMap<>();
    }

    @Inject(method = "<clinit>",at = @At("TAIL"))
    private static void onClinit(CallbackInfo ci){
        try {
            Class<?> clazz = Class.forName("appeng.container.implementations.ContainerInterfaceTerminal$InvTracker");
            randomComplement$constructor = clazz.getDeclaredConstructor(DualityInterface.class, IItemHandler.class, String.class);
        } catch (NoSuchMethodException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        randomComplement$constructor.setAccessible(true);
    }

    public MixinContainerInterfaceTerminal(InventoryPlayer ip, TileEntity myTile, IPart myPart) {
        super(ip, myTile, myPart);
    }

    @Inject(method = "detectAndSendChanges",at = @At(value = "INVOKE", target = "Lappeng/api/networking/IGridNode;isActive()Z",ordinal = 0,remap = false,shift = At.Shift.BY,by = 2))
    public void detectAndSendChangesMixin(CallbackInfo ci) {
        randomComplement$total = 0;
        randomComplement$missing = false;
        for(IGridNode gn : this.grid.getMachines(MEPatternProvider.class)) {
            if (gn.isActive()) {
                IInterfaceHost ih = (IInterfaceHost)gn.getMachine();
                var t = (SpecialInvTracker)this.diList.get(ih);
                if (t == null) {
                    randomComplement$missing = true;
                } else {
                    DualityInterface dual = ih.getInterfaceDuality();
                    if (!t.r$getUnlocalizedName().equals(dual.getTermName())) {
                        randomComplement$missing = true;
                    }
                }

                ++randomComplement$total;
            }
        }
    }

    @Inject(method = "regenList",at = @At(value = "INVOKE", target = "Lappeng/api/networking/IGridNode;isActive()Z",ordinal = 0,shift = At.Shift.BY,by = 2),remap = false)
    public void regenListMixin(CallbackInfo ci) {
        for(IGridNode gn : this.grid.getMachines(MEPatternProvider.class)) {
            if (gn.isActive()) {
                try {
                    IInterfaceHost ih = (IInterfaceHost)gn.getMachine();
                    DualityInterface dual = ih.getInterfaceDuality();
                    IItemHandler patterns = ((MEPatternProvider)ih).getPatterns();
                    String name = dual.getTermName();
                    Object instance = randomComplement$constructor.newInstance(dual,patterns,name);
                    this.diList.put(ih, instance);
                } catch (IllegalAccessException | InvocationTargetException | InstantiationException ignored) {}
            }
        }
    }

    @Redirect(method = "detectAndSendChanges",at = @At(value = "INVOKE", target = "Ljava/util/Map;size()I"))
    public int totalMixin(Map<?,?> instance){
        return (instance.size() - randomComplement$total);
    }

    @ModifyVariable(method = "detectAndSendChanges",at = @At(value = "INVOKE", target = "Ljava/util/Map;size()I",remap = false,shift = At.Shift.BY ,by = -2),name = "missing")
    public boolean missingMixin(boolean missing){
        return randomComplement$missing || missing;
    }
}
