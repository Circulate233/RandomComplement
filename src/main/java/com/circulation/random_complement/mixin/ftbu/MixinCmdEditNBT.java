package com.circulation.random_complement.mixin.ftbu;

import com.feed_the_beast.ftblib.lib.command.CmdTreeBase;
import com.feed_the_beast.ftbutilities.command.CmdEditNBT;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(CmdEditNBT.class)
public class MixinCmdEditNBT extends CmdTreeBase {


    public MixinCmdEditNBT(String n) {
        super(n);
    }


    @ModifyConstant(method = "<init>", constant = @Constant(stringValue = "nbtedit"), remap = false)
    private static String setCmdNBT(String constant) {
        return "ftbnbtedit";
    }


}
