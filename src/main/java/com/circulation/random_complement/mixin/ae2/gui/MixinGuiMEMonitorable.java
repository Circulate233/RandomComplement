package com.circulation.random_complement.mixin.ae2.gui;

import appeng.client.gui.AEBaseMEGui;
import appeng.client.gui.implementations.GuiMEMonitorable;
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
import com.circulation.random_complement.common.util.SimpleItem;
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

import java.util.HashSet;
import java.util.Set;

@Mixin(value = GuiMEMonitorable.class)
public abstract class MixinGuiMEMonitorable extends AEBaseMEGui implements SpecialLogic {

    @Final
    @Shadow(remap = false)
    private ContainerMEMonitorable monitorableContainer;

    @Shadow (remap = false)
    protected int jeiOffset;
    @Unique
    public final Set<SimpleItem> randomComplement$craftableCache = new HashSet<>();

    @Unique
    private final Set<SimpleItem> randomComplement$mergedCache = new HashSet<>();

    @Unique
    private RCGuiButton randomComplement$AutoFillPattern;

    public MixinGuiMEMonitorable(Container container) {
        super(container);
    }

    @Inject(method = "initGui",at = @At(value = "INVOKE", target = "Lappeng/client/gui/widgets/GuiImgButton;<init>(IILjava/lang/Enum;Ljava/lang/Enum;)V",ordinal = 3,remap = false))
    public void initGuiMixin(CallbackInfo ci) {
        if (this.monitorableContainer instanceof ContainerPatternEncoder) {
            int offset = this.guiTop + 8 + this.jeiOffset + 100;
            this.buttonList.add(this.randomComplement$AutoFillPattern = new RCGuiButton(this.guiLeft - 18,offset, RCSettings.PatternTermAutoFillPattern,PatternTermAutoFillPattern.CLOSE));
        }
    }

    @Inject(method = "actionPerformed",at = @At(value = "INVOKE", target = "Lorg/lwjgl/input/Mouse;isButtonDown(I)Z",remap = false), cancellable = true)
    protected void actionPerformed(GuiButton btn, CallbackInfo ci) {
        if (this.monitorableContainer instanceof ContainerPatternEncoder) {
            boolean backwards = Mouse.isButtonDown(1);
            if (btn == this.randomComplement$AutoFillPattern) {
                var option = this.randomComplement$AutoFillPattern.getRCSetting();
                RandomComplement.NET_CHANNEL.sendToServer(new RCConfigButton(option,backwards));
                ci.cancel();
            }
        }
    }

    @Inject(method = "drawFG",at = @At("HEAD"),remap = false)
    public void drawFG(int offsetX, int offsetY, int mouseX, int mouseY, CallbackInfo ci) {
        if (this.monitorableContainer instanceof ContainerPatternEncoder) {
            this.randomComplement$AutoFillPattern.set(((PatternTermConfigs) this.monitorableContainer).r$getAutoFillPattern());
        }
    }

    @Unique
    @Override
    public Set<SimpleItem> r$getList() {
        if (randomComplement$mergedCache.isEmpty()){
            randomComplement$mergedCache.addAll(MEHandler.craftableCacheS);
            randomComplement$mergedCache.addAll(randomComplement$craftableCache);
            MEHandler.craftableCacheS.clear();
        }
        return randomComplement$mergedCache;
    }

    @Unique
    @Override
    public void r$setList(Set<SimpleItem> list) {
        randomComplement$craftableCache.clear();
        randomComplement$craftableCache.addAll(list);
    }

    @Unique
    @Override
    public void r$addList(SimpleItem item) {
        randomComplement$craftableCache.add(item);
    }

    @Unique
    @Override
    public void r$addAllList(Set<SimpleItem> list) {
        randomComplement$craftableCache.addAll(list);
    }

}
