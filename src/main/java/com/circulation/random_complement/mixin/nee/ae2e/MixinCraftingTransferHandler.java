package com.circulation.random_complement.mixin.nee.ae2e;

import appeng.client.me.ItemRepo;
import appeng.container.AEBaseContainer;
import appeng.container.implementations.ContainerCraftingTerm;
import appeng.container.slot.SlotCraftingMatrix;
import appeng.container.slot.SlotFakeCraftingMatrix;
import appeng.core.AELog;
import appeng.core.sync.network.NetworkHandler;
import appeng.core.sync.packets.PacketJEIRecipe;
import appeng.helpers.IContainerCraftingPacket;
import appeng.util.Platform;
import com._0xc4de.ae2exttable.client.gui.GuiMEMonitorableTwo;
import com.github.vfyjxf.nee.config.KeyBindings;
import com.github.vfyjxf.nee.helper.IngredientRequester;
import com.github.vfyjxf.nee.helper.PlatformHelper;
import com.github.vfyjxf.nee.helper.RecipeAnalyzer;
import com.github.vfyjxf.nee.jei.CraftingInfoError;
import com.github.vfyjxf.nee.jei.CraftingTransferHandler;
import com.github.vfyjxf.nee.utils.GuiUtils;
import com.github.vfyjxf.nee.utils.ReflectionHelper;
import mezz.jei.api.gui.IGuiIngredient;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@Mixin(value = CraftingTransferHandler.class,remap = false)
public abstract class MixinCraftingTransferHandler<C extends AEBaseContainer & IContainerCraftingPacket> implements IRecipeTransferHandler<C> {

    @Final
    @Shadow
    private IngredientRequester requester;

    @SuppressWarnings("AmbiguousMixinReference")
    @Inject(method = "transferRecipe", at = @At(value="HEAD"), cancellable=true)
    public void transferRecipeMixin(@Nonnull C container, @Nonnull IRecipeLayout recipeLayout, @Nonnull EntityPlayer player, boolean maxTransfer, boolean doTransfer, CallbackInfoReturnable<Object> cir) {
        GuiScreen parent = GuiUtils.getParentScreen();
        if (parent instanceof com._0xc4de.ae2exttable.client.gui.GuiCraftingTerm craftingTerm) {
            if (!doTransfer) {
                RecipeAnalyzer analyzer = this.randomComplement$createAnalyzer(parent);
                if (analyzer != null) {
                    cir.setReturnValue(new CraftingInfoError(this.initAnalyzer(analyzer, craftingTerm, recipeLayout, player), recipeLayout, true));
                }
            }
            boolean preview = KeyBindings.isPreviewKeyDown();
            boolean nonPreview = KeyBindings.isNonPreviewKeyDown();
            if (!preview && !nonPreview) {
                this.randomComplement$moveItems(container, recipeLayout);
            } else {
                RecipeAnalyzer analyzer = this.randomComplement$createAnalyzer(parent);
                if (analyzer != null) {
                    this.requester.setRequested(false, nonPreview, this.initAnalyzer(analyzer, craftingTerm, recipeLayout, player).analyzeRecipe(recipeLayout));
                    this.requester.requestNext();
                }
            }
        }
    }

    @Shadow
    protected abstract RecipeAnalyzer initAnalyzer(@Nonnull RecipeAnalyzer analyzer, @Nonnull GuiContainer craftingTerm, @Nonnull IRecipeLayout recipeLayout, @Nonnull EntityPlayer player);

    @Unique
    private RecipeAnalyzer randomComplement$createAnalyzer(@Nonnull GuiScreen screen) {
        if (screen instanceof GuiMEMonitorableTwo term) {
            Supplier<ItemRepo> repoSupplier = () -> ReflectionHelper.getFieldValue(GuiMEMonitorableTwo.class, term, "repo");
            return new RecipeAnalyzer(term, false, repoSupplier);
        }
        return null;
    }

    @Unique
    private void randomComplement$moveItems(AEBaseContainer container, IRecipeLayout recipeLayout) {
        Map<Integer, ? extends IGuiIngredient<ItemStack>> ingredients = recipeLayout.getItemStacks().getGuiIngredients();
        NBTTagCompound recipe = new NBTTagCompound();
        int slotIndex = 0;

        for(Map.Entry<Integer, ? extends IGuiIngredient<ItemStack>> ingredientEntry : ingredients.entrySet()) {
            IGuiIngredient<ItemStack> ingredient = ingredientEntry.getValue();
            if (ingredient.isInput()) {
                for(Slot slot : container.inventorySlots) {
                    if ((slot instanceof SlotCraftingMatrix || slot instanceof SlotFakeCraftingMatrix) && slot.getSlotIndex() == slotIndex) {
                        NBTTagList tags = new NBTTagList();
                        List<ItemStack> list = new ArrayList<>();
                        ItemStack displayed = ingredient.getDisplayedIngredient();
                        if (displayed != null && !displayed.isEmpty()) {
                            list.add(displayed);
                        }

                        for(ItemStack stack : ingredient.getAllIngredients()) {
                            if (Platform.isRecipePrioritized(stack)) {
                                list.add(0, stack);
                            } else {
                                list.add(stack);
                            }
                        }

                        for(ItemStack is : list) {
                            NBTTagCompound tag = new NBTTagCompound();
                            is.writeToNBT(tag);
                            tags.appendTag(tag);
                        }

                        recipe.setTag("#" + slot.getSlotIndex(), tags);
                        break;
                    }
                }

                ++slotIndex;
            }
        }

        try {
            if (!(container instanceof ContainerCraftingTerm) && !PlatformHelper.isWirelessContainer(container)) {
                NetworkHandler.instance().sendToServer(new PacketJEIRecipe(recipe));
            }
        } catch (IOException e) {
            AELog.debug(e);
        }

    }
}
