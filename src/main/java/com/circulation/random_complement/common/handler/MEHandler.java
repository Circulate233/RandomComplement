package com.circulation.random_complement.common.handler;

import appeng.api.AEApi;
import appeng.api.features.ILocatable;
import appeng.api.features.IWirelessTermHandler;
import appeng.api.features.IWirelessTermRegistry;
import appeng.api.storage.data.IAEItemStack;
import appeng.container.implementations.ContainerMEMonitorable;
import appeng.container.slot.SlotRestrictedInput;
import appeng.core.localization.PlayerMessages;
import appeng.helpers.WirelessTerminalGuiObject;
import appeng.tile.misc.TileSecurityStation;
import appeng.util.Platform;
import appeng.util.item.AEItemStack;
import baubles.api.BaublesApi;
import com.circulation.random_complement.RandomComplement;
import com.circulation.random_complement.client.buttonsetting.PatternTermAutoFillPattern;
import com.circulation.random_complement.common.interfaces.PatternTermConfigs;
import com.circulation.random_complement.common.util.SimpleItem;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class MEHandler {

    public static final Set<SimpleItem> craftableCacheS = new ObjectOpenHashSet<>();

    @SideOnly(Side.CLIENT)
    public static void drawSlotPluses(List<Slot> slots) {
        RenderHelper.disableStandardItemLighting();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        GlStateManager.pushMatrix();
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        for (var slot : slots) {
            int x = slot.xPos;
            int y = slot.yPos;
            float startX = x + 0.5f;
            float startY = y + 0.25f;
            float endX = startX + 3f;
            float endY = startY + 3f;

            buffer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION);
            buffer.pos(startX, startY + 1.5f, 0).endVertex();
            buffer.pos(endX, startY + 1.5f, 0).endVertex();
            buffer.pos(startX + 1.5f, startY, 0).endVertex();
            buffer.pos(startX + 1.5f, endY, 0).endVertex();
            tessellator.draw();
        }
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
        RenderHelper.enableGUIStandardItemLighting();
        slots.clear();
    }

    /*
     * 使用了GTNH团队的AE2U的相关方法
     * https://github.com/GTNewHorizons/Applied-Energistics-2-Unofficial/blob/c3cd45df5db9db256b9fb4774b2cb57fdf11e389/src/main/java/appeng/container/implementations/ContainerMEMonitorable.java#L397
     */
    public static void refillBlankPatterns(ContainerMEMonitorable container, SlotRestrictedInput slot) {
        if (container instanceof PatternTermConfigs) {
            if (((PatternTermConfigs) container).r$getAutoFillPattern() == PatternTermAutoFillPattern.CLOSE) return;
            if (Platform.isServer()) {
                ItemStack blanks = slot.getStack();
                int blanksToRefill = 64;
                blanksToRefill -= blanks.getCount();
                if (blanksToRefill <= 0) return;
                var blankPattern = AEApi.instance().definitions().materials().blankPattern().maybeStack(blanksToRefill);
                if (blankPattern.isPresent()) {
                    final AEItemStack request = AEItemStack
                            .fromItemStack(blankPattern.get());
                    final IAEItemStack extracted = Platform
                            .poweredExtraction(container.getPowerSource(), container.getCellInventory(), request, container.getActionSource());
                    if (extracted != null) {
                        if (blanks.isEmpty()) {
                            blanks = request.getDefinition().copy();
                            blanks.setCount((int) (extracted.getStackSize()));
                        } else {
                            blanks.setCount((int) (blanks.getCount() + extracted.getStackSize()));
                        }
                        slot.putStack(blanks);
                    }
                }
            }
        }
    }

    @Optional.Method(modid = "appliedenergistics2")
    public static WirelessTerminalGuiObject getTerminalGuiObject(EntityPlayer player) {
        for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
            ItemStack item = player.inventory.getStackInSlot(i);
            if (item.getItem() instanceof IWirelessTermHandler t && t.canHandle(item)) {
                return getTerminalGuiObject(item, player, i, 0);
            }
        }

        if (Loader.isModLoaded("baubles")) {
            return readBaubles(player);
        }
        return null;
    }

    @Optional.Method(modid = "appliedenergistics2")
    public static WirelessTerminalGuiObject getTerminalGuiObject(InventoryPlayer player) {
        for (int i = 0; i < player.getSizeInventory(); i++) {
            ItemStack item = player.getStackInSlot(i);
            if (item.getItem() instanceof IWirelessTermHandler t && t.canHandle(item)) {
                return getTerminalGuiObject(item, player.player, i, 0);
            }
        }

        if (Loader.isModLoaded("baubles")) {
            return readBaubles(player.player);
        }
        return null;
    }

    @Optional.Method(modid = "baubles")
    public static WirelessTerminalGuiObject readBaubles(EntityPlayer player) {
        for (int i = 0; i < BaublesApi.getBaublesHandler(player).getSlots(); i++) {
            ItemStack item = BaublesApi.getBaublesHandler(player).getStackInSlot(i);
            if (item.getItem() instanceof IWirelessTermHandler t && t.canHandle(item)) {
                return getTerminalGuiObject(item, player, i, 1);
            }
        }
        return null;
    }

    @Optional.Method(modid = "appliedenergistics2")
    public static WirelessTerminalGuiObject getTerminalGuiObject(ItemStack item, EntityPlayer player, int slot, int isBauble) {
        if (Platform.isClient()) return null;
        if (item.getItem() instanceof IWirelessTermHandler wt && wt.canHandle(item)) {
            IWirelessTermRegistry registry = AEApi.instance().registries().wireless();
            if (!registry.isWirelessTerminal(item)) {
                player.sendMessage(PlayerMessages.DeviceNotWirelessTerminal.get());
                return null;
            }
            IWirelessTermHandler handler = registry.getWirelessTerminalHandler(item);
            String unparsedKey = handler.getEncryptionKey(item);
            if (unparsedKey.isEmpty()) {
                player.sendMessage(PlayerMessages.DeviceNotLinked.get());
                return null;
            }
            long parsedKey = Long.parseLong(unparsedKey);
            ILocatable securityStation = AEApi.instance().registries().locatable().getLocatableBy(parsedKey);
            if (securityStation instanceof TileSecurityStation t) {
                if (!handler.hasPower(player, 1000F, item)) {
                    player.sendMessage(PlayerMessages.DeviceNotPowered.get());
                    return null;
                }
                return new WirelessTerminalGuiObject(wt, item, player, player.world, slot, isBauble, Integer.MIN_VALUE);
            }
        }
        return null;
    }

    private static final Map<Integer, ResourceLocation> textures = new Object2ObjectOpenHashMap<>();

    static {
        textures.put(0, new ResourceLocation(RandomComplement.MOD_ID + ":textures/gui/pinned0.png"));
        textures.put(1, new ResourceLocation(RandomComplement.MOD_ID + ":textures/gui/pinned1.png"));
        textures.put(2, new ResourceLocation(RandomComplement.MOD_ID + ":textures/gui/pinned2.png"));
        final ResourceLocation rl = new ResourceLocation(RandomComplement.MOD_ID + ":textures/gui/pinned3.png");
        textures.put(3, rl);
        textures.put(4, rl);
        textures.put(5, rl);
        textures.put(6, new ResourceLocation(RandomComplement.MOD_ID + ":textures/gui/pinned4.png"));
    }

    @SideOnly(Side.CLIENT)
    public static void bindTexture(Minecraft mc, int craftingSlotTextureIndex) {
        mc.getTextureManager().bindTexture(textures.get(craftingSlotTextureIndex));
    }
}