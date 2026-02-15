package com.circulation.random_complement.mixin;

import com.circulation.random_complement.RCConfig;
import net.minecraftforge.fml.common.Loader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

@SuppressWarnings("unused")
public abstract class MixinConfigPlugin implements IMixinConfigPlugin {

    private static final String packet = "com.circulation.random_complement.mixin.";
    private final String modPacket;

    protected MixinConfigPlugin(String modPacket) {
        this.modPacket = packet + modPacket + ".";
    }

    protected final String erasePacketPrefix(String mixinPackage) {
        return mixinPackage.substring(modPacket.length());
    }

    @Override
    public void onLoad(String s) {

    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public void acceptTargets(Set<String> set, Set<String> set1) {

    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String s, ClassNode classNode, String s1, IMixinInfo iMixinInfo) {

    }

    @Override
    public void postApply(String s, ClassNode classNode, String s1, IMixinInfo iMixinInfo) {

    }

    public static final class AE2 extends MixinConfigPlugin {

        public AE2() {
            super("ae2");
        }

        @Override
        public boolean shouldApplyMixin(String s, String mixinClassName) {
            mixinClassName = erasePacketPrefix(mixinClassName);
            if (mixinClassName.startsWith("miss_craft")) {
                return RCConfig.AE2.enableMissCraft;
            }
            if (mixinClassName.startsWith("branch_craft")) {
                return RCConfig.AE2.enableBranchCraft;
            }
            if (mixinClassName.startsWith("new_patten_gui")) {
                return RCConfig.AE2.newPattenGui;
            }
            return true;
        }
    }

    public static final class AE2FC extends MixinConfigPlugin {

        public AE2FC() {
            super("ae2fc");
        }

        @Override
        public boolean shouldApplyMixin(String s, String mixinClassName) {
            mixinClassName = erasePacketPrefix(mixinClassName);
            if (mixinClassName.startsWith("new_patten_gui")) {
                return RCConfig.AE2.newPattenGui;
            }
            return true;
        }
    }

    public static final class AE2CTL extends MixinConfigPlugin {

        public AE2CTL() {
            super("ae2ctl");
        }

        @Override
        public boolean shouldApplyMixin(String s, String mixinClassName) {
            mixinClassName = erasePacketPrefix(mixinClassName);
            if (mixinClassName.startsWith("branch_craft")) {
                return RCConfig.AE2.enableBranchCraft;
            }
            if (mixinClassName.startsWith("miss_craft")) {
                return RCConfig.AE2.enableMissCraft;
            }
            return true;
        }
    }

    public static final class Botania extends MixinConfigPlugin {

        private static final boolean isOfficial;

        static {
            boolean c;
            try {
                String v = Loader.instance().getIndexedModList().get("botania").getMetadata().version;
                c = v.equals("r1.10-364.4");
            } catch (Exception e) {
                c = false;
            }
            isOfficial = c;
        }

        public Botania() {
            super("botania");
        }

        @Override
        public boolean shouldApplyMixin(String s, String mixinClassName) {
            mixinClassName = erasePacketPrefix(mixinClassName);
            if (mixinClassName.startsWith("official")) {
                return isOfficial;
            }
            if (mixinClassName.startsWith("flower")) {
                return RCConfig.Botania.FlowerLinkPool;
            }
            if (mixinClassName.startsWith("spark")) {
                return RCConfig.Botania.SparkSupport;
            }
            return RCConfig.Botania.BugFix;
        }
    }
}
