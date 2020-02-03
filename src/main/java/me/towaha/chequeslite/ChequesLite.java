package me.towaha.chequeslite;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public final class ChequesLite extends JavaPlugin {

    public ChequesManager chequesManager;

    public static Economy economy = null;

    @Override
    public void onEnable() {
        if (!setupEconomy()) {
            getServer().getConsoleSender().sendRawMessage("§8----------------------------------------" +
                    "\n                 §4Cheques Lite Severe Error:"                 +
                    "\n                 §cDisabled due to no Vault dependency found!" +
                    "\n                 §cThis plugin requires Vault to function."    +
                    "\n                 §8----------------------------------------"
            );
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        chequesManager = new ChequesManager(this);

        getServer().getPluginManager().registerEvents(new EventManager(), this);

        getCommand("cheque").setExecutor(chequesManager);

        saveResource("config.yml", false);
        new Messages(this);

        getLogger().info("§aChequesLite has successfully started");
    }

    @Override
    public void onDisable() {
        getLogger().info("§cChequesLite has successfully stopped");
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> registeredServiceProvider = getServer().getServicesManager().getRegistration(Economy.class);
        if (registeredServiceProvider == null) {
            return false;
        }
        economy = registeredServiceProvider.getProvider();
        return economy != null;
    }

    public List<String> getAvailableOptions(List<String> currentOptions, String input) {
        List<String> availableOptions = new ArrayList<>();

        for (String option : currentOptions)
            if(option.length() >= input.length() && option.substring(0, input.length()).equals(input))
                availableOptions.add(option);

        return availableOptions;
    }

    private String getVersion() {
        return getServer().getClass().getPackage().getName().split("\\.")[3];
    }

    public enum Conditions {
        GREATER,
        GREATEROREQUAL,
        LESS,
        LESSOREQUAL,
        EQUAL
    }
    public boolean compareVersion(String version, Conditions condition) {
        String currentVersion = null;
        switch(getVersion()){
            case "v1_8_R3":
                currentVersion = "1.8.8";
                break;
            case "v1_9_R1":
                currentVersion = "1.9.2";
                break;
            case "v1_9_R2":
                currentVersion = "1.9.4";
                break;
            case "v1_10_R1":
                currentVersion = "1.10.2";
                break;
            case "v1_11_R1":
                currentVersion = "1.11";
                break;
            case "v1_12_R1":
                currentVersion = "1.12";
                break;
            case "v1_13_R1":
                currentVersion = "1.13";
                break;
            case "v1_13_R2":
                currentVersion = "1.13.1";
                break;
            case "v1_14_R1":
                currentVersion = Bukkit.getBukkitVersion().contains("1.14.4") ? "1.14.4" : "1.14";
                break;
            case "v1_15_R1":
                currentVersion = "1.15";
                break;
        }

        if(currentVersion == null) {
            getLogger().severe("Server version wasn't able to be found, parts of the plugin may not work correctly. Please contact the developers. (VER: " + getVersion() + ")");
            return false;
        }

        try {
            if(version.split("\\.").length == 2) {
                int compareVersion = Integer.parseInt(currentVersion.split("\\.")[1]);
                int askedVersion = Integer.parseInt(version.split("\\.")[1]);

                switch (condition) {
                    case LESS:
                        return compareVersion < askedVersion;
                    case LESSOREQUAL:
                        return compareVersion <= askedVersion;
                    case GREATER:
                        return compareVersion > askedVersion;
                    case GREATEROREQUAL:
                        return compareVersion >= askedVersion;
                    case EQUAL:
                        return compareVersion == askedVersion;
                }
            } else {
                float compareVersion = Float.parseFloat(currentVersion.split("\\.")[1]);
                float askedVersion = Float.parseFloat(version.split("\\.")[1]);

                switch (condition) {
                    case LESS:
                        return compareVersion < askedVersion;
                    case LESSOREQUAL:
                        return compareVersion <= askedVersion;
                    case GREATER:
                        return compareVersion > askedVersion;
                    case GREATEROREQUAL:
                        return compareVersion >= askedVersion;
                    case EQUAL:
                        return compareVersion == askedVersion;
                }
            }
        } catch (NumberFormatException exception) {
            return false;
        }
        return false;
    }

    public void saveResource(String resourcePath, boolean replace) {
        if (resourcePath == null || resourcePath.equals("")) {
            throw new IllegalArgumentException("ResourcePath cannot be null or empty");
        }

        resourcePath = resourcePath.replace('\\', '/');
        InputStream in = getResource(resourcePath);
        if (in == null) {
            throw new IllegalArgumentException("The embedded resource '" + resourcePath + "' cannot be found in " + getFile());
        }

        File outFile = new File(getDataFolder(), resourcePath);
        int lastIndex = resourcePath.lastIndexOf('/');
        File outDir = new File(getDataFolder(), resourcePath.substring(0, Math.max(lastIndex, 0)));

        if (!outDir.exists())
            if(!outDir.mkdirs())
                getServer().getLogger().warning("Something went wrong while saving the '" + outFile.getName() + "' file");

        try {
            if (!outFile.exists() || replace) {
                OutputStream out = new FileOutputStream(outFile);
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                out.close();
                in.close();
            }
        } catch (IOException ex) {
            getServer().getLogger().warning("Something went wrong while saving the '" + outFile.getName() + "' file");
        }
    }
}
