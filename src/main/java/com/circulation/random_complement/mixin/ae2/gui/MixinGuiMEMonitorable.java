package com.circulation.random_complement.mixin.ae2.gui;

import appeng.api.storage.data.IAEItemStack;
import appeng.client.gui.implementations.GuiMEMonitorable;
import appeng.client.me.ItemRepo;
import appeng.container.implementations.ContainerMEMonitorable;
import appeng.container.implementations.ContainerPatternEncoder;
import com.circulation.random_complement.RandomComplement;
import com.circulation.random_complement.client.RCGuiButton;
import com.circulation.random_complement.client.RCSettings;
import com.circulation.random_complement.client.buttonsetting.PatternTermAutoFillPattern;
import com.circulation.random_complement.common.handler.MEHandler;
import com.circulation.random_complement.common.interfaces.PatternTermConfigs;
import com.circulation.random_complement.common.interfaces.SpecialLogic;
import com.circulation.random_complement.common.network.RCConfigButton;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.inventory.Container;
import org.lwjgl.input.Mouse;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;
import java.util.Set;

@Mixin(value = GuiMEMonitorable.class)
public abstract class MixinGuiMEMonitorable extends MixinAEBaseGui implements SpecialLogic {

    @Final
    @Shadow(remap = false)
    private ContainerMEMonitorable monitorableContainer;

    @Shadow(remap = false)
    @Final
    protected ItemRepo repo;

    @Unique
    protected final Set<IAEItemStack> randomComplement$craftableCache = new ObjectOpenHashSet<>();

    @Unique
    protected final Set<IAEItemStack> randomComplement$mergedCache = new ObjectOpenHashSet<>();

    @Unique
    private RCGuiButton randomComplement$AutoFillPattern;

    public MixinGuiMEMonitorable(Container container) {
        super(container);
    }

    @Inject(method = "initGui", at = @At("TAIL"))
    public void initGuiMixin(CallbackInfo ci) {
        if (this.monitorableContainer instanceof ContainerPatternEncoder) {
            this.buttonList.add(this.randomComplement$AutoFillPattern = new RCGuiButton(this.guiLeft - 18, r$getTop() + 20, RCSettings.PatternTermAutoFillPattern, PatternTermAutoFillPattern.CLOSE));
        }
    }

    @Unique
    public int r$getTop() {
        int top = guiTop + 8;
        final int left = guiLeft - 18;
        for (GuiButton guiButton : buttonList) {
            if (guiButton.x != left) continue;
            if (top < guiButton.y) top = guiButton.y;
        }
        return top;
    }

    @Inject(method = "actionPerformed", at = @At(value = "INVOKE", target = "Lorg/lwjgl/input/Mouse;isButtonDown(I)Z", remap = false), cancellable = true)
    protected void actionPerformed(GuiButton btn, CallbackInfo ci) {
        if (this.monitorableContainer instanceof ContainerPatternEncoder) {
            boolean backwards = Mouse.isButtonDown(1);
            if (btn == this.randomComplement$AutoFillPattern) {
                var option = this.randomComplement$AutoFillPattern.getRCSetting();
                RandomComplement.NET_CHANNEL.sendToServer(new RCConfigButton(option, backwards));
                ci.cancel();
            }
        }
    }

    @Inject(method = "drawFG", at = @At("HEAD"), remap = false)
    public void drawFG(int offsetX, int offsetY, int mouseX, int mouseY, CallbackInfo ci) {
        if (this.monitorableContainer instanceof ContainerPatternEncoder) {
            this.randomComplement$AutoFillPattern.set(((PatternTermConfigs) this.monitorableContainer).r$getAutoFillPattern());
        }
    }

    @Unique
    @Override
    public Set<IAEItemStack> r$getList() {
        if (randomComplement$mergedCache.isEmpty()) {
            randomComplement$mergedCache.addAll(MEHandler.getCraftableCacheS());
            randomComplement$mergedCache.addAll(randomComplement$craftableCache);
            MEHandler.getCraftableCacheS().clear();
        }
        return randomComplement$mergedCache;
    }

    @Unique
    @Override
    public void r$setList(Collection<IAEItemStack> list) {
        randomComplement$craftableCache.clear();
        randomComplement$craftableCache.addAll(list);
    }

    @Unique
    @Override
    public void r$addAllList(Collection<IAEItemStack> list) {
        randomComplement$craftableCache.addAll(list);
    }

    @Unique
    @Override
    public ItemRepo r$getRepo() {
        return this.repo;
    }

}