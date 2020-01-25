package me.towaha.chequeslite.Commands;

import me.towaha.chequeslite.ChequesLite;
import me.towaha.chequeslite.Classes.NBTItemStack;
import me.towaha.chequeslite.Messages;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Cash {
    public Cash(ChequesLite main, CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Messages.ONLY_PLAYERS_CAN_EXECUTE);
            return;
        }

        Player player = (Player) sender;

        if(!player.hasPermission("chequeslite.cashcommand")) {
            player.sendMessage(Messages.NO_PERMISSION);
            return;
        }

        NBTItemStack cheque = new NBTItemStack(main.compareVersion("1.9", ChequesLite.Conditions.GREATEROREQUAL) ? player.getInventory().getItemInMainHand() : player.getInventory().getItemInHand());

        if (!cheque.hasItemMeta() || !cheque.getItemMeta().hasDisplayName() || !cheque.getItemMeta().hasLore() || !cheque.hasNBTData("creator")) {
            player.sendMessage(Messages.INVALID_CHEQUE);
            return;
        }

        double worth;
        try {
            worth = Double.parseDouble(cheque.getNBTData("worth"));
        } catch (NumberFormatException exception) {
            player.sendMessage(Messages.INVALID_CHEQUE_VALUE);
            return;
        }

        double total = worth * cheque.getAmount();
        cheque.removeStack(player, cheque.getAmount());

        ChequesLite.economy.depositPlayer(player, worth);

        if(cheque.getAmount() > 1)
            player.sendMessage(Messages.CHEQUE_CASHED_MULTIPLE
                    .replace("%worth%", ChequesLite.economy.format(worth))
                    .replace("%count%", String.valueOf(cheque.getAmount()))
                    .replace("%total%", ChequesLite.economy.format(total)));
        else
            player.sendMessage(Messages.CHEQUE_CASHED.replace("%worth%", ChequesLite.economy.format(worth)));
    }
}
