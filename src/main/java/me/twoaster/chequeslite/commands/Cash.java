package me.twoaster.chequeslite.commands;

import me.twoaster.chequeslite.ChequesLite;
import me.twoaster.chequeslite.util.ItemStackUtil;
import me.twoaster.chequeslite.Messages;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Cash {
    public Cash(ChequesLite main, CommandSender sender) {
        if (!(sender instanceof Player)) {
            Messages.sendMessage(Messages.Keys.ONLY_PLAYERS_CAN_EXECUTE, sender);
            return;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("chequeslite.cashcommand")) {
            Messages.sendMessage(Messages.Keys.NO_PERMISSION, player);
            return;
        }

        boolean offHand = false;
        ItemStack item;
        if (ChequesLite.compareVersion("1.9", ChequesLite.Conditions.GREATEROREQUAL)) {
            ItemStack mainHand = player.getInventory().getItemInMainHand();
            if (mainHand != null && mainHand.getType() == Material.PAPER) {
                item = mainHand;
            } else {
                item = player.getInventory().getItemInOffHand();
                offHand = true;
            }
        } else {
            item = player.getInventory().getItemInHand();
        }

        if (!item.hasItemMeta() || !item.getItemMeta().hasDisplayName() || !item.getItemMeta().hasLore() || !ItemStackUtil.hasNBTData(item, "creator")) {
            Messages.sendMessage(Messages.Keys.INVALID_CHEQUE, player);
            return;
        }

        double worth;
        try {
            worth = Double.parseDouble(ItemStackUtil.getNBTData(item, "worth"));
        } catch (NumberFormatException exception) {
            Messages.sendMessage(Messages.Keys.INVALID_CHEQUE_VALUE, player);
            return;
        }

        int amount = item.getAmount();
        double total = worth * amount;
        ItemStackUtil.removeStack(item, player, item.getAmount(), offHand);

        ChequesLite.economy.depositPlayer(player, worth);

        if (amount > 1)
            player.sendMessage(Messages.getMessage(Messages.Keys.CHEQUE_CASHED_MULTIPLE)
                    .replace("%worth%", ChequesLite.economy.format(worth))
                    .replace("%count%", String.valueOf(amount))
                    .replace("%total%", ChequesLite.economy.format(total)));
        else
            player.sendMessage(Messages.getMessage(Messages.Keys.CHEQUE_CASHED).replace("%worth%", ChequesLite.economy.format(worth)));
    }
}
