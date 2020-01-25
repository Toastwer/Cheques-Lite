package me.towaha.chequeslite.Commands;

import me.towaha.chequeslite.ChequesLite;
import me.towaha.chequeslite.Classes.NBTItemStack;
import me.towaha.chequeslite.Messages;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class Create {
    public Create(ChequesLite main, CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Messages.ONLY_PLAYERS_CAN_EXECUTE);
            return;
        }

        Player player = (Player) sender;

        if(!player.hasPermission("chequeslite.create")) {
            player.sendMessage(Messages.NO_PERMISSION);
            return;
        }

        if (args.length < 2) {
            player.sendMessage("§9Usage: §e/cheque create §f<amount> [memo]");
            return;
        }

        double worth;
        try {
            worth = Double.parseDouble(args[1]);
        } catch (NumberFormatException ex) {
            player.sendMessage(Messages.INVALID_CHEQUE_VALUE);
            return;
        }

        if (worth < ChequesLite.MIN_CHEQUE_VALUE) {
            sender.sendMessage(Messages.CHEQUE_NOT_WORTH_ENOUGH.replace("%min%", ChequesLite.economy.format(ChequesLite.MIN_CHEQUE_VALUE)));
            return;
        }

        String memo = "";
        if (args.length >= 3)
            memo = String.join(" ", Arrays.copyOfRange(args, 2, args.length));

        if (!memo.equals("") && memo.length() > ChequesLite.MAX_MEMO_LENGTH) {
            player.sendMessage(Messages.MEMO_TOO_LONG);
            return;
        }

        NBTItemStack cheque = new NBTItemStack(main.chequesManager.createCheque(player, worth, memo));

        cheque.setNBTData("worth", worth);
        cheque.setNBTData("creator", player.getUniqueId());
        cheque.setNBTData("memo", memo);

        if(!cheque.spawnForPlayer(player)) {
            player.sendMessage(Messages.INVENTORY_FULL);
            return;
        }

        ChequesLite.economy.withdrawPlayer(player, worth);

        player.sendMessage(Messages.CHEQUE_CREATED);
    }
}
