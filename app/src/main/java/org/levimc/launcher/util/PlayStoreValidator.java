package org.levimc.launcher.util;

import android.content.Context;

public class PlayStoreValidator {
    private static final String MINECRAFT_PACKAGE_NAME = "com.mojang.minecraftpe";
    private static final String PLAY_STORE_INSTALLER = "com.android.vending";

    public static boolean isMinecraftFromPlayStore(Context context) {
        return MinecraftPackageDetector.isMinecraftInstalled(context);
    }

    public static boolean isMinecraftInstalled(Context context) {
        return MinecraftPackageDetector.isMinecraftInstalled(context);
    }

    public static boolean isLicenseVerified(Context context) {
        return MinecraftPackageDetector.isMinecraftInstalled(context);
    }

}
