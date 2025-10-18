package com.circulation.random_complement.mixin.ae2.new_patten_gui;

import appeng.api.storage.ITerminalHost;
import appeng.container.implementations.ContainerMEMonitorable;
import appeng.container.implementations.ContainerPatternEncoder;
import appeng.container.slot.IOptionalSlotHost;
import appeng.container.slot.OptionalSlotFake;
import appeng.container.slot.SlotFakeCraftingMatrix;
import appeng.parts.reporting.AbstractPartEncoder;
import com.circulation.random_complement.common.interfaces.RCPatternEncoder;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraftforge.items.IItemHandler;
import org.spongepowered.asm.mixin.Intrinsic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(value = ContainerPatternEncoder.class, remap = false)
public abstract class MixinContainerPatternEncoder extends ContainerMEMonitorable implements RCPatternEncoder, IOptionalSlotHost {

    @Shadow
    protected SlotFakeCraftingMatrix[] craftingSlots;

    @Shadow
    protected OptionalSlotFake[] outputSlots;

    @Shadow
    protected IItemHandler crafting;

    @Unique
    protected SlotFakeCraftingMatrix[][] r$craftingSlotGroup;

    @Unique
    protected OptionalSlotFake[][] r$outputSlotGroup;

    public MixinContainerPatternEncoder(InventoryPlayer ip, ITerminalHost monitorable) {
        super(ip, monitorable);
    }

    @Intrinsic
    public void setRCSlot(int i) {
        if (!isRCPatternEncoder()) return;
        for (int j = 0; j < 9; j++) {
            if (j == i) {
                for (int y = 0; y < 3; ++y) {
                    for (int x = 0; x < 3; ++x) {
                        r$craftingSlotGroup[j][x + y * 3].xPos = 18 + x * 18;
                    }
                    r$outputSlotGroup[j][y].xPos = 110;
                }
            } else {
                for (var slot : r$craftingSlotGroup[j]) {
                    slot.xPos = -9000;
                }
                for (var slot : r$outputSlotGroup[j]) {
                    slot.xPos = -9000;
                }
            }
        }
    }

    @Shadow
    public abstract AbstractPartEncoder getPart();

    @Intrinsic
    public final boolean isRCPatternEncoder() {
        return this.craftingSlots.length == 81;
    }
}