package org.levimc.launcher.util;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import org.levimc.launcher.core.minecraft.MinecraftLauncher;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Finds Minecraft Bedrock packages already installed on the device,
 * regardless of whether they came from Play Store, another store, or a sideload.
 */
public final class MinecraftPackageDetector {
    private MinecraftPackageDetector() {
    }

    public static final String[] KNOWN_PACKAGE_NAMES = {
            MinecraftLauncher.MC_PACKAGE_NAME
    };

    public static List<PackageInfo> findInstalledPackages(Context context) {
        PackageManager pm = context.getPackageManager();
        LinkedHashMap<String, PackageInfo> found = new LinkedHashMap<>();

        for (String packageName : KNOWN_PACKAGE_NAMES) {
            PackageInfo info = getPackageInfo(pm, packageName);
            if (info != null) {
                found.put(packageName, info);
            }
        }

        try {
            List<ApplicationInfo> apps = pm.getInstalledApplications(0);
            for (ApplicationInfo app : apps) {
                if (app == null || app.packageName == null || found.containsKey(app.packageName)) {
                    continue;
                }
                if (looksLikeMinecraft(pm, app)) {
                    PackageInfo info = getPackageInfo(pm, app.packageName);
                    if (info != null) {
                        found.put(app.packageName, info);
                    }
                }
            }
        } catch (Exception ignored) {
        }

        return new ArrayList<>(found.values());
    }

    public static boolean isMinecraftInstalled(Context context) {
        return !findInstalledPackages(context).isEmpty();
    }

    public static PackageInfo findPrimary(Context context) {
        List<PackageInfo> packages = findInstalledPackages(context);
        return packages.isEmpty() ? null : packages.get(0);
    }

    public static String profileIdFor(String packageName) {
        if (packageName == null || packageName.trim().isEmpty()) {
            return LauncherStorage.INSTALLED_MINECRAFT_PROFILE_ID;
        }
        return packageName;
    }

    public static boolean isMinecraftPackageName(String packageName) {
        if (packageName == null || packageName.isEmpty()) {
            return false;
        }
        for (String known : KNOWN_PACKAGE_NAMES) {
            if (known.equals(packageName)) {
                return true;
            }
        }
        return packageName.startsWith("com.mojang.minecraft");
    }

    static boolean looksLikeMinecraft(PackageManager pm, ApplicationInfo app) {
        if (isMinecraftPackageName(app.packageName)) {
            return true;
        }
        if (app.nativeLibraryDir != null) {
            File nativeLib = new File(app.nativeLibraryDir, "libminecraftpe.so");
            if (nativeLib.exists()) {
                return true;
            }
        }
        try {
            pm.getActivityInfo(new ComponentName(app.packageName, "com.mojang.minecraftpe.MainActivity"), 0);
            return true;
        } catch (PackageManager.NameNotFoundException ignored) {
            return false;
        }
    }

    private static PackageInfo getPackageInfo(PackageManager pm, String packageName) {
        try {
            return pm.getPackageInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }
}
