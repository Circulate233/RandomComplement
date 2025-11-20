package com.circulation.random_complement.common.util;

import lombok.val;
import net.minecraftforge.fml.common.Loader;

public class VersionParser {

    public static int[] parseVersion(String version) {
        String[] parts = version.split("\\.");

        try {
            int[] out = new int[parts.length];
            for (int i = 0; i < parts.length; i++) {
                out[i] = Integer.parseInt(parts[i]);
            }
            return out;

        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid version number format: " + version, e);
        }

    }

    public static boolean maxVersion(String modid, String targetVersion) {
        return !minVersion(modid, targetVersion);
    }

    public static boolean minVersion(String modid, String targetVersion) {
        var nowVersion = getModVersion(modid);
        int[] now = parseVersion(nowVersion);
        int[] target = parseVersion(targetVersion);

        if (now.length != target.length) {
            if (now.length > target.length) {
                int[] t = new int[now.length];
                System.arraycopy(target, 0, t, 0, target.length);
                target = t;
            } else {
                int[] n = new int[target.length];
                System.arraycopy(now, 0, n, 0, now.length);
                now = n;
            }
        }

        for (int i = 0; i < now.length; i++) {
            if (now[i] < target[i]) {
                return false;
            } else if (now[i] > target[i]) {
                return true;
            }
        }

        return true;
    }

    public static boolean rangeVersion(String modid, String min, String max) {
        return VersionParser.minVersion(modid, min)
            && VersionParser.maxVersion(modid, max);
    }

    public static String getModVersion(String modid) {
        val version = Loader.instance().getIndexedModList().get(modid).getMetadata().version;
        String[] parts = version.split("\\.");

        try {
            Integer.parseInt(parts[0]);
        } catch (NumberFormatException e) {
            return "0";
        }

        return version;
    }

}