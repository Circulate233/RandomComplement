package com.circulation.random_complement.client;

import appeng.api.config.Settings;
import appeng.client.gui.widgets.GuiImgButton;
import com.circulation.random_complement.RandomComplement;
import com.circulation.random_complement.client.buttonsetting.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

@SideOnly(Side.CLIENT)
public class RCGuiButton extends GuiImgButton {
    private static final Pattern COMPILE = Pattern.compile("%s");
    private static final Pattern PATTERN_NEW_LINE = Pattern.compile("\\n", Pattern.LITERAL);
    private static Map<RCEnumPair, RCButtonAppearance> appearances;
    private final Enum<?> buttonSetting;
    private static final ResourceLocation texture = new ResourceLocation(RandomComplement.MOD_ID , "textures/gui/states.png");
    private String exMessage = "";

    public RCGuiButton(int x, int y, Enum idx, Enum val) {
        super(x, y, idx, val);
        this.buttonSetting = idx;
        if (appearances == null) {
            appearances = new HashMap<>();
            this.registerApp(0,RCSettings.InscriberBlockMode, InscriberBlockMode.OPEN);
            this.registerApp(1,RCSettings.InscriberBlockMode, InscriberBlockMode.CLOSE);
            this.registerApp(2,RCSettings.InscriberAutoOutput, InscriberAutoOutput.CLOSE);
            this.registerApp(3,RCSettings.InscriberAutoOutput, InscriberAutoOutput.OPEN);
            this.registerApp(4,RCSettings.InscriberMaxStackLimit, InscriberMaxStackLimit.SMALL);
            this.registerApp(5,RCSettings.InscriberMaxStackLimit, InscriberMaxStackLimit.MEDIUM);
            this.registerApp(6,RCSettings.InscriberMaxStackLimit, InscriberMaxStackLimit.BIG);
            this.registerApp(7,RCSettings.PatternTermAutoFillPattern, PatternTermAutoFillPattern.OPEN);
            this.registerApp(8,RCSettings.PatternTermAutoFillPattern, PatternTermAutoFillPattern.CLOSE);
        }
    }

    private int getIconIndex() {
        if (this.buttonSetting != null && this.getCurrentValue() != null) {
            RCButtonAppearance app = appearances.get(new RCEnumPair(this.buttonSetting, this.getCurrentValue()));
            return app == null ? 255 : app.index;
        } else {
            return 255;
        }
    }

    public Settings getSetting() {
        return null;
    }

    public RCSettings getRCSetting() {
        return (RCSettings)this.buttonSetting;
    }

    public void drawButton(Minecraft minecraft, int par2, int par3, float partial) {
        if (this.visible) {
            int iconIndex = this.getIconIndex();
            if (this.isHalfSize()) {
                this.width = 8;
                this.height = 8;
                GlStateManager.pushMatrix();
                GlStateManager.translate((float)this.x, (float)this.y, 0.0F);
                GlStateManager.scale(0.5F, 0.5F, 0.5F);
                if (this.enabled) {
                    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                } else {
                    GlStateManager.color(0.5F, 0.5F, 0.5F, 1.0F);
                }

                minecraft.renderEngine.bindTexture(texture);
                this.hovered = par2 >= this.x && par3 >= this.y && par2 < this.x + this.width && par3 < this.y + this.height;
                int uv_y = (int)Math.floor((double) iconIndex / 16);
                int uv_x = iconIndex - uv_y * 16;
                this.drawTexturedModalRect(0, 0, 240, 240, 16, 16);
                this.drawTexturedModalRect(0, 0, uv_x * 16, uv_y * 16, 16, 16);
                this.mouseDragged(minecraft, par2, par3);
                GlStateManager.popMatrix();
            } else {
                if (this.enabled) {
                    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                } else {
                    GlStateManager.color(0.5F, 0.5F, 0.5F, 1.0F);
                }

                minecraft.renderEngine.bindTexture(texture);
                this.hovered = par2 >= this.x && par3 >= this.y && par2 < this.x + this.width && par3 < this.y + this.height;
                int uv_y = (int)Math.floor((double) iconIndex / 16);
                int uv_x = iconIndex - uv_y * 16;
                this.drawTexturedModalRect(this.x, this.y, 240, 240, 16, 16);
                this.drawTexturedModalRect(this.x, this.y, uv_x * 16, uv_y * 16, 16, 16);
                this.mouseDragged(minecraft, par2, par3);
            }
        }

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }

    private void registerApp(int iconIndex, RCSettings setting, InterfaceButton val) {
        RCButtonAppearance a = new RCButtonAppearance();
        a.displayName = val.getName();
        a.displayValue = val.getTooltip();
        a.index = iconIndex;
        appearances.put(new RCEnumPair(setting,(Enum<?>) val), a);
    }

    public String getMessage() {
        String displayName = null;
        String displayValue = null;
        if (this.buttonSetting != null && this.getCurrentValue() != null) {
            RCButtonAppearance buttonAppearance = appearances.get(new RCEnumPair(this.buttonSetting, this.getCurrentValue()));
            if (buttonAppearance == null) {
                return "No Such Message";
            }

            displayName = buttonAppearance.displayName;
            displayValue = buttonAppearance.displayValue;
        }

        if (displayName == null) {
            return null;
        } else {
            String name = I18n.translateToLocal(displayName);
            String value = I18n.translateToLocal(displayValue);
            if (name.isEmpty()) {
                name = displayName;
            }

            if (value.isEmpty()) {
                value = displayValue;
            }

            if (this.getFillVar() != null) {
                value = COMPILE.matcher(value).replaceFirst(this.getFillVar());
            }

            value = PATTERN_NEW_LINE.matcher(value).replaceAll("\n");
            StringBuilder sb = new StringBuilder(value);
            int i = sb.lastIndexOf("\n");
            if (i <= 0) {
                i = 0;
            }

            while(i + 30 < sb.length() && (i = sb.lastIndexOf(" ", i + 30)) != -1) {
                sb.replace(i, i + 1, "\n");
            }

            return name + '\n' + sb + '\n' + exMessage;
        }
    }

    public void setEXMessage(String exMessage){
        this.exMessage = exMessage;
    }

    private static class RCButtonAppearance {
        public int index;
        public String displayName;
        public String displayValue;

        private RCButtonAppearance() {
        }
    }

    public static final class RCEnumPair {
        final Enum<?> setting;
        final Enum<?> value;

        RCEnumPair(Enum<?> a, Enum<?> b) {
            this.setting = a;
            this.value = b;
        }

        public int hashCode() {
            return this.setting.hashCode() ^ this.value.hashCode();
        }

        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            } else if (this.getClass() != obj.getClass()) {
                return false;
            } else {
                RCEnumPair other = (RCEnumPair)obj;
                return other.setting == this.setting && other.value == this.value;
            }
        }
    }
}
