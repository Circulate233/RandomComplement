package com.circulation.random_complement.client;

import com.circulation.random_complement.client.handler.GuiMouseHelper;
import com.circulation.random_complement.client.handler.HighlighterHandler;
import com.circulation.random_complement.client.handler.ItemTooltipHandler;
import com.circulation.random_complement.client.handler.RCInputHandler;
import com.circulation.random_complement.common.CommonProxy;
import com.circulation.random_complement.common.integration.ae2.AEIntegrations;
import com.circulation.random_complement.mixin.jei.AccessorBookmarkItem;
import com.circulation.random_complement.mixin.jei.AccessorGhostIngredientDragManager;
import com.circulation.random_complement.mixin.jei.AccessorInputHandler;
import lombok.val;
import mezz.jei.Internal;
import mezz.jei.gui.ghost.GhostIngredientDrag;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.jetbrains.annotations.NotNull;

@Mod.EventBusSubscriber(Side.CLIENT)
public class ClientProxy extends CommonProxy {
    public static final String categoryJEI = "RandomComplement(JEI)";
    public static final String categoryAE2 = "RandomComplement(AE2)";
    @NotNull
    private static ItemStack mouseItemStack = ItemStack.EMPTY;

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        val player = Minecraft.getMinecraft().player;
        if (player == null) {
            mouseItemStack = ItemStack.EMPTY;
            return;
        }
        var inv = player.inventory.getItemStack();
        if (!inv.isEmpty()) {
            mouseItemStack = inv;
            return;
        }
        if (Loader.isModLoaded("jei")) mouseItemStack = getJEIMouseItem();
        else mouseItemStack = ItemStack.EMPTY;
    }

    public static ItemStack getMouseItem() {
        return mouseItemStack;
    }

    @Optional.Method(modid = "jei")
    public static ItemStack getJEIMouseItem() {
        GhostIngredientDrag<?> ii = null;
        if (Internal.getInputHandler() != null) {
            ii = ((AccessorGhostIngredientDragManager) ((AccessorInputHandler) Internal.getInputHandler()).getGhostIngredientDragManager()).getGhostIngredientDrag();
        }
        if (ii != null) {
            if (ii.getIngredient() instanceof ItemStack stack) {
                return stack;
            }
            if (ii.getIngredient() instanceof AccessorBookmarkItem<?> book && book.i_getIngredient() instanceof ItemStack stack) {
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public void construction() {
        super.construction();
    }

    @Override
    public void preInit() {
        super.preInit();
    }

    @Override
    public void init() {
        super.init();
        if (AEIntegrations.INSTANCE.isEnabled()) {
            KeyBindings.init();
        }
    }

    @Override
    public void postInit() {
        super.postInit();
        MinecraftForge.EVENT_BUS.register(GuiMouseHelper.INSTANCE);
        MinecraftForge.EVENT_BUS.register(ItemTooltipHandler.INSTANCE);
        MinecraftForge.EVENT_BUS.register(HighlighterHandler.INSTANCE);
        if (AEIntegrations.INSTANCE.isEnabled()) {
            MinecraftForge.EVENT_BUS.register(RCInputHandler.INSTANCE);
        }
        RegItemTooltip.regAll();
    }

    @Override
    public boolean isMouseHasItem() {
        return !getMouseItem().isEmpty();
    }
}
