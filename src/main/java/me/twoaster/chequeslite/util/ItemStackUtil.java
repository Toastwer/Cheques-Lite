package me.twoaster.chequeslite.util;

import me.twoaster.chequeslite.ChequesLite;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static me.twoaster.chequeslite.ChequesLite.compareVersion;

public class ItemStackUtil {

    public static boolean spawnForPlayer(ItemStack itemStack, Player player) {
        Inventory inventory = player.getInventory();

        List<Integer> emptySpots = new ArrayList<>();
        List<Integer> sameSpots = new ArrayList<>();

        for (int slot = 0; slot < 36; slot++) {
            if (inventory.getItem(slot) == null)
                emptySpots.add(slot);
            else if (inventory.getItem(slot).getItemMeta().equals(itemStack.getItemMeta()) && inventory.getItem(slot).getData().equals(itemStack.getData()))
                sameSpots.add(slot);
        }

        if (sameSpots.size() > 0) {
            int freeAmount = 64 - inventory.getItem(sameSpots.get(0)).getAmount();
            if (freeAmount >= itemStack.getAmount()) {
                ItemStack clone = itemStack.clone();
                clone.setAmount(itemStack.getAmount() + inventory.getItem(sameSpots.get(0)).getAmount());
                inventory.setItem(sameSpots.get(0), clone);
                return true;
            }

            freeAmount = 0;
            for (int sameSpot : sameSpots)
                freeAmount += 64 - inventory.getItem(sameSpot).getAmount();

            int amountLeft = itemStack.getAmount();
            if (freeAmount >= itemStack.getAmount()) {
                for (int slot : sameSpots) {
                    ItemStack clone = itemStack.clone();

                    int canBeAdded = 64 - inventory.getItem(slot).getAmount();
                    amountLeft -= canBeAdded;
                    if (amountLeft <= 0) {
                        clone.setAmount(inventory.getItem(slot).getAmount() + amountLeft + canBeAdded);
                        inventory.setItem(slot, clone);
                        return true;
                    }

                    clone.setAmount(64);
                    inventory.setItem(slot, clone);
                }

                return true;
            } else {
                if (emptySpots.size() > 0) {
                    for (int slot : sameSpots) {
                        ItemStack clone = itemStack.clone();

                        int canBeAdded = 64 - inventory.getItem(slot).getAmount();
                        amountLeft -= canBeAdded;
                        if (amountLeft <= 0) {
                            clone.setAmount(inventory.getItem(slot).getAmount() + amountLeft + canBeAdded);
                            inventory.setItem(slot, clone);
                            return true;
                        }

                        clone.setAmount(64);
                        inventory.setItem(slot, clone);
                    }

                    for (int slot : emptySpots) {
                        ItemStack clone = itemStack.clone();

                        int canBeAdded = 64;
                        amountLeft -= canBeAdded;
                        if (amountLeft <= 0) {
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
            if (emptySpots.size() > 0) {
                inventory.setItem(emptySpots.get(0), itemStack.clone());
                return true;
            } else {
                return false;
            }
        }
    }

    public static void updateCheque(ItemStack itemStack, Player player, int slot) {
        player.getInventory().setItem(slot, null);
        player.getInventory().setItem(slot, itemStack.clone());
    }

    public static void removeStack(ItemStack itemStack, Player player, int amount, boolean offHand) {
        Inventory inventory = player.getInventory();

        ItemStack item;
        if (compareVersion("1.9", ChequesLite.Conditions.GREATEROREQUAL))
            item = offHand ? player.getInventory().getItemInOffHand() : player.getInventory().getItemInMainHand();
        else
            item = player.getInventory().getItemInHand();

        if (item != null && item.getItemMeta().equals(itemStack.getItemMeta()) && item.getData().equals(itemStack.getData())) {
            item.setAmount(Math.max(item.getAmount() - amount, 0));
            if (compareVersion("1.9", ChequesLite.Conditions.LESS))
                player.getInventory().setItemInHand(item);
            else if (compareVersion("1.11", ChequesLite.Conditions.LESS))
                if (offHand)
                    player.getInventory().setItemInOffHand(item);
                else
                    player.getInventory().setItemInMainHand(item);

            return;
        }

        for (int slot = 0; slot < 36; slot++) {
            item = inventory.getItem(slot);
            if (item != null && item.getItemMeta().equals(itemStack.getItemMeta()) && item.getData().equals(itemStack.getData())) {
                item.setAmount(Math.max(item.getAmount() - amount, 0));
                if (compareVersion("1.11", ChequesLite.Conditions.LESS))
                    player.getInventory().setItem(slot, item);

                break;
            }
        }
    }

    public static ItemStack setNBTData(ItemStack itemStack, String tag, double value) {
        return setNBTData(itemStack, tag, String.valueOf(value));
    }

    public static ItemStack setNBTData(ItemStack itemStack, String tag, UUID uuid) {
        return setNBTData(itemStack, tag, uuid.toString());
    }

    public static ItemStack setNBTData(ItemStack itemStack, String tag, String data) {
        return NBTEditor.set(itemStack, data, tag);
    }

    public static String getNBTData(ItemStack itemStack, String tag) {
        return NBTEditor.getString(itemStack, tag);
    }

    public static boolean hasNBTData(ItemStack itemStack, String tag) {
        return NBTEditor.contains(itemStack, tag);
    }
}
