package com.circulation.random_complement.mixin.shulkertooltip;

import mezz.jei.gui.recipes.RecipesGui;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiShulkerBox;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.GuiScreenEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(targets = "com.zephaniahnoah.shulkertooltip.ShulkerToolTip$EventManager",remap = false)
public class MixinEventManager {

    @Unique
    private GuiContainer randomComplement$FakeGui;
    @Unique
    private Slot randomComplement$FakeSlot;
    @Unique
    private boolean randomComplement$isJEI;

    @Redirect(method = "event",at = @At(value = "INVOKE", target = "Lnet/minecraftforge/client/event/GuiScreenEvent$DrawScreenEvent$Post;getGui()Lnet/minecraft/client/gui/GuiScreen;"))
    public GuiScreen RedGetGUI(GuiScreenEvent.DrawScreenEvent.Post event) {
        if (event.getGui() instanceof RecipesGui){
            if (randomComplement$FakeGui == null){
                InventoryPlayer inv = Minecraft.getMinecraft().player.inventory;
                randomComplement$FakeGui = new GuiShulkerBox(inv,inv);
            }
            this.randomComplement$isJEI = true;
            return randomComplement$FakeGui;
        }
        this.randomComplement$isJEI = false;
        return event.getGui();
    }

    @Redirect(method = "event",at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/inventory/GuiContainer;getSlotUnderMouse()Lnet/minecraft/inventory/Slot;"))
    public Slot RedGetSlot(GuiContainer instance) {
        if (this.randomComplement$isJEI){
            if (randomComplement$FakeSlot == null){
                randomComplement$FakeSlot = new Slot(Minecraft.getMinecraft().player.inventory,0,0,0);
            }
            return randomComplement$FakeSlot;
        }
        return instance.getSlotUnderMouse();
    }

    @Redirect(method = "event",at = @At(value = "INVOKE", target = "Lnet/minecraft/inventory/Slot;getStack()Lnet/minecraft/item/ItemStack;",remap = true))
    public ItemStack RedGetItem(Slot instance) {
        if (this.randomComplement$isJEI){
            RecipesGui gui = (RecipesGui) Minecraft.getMinecraft().currentScreen;
            Object item =  gui.getIngredientUnderMouse();
            if (item instanceof ItemStack itemStack){
                return itemStack;
            } else {
                return ItemStack.EMPTY;
            }
        }
        return instance.getStack();
    }
}
