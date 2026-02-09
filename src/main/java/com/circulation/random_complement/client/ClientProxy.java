package com.circulation.random_complement.client;

import com.circulation.random_complement.RandomComplement;
import com.circulation.random_complement.client.handler.GuiMouseHelper;
import com.circulation.random_complement.client.handler.HighlighterHandler;
import com.circulation.random_complement.client.handler.ItemTooltipHandler;
import com.circulation.random_complement.client.handler.RCInputHandler;
import com.circulation.random_complement.client.handler.RCJEIInputHandler;
import com.circulation.random_complement.common.CommonProxy;
import com.circulation.random_complement.common.util.Functions;
import com.circulation.random_complement.mixin.jei.AccessorGhostIngredientDragManager;
import com.circulation.random_complement.mixin.jei.AccessorInputHandler;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import it.unimi.dsi.fastutil.objects.ReferenceList;
import lombok.val;
import mezz.jei.Internal;
import mezz.jei.gui.ghost.GhostIngredientDrag;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(value = Side.CLIENT, modid = RandomComplement.MOD_ID)
public class ClientProxy extends CommonProxy {
    public static final String categoryJEI = "RandomComplement(JEI)";
    public static final String categoryAE2 = "RandomComplement(AE2)";

    public static ItemStack getMouseItem() {
        val player = Minecraft.getMinecraft().player;
        if (player == null) return ItemStack.EMPTY;
        val i = player.inventory.getItemStack();
        if (!i.isEmpty()) return i;

        if (Loader.isModLoaded("jei")) return getJEIMouseItem();

        return ItemStack.EMPTY;
    }

    @Optional.Method(modid = "jei")
    public static ItemStack getJEIMouseItem() {
        GhostIngredientDrag<?> ii = null;
        if (Internal.getInputHandler() != null) {
            ii = ((AccessorGhostIngredientDragManager) ((AccessorInputHandler) Internal.getInputHandler()).getGhostIngredientDragManager()).getGhostIngredientDrag();
        }
        if (ii != null && ii.getIngredient() instanceof ItemStack stack) return stack;
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
        if (Functions.modLoaded("appliedenergistics2")) {
            KeyBindings.init();
        }
    }

    @Override
    public void postInit() {
        super.postInit();
        MinecraftForge.EVENT_BUS.register(GuiMouseHelper.INSTANCE);
        MinecraftForge.EVENT_BUS.register(ItemTooltipHandler.INSTANCE);
        MinecraftForge.EVENT_BUS.register(HighlighterHandler.INSTANCE);
        if (Loader.isModLoaded("appliedenergistics2")) {
            MinecraftForge.EVENT_BUS.register(RCInputHandler.INSTANCE);
        }
        if (Loader.isModLoaded("jei")) {
            ReferenceList<Class<?>> classes = new ReferenceArrayList<>();
            if (Loader.isModLoaded("appliedenergistics2")) {
                try {
                    classes.add(Class.forName("appeng.client.gui.AEBaseGui"));
                } catch (ClassNotFoundException ignored) {

                }
            }
            if (Loader.isModLoaded("packagedauto")) {
                try {
                    classes.add(Class.forName("thelm.packagedauto.client.gui.GuiEncoder"));
                } catch (ClassNotFoundException ignored) {

                }
            }
            if (!classes.isEmpty()) {
                RCJEIInputHandler.setJeiGui(classes.toArray(new Class[0]));
                MinecraftForge.EVENT_BUS.register(RCJEIInputHandler.INSTANCE);
            }
        }
        RegItemTooltip.regAll();
    }

    @Override
    @Optional.Method(modid = "jei")
    public boolean isMouseHasItem() {
        return !getMouseItem().isEmpty();
    }
}