package com.circulation.random_complement.mixin.ae2.gui;

import appeng.client.gui.MathExpressionParser;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(MathExpressionParser.class)
public class MixinMathExpressionParser {

    @ModifyArg(method = "prepare", at = @At(value = "INVOKE", target = "Ljava/util/Stack;push(Ljava/lang/Object;)Ljava/lang/Object;"), remap = false)
    private Object normalizeNumberToken(Object value) {
        if (!(value instanceof String)) {
            return value;
        }
        return rc$normalizeToken((String) value);
    }

    @Unique
    private static String rc$normalizeToken(String token) {
        int len = token.length();
        if (len == 0) {
            return token;
        }
        if (rc$containsExponent(token)) {
            return token;
        }
        char suffix = token.charAt(len - 1);
        int exp;
        switch (Character.toLowerCase(suffix)) {
            case 'k':
                exp = 3;
                break;
            case 'm':
                exp = 6;
                break;
            case 'b':
            case 'g':
                exp = 9;
                break;
            case 't':
                exp = 12;
                break;
            case 'p':
                exp = 15;
                break;
            default:
                return token;
        }
        return token.substring(0, len - 1) + "e" + exp;
    }

    @Unique
    private static boolean rc$containsExponent(String token) {
        for (int i = 0; i < token.length(); i++) {
            char c = token.charAt(i);
            if (c == 'e' || c == 'E') {
                return true;
            }
        }
        return false;
    }
}
