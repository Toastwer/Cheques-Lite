package me.towaha.chequeslite.Commands;

import me.towaha.chequeslite.ChequesLite;
import me.towaha.chequeslite.Classes.NBTItemStack;
import me.towaha.chequeslite.Messages;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Cash {
    public Cash(ChequesLite main, CommandSender sender) {
        if (!(sender instanceof Player)) {
            Messages.sendMessage(Messages.Keys.ONLY_PLAYERS_CAN_EXECUTE, sender);
            return;
        }

        Player player = (Player) sender;

        if(!player.hasPermission("chequeslite.cashcommand")) {
            Messages.sendMessage(Messages.Keys.NO_PERMISSION, player);
            return;
        }

        NBTItemStack cheque = new NBTItemStack(main.compareVersion("1.9", ChequesLite.Conditions.GREATEROREQUAL) ? player.getInventory().getItemInMainHand() : player.getInventory().getItemInHand());

        if (!cheque.hasItemMeta() || !cheque.getItemMeta().hasDisplayName() || !cheque.getItemMeta().hasLore() || !cheque.hasNBTData("creator")) {
            Messages.sendMessage(Messages.Keys.INVALID_CHEQUE, player);
            return;
        }

        double worth;
        try {
            worth = Double.parseDouble(cheque.getNBTData("worth"));
        } catch (NumberFormatException exception) {
            Messages.sendMessage(Messages.Keys.INVALID_CHEQUE_VALUE, player);
            return;
        }

        double total = worth * cheque.getAmount();
        cheque.removeStack(player, cheque.getAmount());

        ChequesLite.economy.depositPlayer(player, worth);

        if(cheque.getAmount() > 1)
            player.sendMessage(Messages.getMessage(Messages.Keys.CHEQUE_CASHED_MULTIPLE)
                    .replace("%worth%", ChequesLite.economy.format(worth))
                    .replace("%count%", String.valueOf(cheque.getAmount()))
                    .replace("%total%", ChequesLite.economy.format(total)));
        else
            player.sendMessage(Messages.getMessage(Messages.Keys.CHEQUE_CASHED).replace("%worth%", ChequesLite.economy.format(worth)));
    }
}
