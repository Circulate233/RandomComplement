package com.circulation.random_complement.common.util;

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

    public static boolean MinimumVersion(String nowVersion,String targetVersion){
        int[] now = parseVersion(nowVersion);
        int[] target = parseVersion(targetVersion);

        if (now.length != target.length){
            if (now.length > target.length){
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
            if (now[i] < target[i]){
                return false;
            } else if (now[i] > target[i]) {
                return true;
            }
        }

        return true;
    }

}
