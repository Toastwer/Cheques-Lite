package me.towaha.chequeslite.Classes;

import me.towaha.chequeslite.ChequesLite;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Constructor;
import java.util.*;

public class NBTItemStack extends ItemStack {
    public NBTItemStack(ItemStack itemStack) {
        super(itemStack);
    }

    //<editor-fold> Reflection

    private Class<?> getNMSClass(String name) {
        try {
            return Class.forName("net.minecraft.server." + getVersion() + "." + name);
        } catch (ClassNotFoundException exception) {
            exception.printStackTrace();
        }

        return null;
    }

    private Class<?> getCraftBukkitClass(String name) {
        try {
            return Class.forName("org.bukkit.craftbukkit." + getVersion() + "." + name);
        } catch (ClassNotFoundException exception) {
            exception.printStackTrace();
        }

        return null;
    }

    private String getVersion() {
        return Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
    }

    public boolean compareVersion(String version, ChequesLite.Conditions condition) {
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
            Bukkit.getLogger().severe("Server version wasn't able to be found, parts of the plugin may not work correctly. Please contact the developers. (VER: " + getVersion() + ")");
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

    //</editor-fold>

    public boolean spawnForPlayer(Player player) {
        Inventory inventory = player.getInventory();

        List<Integer> emptySpots = new ArrayList<>();
        List<Integer> sameSpots = new ArrayList<>();

        for(int slot = 0; slot < 36; slot++) {
            if(inventory.getItem(slot) == null)
                emptySpots.add(slot);
            else if(inventory.getItem(slot).getItemMeta().equals(super.getItemMeta()) && inventory.getItem(slot).getData().equals(super.getData()))
                sameSpots.add(slot);
        }

        if(sameSpots.size() > 0) {
            int freeAmount = 64 - inventory.getItem(sameSpots.get(0)).getAmount();
            if(freeAmount >= super.getAmount()) {
                ItemStack clone = super.clone();
                clone.setAmount(super.getAmount() + inventory.getItem(sameSpots.get(0)).getAmount());
                inventory.setItem(sameSpots.get(0), clone);
                return true;
            }

            freeAmount = 0;
            for (int sameSpot : sameSpots)
                freeAmount += 64 - inventory.getItem(sameSpot).getAmount();

            int amountLeft = super.getAmount();
            if(freeAmount >= super.getAmount()) {
                for (int slot : sameSpots) {
                    ItemStack clone = super.clone();

                    int canBeAdded = 64 - inventory.getItem(slot).getAmount();
                    amountLeft -= canBeAdded;
                    if(amountLeft <= 0) {
                        clone.setAmount(inventory.getItem(slot).getAmount() + amountLeft + canBeAdded);
                        inventory.setItem(slot, clone);
                        return true;
                    }

                    clone.setAmount(64);
                    inventory.setItem(slot, clone);
                }

                return true;
            } else {
                if(emptySpots.size() > 0) {
                    for (int slot : sameSpots) {
                        ItemStack clone = super.clone();

                        int canBeAdded = 64 - inventory.getItem(slot).getAmount();
                        amountLeft -= canBeAdded;
                        if(amountLeft <= 0) {
                            clone.setAmount(inventory.getItem(slot).getAmount() + amountLeft + canBeAdded);
                            inventory.setItem(slot, clone);
                            return true;
                        }

                        clone.setAmount(64);
                        inventory.setItem(slot, clone);
                    }

                    for (int slot : emptySpots) {
                        ItemStack clone = super.clone();

                        int canBeAdded = 64;
                        amountLeft -= canBeAdded;
                        if(amountLeft <= 0) {
                            clone.setAmount(amountLeft + canBeAdded);
                            inventory.setItem(slot, clone);
                            return true;
                        }

                        clone.setAmount(64);
                        inventory.setItem(slot, clone);
                    }
                }
            }
            return false;
        } else {
            if(emptySpots.size() > 0) {
                inventory.setItem(emptySpots.get(0), super.clone());
                return true;
            } else {
                return false;
            }
        }
    }

    public void updateCheque(Player player, int slot) {
        player.getInventory().setItem(slot, null);
        player.getInventory().setItem(slot, super.clone());
    }

    public void removeStack(Player player, int amount, boolean offHand) {
        Inventory inventory = player.getInventory();

        ItemStack item;
        if (compareVersion("1.9", ChequesLite.Conditions.GREATEROREQUAL))
            item = offHand ? player.getInventory().getItemInOffHand() : player.getInventory().getItemInMainHand();
        else
            item = player.getInventory().getItemInHand();

        if(item != null && item.getItemMeta().equals(super.getItemMeta()) && item.getData().equals(super.getData())) {
            item.setAmount(Math.max(item.getAmount() - amount, 0));
            if(compareVersion("1.9", ChequesLite.Conditions.LESS))
                player.getInventory().setItemInHand(item);
            else if(compareVersion("1.11", ChequesLite.Conditions.LESS))
                if (offHand)
                    player.getInventory().setItemInOffHand(item);
                else
                    player.getInventory().setItemInMainHand(item);

            return;
        }

        for(int slot = 0; slot < 36; slot++) {
            item = inventory.getItem(slot);
            if (item != null && item.getItemMeta().equals(super.getItemMeta()) && item.getData().equals(super.getData())) {
                item.setAmount(Math.max(item.getAmount() - amount, 0));
                if(compareVersion("1.11", ChequesLite.Conditions.LESS))
                    player.getInventory().setItem(slot, item);

                break;
            }
        }
    }

    public void setNBTData(String tag, double value) {
        setNBTData(tag, String.valueOf(value));
    }

    public void setNBTData(String tag, UUID uuid) {
        setNBTData(tag, uuid.toString());
    }

    public void setNBTData(String tag, String data) {
        try {
            Object nmsItem = Objects.requireNonNull(getCraftBukkitClass("inventory.CraftItemStack")).getMethod("asNMSCopy", ItemStack.class).invoke(null, super.clone());

            Object hasTag = Objects.requireNonNull(getNMSClass("ItemStack")).getDeclaredMethod("hasTag").invoke(nmsItem);
            Object NBTCompound;
            if((Boolean) hasTag) {
                NBTCompound = Objects.requireNonNull(getNMSClass("ItemStack")).getMethod("getTag").invoke(nmsItem);
            } else {
                Constructor<?> NBTTagCompoundConstructor = Objects.requireNonNull(getNMSClass("NBTTagCompound")).getConstructor();
                NBTCompound = NBTTagCompoundConstructor.newInstance();
            }

            Constructor<?> NBTTagStringConstructor = Objects.requireNonNull(getNMSClass("NBTTagString")).getDeclaredConstructor(String.class);
            NBTTagStringConstructor.setAccessible(true);
            NBTCompound.getClass().getMethod("set", String.class, getNMSClass("NBTBase"))
                    .invoke(NBTCompound, tag, NBTTagStringConstructor.newInstance(data));

            nmsItem.getClass().getMethod("setTag", getNMSClass("NBTTagCompound")).invoke(nmsItem, NBTCompound);

            ItemStack taggedData = (ItemStack) Objects.requireNonNull(getCraftBukkitClass("inventory.CraftItemStack")).getMethod("asBukkitCopy", getNMSClass("ItemStack")).invoke(null, nmsItem);
            super.setItemMeta((taggedData).getItemMeta());
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public String getNBTData(String tag) {
        try {
            Object nmsItem = Objects.requireNonNull(getCraftBukkitClass("inventory.CraftItemStack")).getMethod("asNMSCopy", ItemStack.class).invoke(null, super.clone());

            Object hasTag = Objects.requireNonNull(getNMSClass("ItemStack")).getDeclaredMethod("hasTag").invoke(nmsItem);
            Object NBTCompound;
            if((Boolean) hasTag) {
                NBTCompound = Objects.requireNonNull(getNMSClass("ItemStack")).getMethod("getTag").invoke(nmsItem);
            } else {
                Constructor<?> NBTTagCompoundConstructor = Objects.requireNonNull(getNMSClass("NBTTagCompound")).getConstructor();
                NBTCompound = NBTTagCompoundConstructor.newInstance();
            }

            Object data = NBTCompound.getClass().getMethod("getString", String.class).invoke(NBTCompound, tag);
            return data.toString();
        } catch (Exception exception) {
            exception.printStackTrace();
            return null;
        }
    }

    public boolean hasNBTData(String tag) {
        try {
            Object nmsItem = Objects.requireNonNull(getCraftBukkitClass("inventory.CraftItemStack")).getMethod("asNMSCopy", ItemStack.class).invoke(null, super.clone());

            Object hasTag = Objects.requireNonNull(getNMSClass("ItemStack")).getDeclaredMethod("hasTag").invoke(nmsItem);
            Object NBTCompound;
            if((Boolean) hasTag)
                NBTCompound = Objects.requireNonNull(getNMSClass("ItemStack")).getMethod("getTag").invoke(nmsItem);
            else
                return false;

            Object data = NBTCompound.getClass().getMethod("hasKey", String.class).invoke(NBTCompound, tag);
            return (Boolean) data;
        } catch (Exception exception) {
            exception.printStackTrace();
            return false;
        }
    }
}
