package com.circulation.random_complement.common.util;

import it.unimi.dsi.fastutil.chars.CharArrayList;
import it.unimi.dsi.fastutil.chars.CharList;
import it.unimi.dsi.fastutil.chars.CharLists;
import it.unimi.dsi.fastutil.chars.CharOpenHashSet;
import it.unimi.dsi.fastutil.chars.CharSet;
import it.unimi.dsi.fastutil.chars.CharSets;
import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import it.unimi.dsi.fastutil.doubles.DoubleLists;
import it.unimi.dsi.fastutil.doubles.DoubleOpenHashSet;
import it.unimi.dsi.fastutil.doubles.DoubleSet;
import it.unimi.dsi.fastutil.doubles.DoubleSets;
import it.unimi.dsi.fastutil.floats.FloatArrayList;
import it.unimi.dsi.fastutil.floats.FloatList;
import it.unimi.dsi.fastutil.floats.FloatLists;
import it.unimi.dsi.fastutil.floats.FloatOpenHashSet;
import it.unimi.dsi.fastutil.floats.FloatSet;
import it.unimi.dsi.fastutil.floats.FloatSets;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntLists;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.ints.IntSets;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import it.unimi.dsi.fastutil.longs.LongLists;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.longs.LongSets;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectLists;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectSets;
import it.unimi.dsi.fastutil.shorts.ShortArrayList;
import it.unimi.dsi.fastutil.shorts.ShortList;
import it.unimi.dsi.fastutil.shorts.ShortLists;
import it.unimi.dsi.fastutil.shorts.ShortOpenHashSet;
import it.unimi.dsi.fastutil.shorts.ShortSet;
import it.unimi.dsi.fastutil.shorts.ShortSets;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraftforge.fml.common.Loader;

public class Functions {

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

    public static boolean modLoaded(final String modID) {
        return Loader.isModLoaded(modID);
    }

    @SafeVarargs
    public static <T> ObjectList<T> asList(T... s){
        return ObjectLists.unmodifiable(ObjectArrayList.wrap(s));
    }

    public static IntList asList(int... s){
        return IntLists.unmodifiable(IntArrayList.wrap(s));
    }

    public static LongList asList(long... s){
        return LongLists.unmodifiable(LongArrayList.wrap(s));
    }

    public static ShortList asList(short... s){
        return ShortLists.unmodifiable(ShortArrayList.wrap(s));
    }

    public static FloatList asList(float... s){
        return FloatLists.unmodifiable(FloatArrayList.wrap(s));
    }

    public static DoubleList asList(double... s){
        return DoubleLists.unmodifiable(DoubleArrayList.wrap(s));
    }

    public static CharList asList(char... s){
        return CharLists.unmodifiable(CharArrayList.wrap(s));
    }

    @SafeVarargs
    public static <T> ObjectSet<T> asSet(T... s){
        return ObjectSets.unmodifiable(new ObjectOpenHashSet<>(s));
    }

    public static IntSet asSet(int... s){
        return IntSets.unmodifiable(new IntOpenHashSet(s));
    }

    public static LongSet asSet(long... s){
        return LongSets.unmodifiable(new LongOpenHashSet(s));
    }

    public static ShortSet asSet(short... s){
        return ShortSets.unmodifiable(new ShortOpenHashSet(s));
    }

    public static FloatSet asSet(float... s){
        return FloatSets.unmodifiable(new FloatOpenHashSet(s));
    }

    public static DoubleSet asSet(double... s){
        return DoubleSets.unmodifiable(new DoubleOpenHashSet(s));
    }

    public static CharSet asSet(char... s){
        return CharSets.unmodifiable(new CharOpenHashSet(s));
    }
}
