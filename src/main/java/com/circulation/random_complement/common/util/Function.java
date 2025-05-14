package com.circulation.random_complement.common.util;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;

public class Function {

    public static IBlockState getBlockFromName(String name){
        if (name != null) {
            String[] parts = name.split(":");
            if (parts.length >= 2) {
                Block block = Block.getBlockFromName(parts[0] + ":" + parts[1]);
                if (block == null){
                    return null;
                }
                int meta = (parts.length > 2) ? Integer.parseInt(parts[2]) : 0;
                return block.getBlockState().getValidStates().get(meta);
            }
        }
        return null;
    }
}
