package com.circulation.random_complement.mixin.ae2.new_patten_gui;

import appeng.api.storage.ITerminalHost;
import appeng.container.implementations.ContainerPatternTerm;
import appeng.container.slot.OptionalSlotFake;
import appeng.container.slot.SlotFakeCraftingMatrix;
import appeng.container.slot.SlotPatternOutputs;
import appeng.container.slot.SlotRestrictedInput;
import com.circulation.random_complement.client.RCSlotFakeCraftingMatrix;
import com.circulation.random_complement.client.RCSlotPatternOutputs;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ContainerPatternTerm.class)
public abstract class MixinContainerPatternTerm extends MixinContainerPatternEncoder {

    public MixinContainerPatternTerm(InventoryPlayer ip, ITerminalHost monitorable) {
        super(ip, monitorable);
    }

    @WrapOperation(method = "<init>", at = @At(value = "INVOKE", target = "Lappeng/container/slot/SlotRestrictedInput;setStackLimit(I)Lnet/minecraft/inventory/Slot;", remap = false))
    public Slot onInit(SlotRestrictedInput instance, int size, Operation<Slot> original) {
        this.craftingSlots = new SlotFakeCraftingMatrix[81];
        this.outputSlots = new OptionalSlotFake[27];
        var output = this.getPart().getInventoryByName("output");
        this.r$craftingSlotGroup = new RCSlotFakeCraftingMatrix[9][];
        this.r$outputSlotGroup = new RCSlotPatternOutputs[9][];

        for (int i = 0; i < 9; i++) {
            var inputGroup = new RCSlotFakeCraftingMatrix[9];
            var outGroup = new RCSlotPatternOutputs[3];
            for (int y = 0; y < 3; ++y) {
                for (int x = 0; x < 3; ++x) {
                    final int c = i * inputGroup.length + x + y * 3;
                    this.addSlotToContainer(this.craftingSlots[c] = (inputGroup[x + y * 3] = new RCSlotFakeCraftingMatrix(this.crafting, c, 18 + x * 18, -76 + y * 18)));
                }

                final int o = i * outGroup.length + y;
                this.addSlotToContainer(this.outputSlots[o] = (outGroup[y] = new RCSlotPatternOutputs(output, this, o, 110, -76 + y * 18, 0, 0, 1)));
                outGroup[y].setRenderDisabled(false);
                outGroup[y].setIIcon(-1);
            }
            this.r$craftingSlotGroup[i] = inputGroup;
            this.r$outputSlotGroup[i] = outGroup;
        }

        return original.call(instance, size);
    }

    @WrapOperation(method = "<init>", at = @At(value = "INVOKE", target = "Lappeng/container/implementations/ContainerPatternTerm;addSlotToContainer(Lnet/minecraft/inventory/Slot;)Lnet/minecraft/inventory/Slot;"))
    public Slot redirectAddSlot(ContainerPatternTerm instance, Slot slot, Operation<Slot> original) {
        if (slot instanceof SlotFakeCraftingMatrix || slot instanceof SlotPatternOutputs) {
            return null;
        }
        return original.call(instance, slot);
    }
}