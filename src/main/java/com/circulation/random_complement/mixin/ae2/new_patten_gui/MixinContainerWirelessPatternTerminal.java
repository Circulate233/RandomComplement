package com.circulation.random_complement.mixin.ae2.new_patten_gui;

import appeng.api.storage.ITerminalHost;
import appeng.container.implementations.ContainerWirelessPatternTerminal;
import appeng.container.slot.OptionalSlotFake;
import appeng.container.slot.SlotFakeCraftingMatrix;
import appeng.container.slot.SlotPatternOutputs;
import appeng.container.slot.SlotRestrictedInput;
import appeng.tile.inventory.AppEngInternalInventory;
import appeng.util.inv.IAEAppEngInventory;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ContainerWirelessPatternTerminal.class)
public abstract class MixinContainerWirelessPatternTerminal extends MixinContainerPatternEncoder {

    @Shadow(remap = false)
    protected AppEngInternalInventory output;

    public MixinContainerWirelessPatternTerminal(InventoryPlayer ip, ITerminalHost monitorable) {
        super(ip, monitorable);
    }

    @Redirect(method = "<init>", at = @At(value = "NEW", target = "(Lappeng/util/inv/IAEAppEngInventory;I)Lappeng/tile/inventory/AppEngInternalInventory;", ordinal = 0, remap = false))
    public AppEngInternalInventory newCrafting(IAEAppEngInventory inventory, int size) {
        return new AppEngInternalInventory(inventory, 81) {
            @Override
            public void setSize(int size) {
                if (size < getSlots()) return;
                super.setSize(size);
            }
        };
    }

    @Redirect(method = "<init>", at = @At(value = "NEW", target = "(Lappeng/util/inv/IAEAppEngInventory;I)Lappeng/tile/inventory/AppEngInternalInventory;", ordinal = 1, remap = false))
    public AppEngInternalInventory newOutput(IAEAppEngInventory inventory, int size) {
        return new AppEngInternalInventory(inventory, 27) {
            @Override
            public void setSize(int size) {
                if (size < getSlots()) return;
                super.setSize(size);
            }
        };
    }

    @WrapOperation(method = "<init>", at = @At(value = "INVOKE", target = "Lappeng/container/implementations/ContainerWirelessPatternTerminal;addSlotToContainer(Lnet/minecraft/inventory/Slot;)Lnet/minecraft/inventory/Slot;"))
    public Slot redirectAddSlot(ContainerWirelessPatternTerminal instance, Slot slot, Operation<Slot> original) {
        if (slot instanceof SlotFakeCraftingMatrix || slot instanceof SlotPatternOutputs) {
            return null;
        }
        return original.call(instance, slot);
    }

    @WrapOperation(method = "<init>", at = @At(value = "INVOKE", target = "Lappeng/container/slot/SlotRestrictedInput;setStackLimit(I)Lnet/minecraft/inventory/Slot;", remap = false))
    public Slot onInit(SlotRestrictedInput instance, int size, Operation<Slot> original) {
        this.craftingSlots = new SlotFakeCraftingMatrix[81];
        this.outputSlots = new OptionalSlotFake[27];
        this.r$craftingSlotGroup = new SlotFakeCraftingMatrix[9][];
        this.r$outputSlotGroup = new OptionalSlotFake[9][];

        for (int i = 0; i < 9; i++) {
            var inputGroup = new SlotFakeCraftingMatrix[9];
            var outGroup = new OptionalSlotFake[3];
            for (int y = 0; y < 3; ++y) {
                for (int x = 0; x < 3; ++x) {
                    final int c = i * inputGroup.length + x + y * 3;
                    this.addSlotToContainer(inputGroup[x + y * 3] = (this.craftingSlots[c] = new SlotFakeCraftingMatrix(this.crafting, c, 18 + x * 18, -76 + y * 18)));
                }

                final int o = i * outGroup.length + y;
                this.addSlotToContainer(outGroup[y] = (this.outputSlots[o] = new SlotPatternOutputs(this.output, this, o, 110, -76 + y * 18, 0, 0, 1)));
                outGroup[y].setRenderDisabled(false);
                outGroup[y].setIIcon(-1);
            }
            this.r$craftingSlotGroup[i] = inputGroup;
            this.r$outputSlotGroup[i] = outGroup;
        }

        return original.call(instance, size);
    }
}