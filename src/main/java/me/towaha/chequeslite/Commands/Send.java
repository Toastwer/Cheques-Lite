package me.towaha.chequeslite.Commands;

import me.towaha.chequeslite.ChequesLite;
import me.towaha.chequeslite.Classes.NBTItemStack;
import me.towaha.chequeslite.Messages;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class Send {
    public Send(ChequesLite main, CommandSender sender, String[] args) {
        if(!sender.hasPermission("chequeslite.send")) {
            sender.sendMessage(Messages.NO_PERMISSION);
            return;
        }

        if (args.length < 3)
            sender.sendMessage("§9Usage: §e/cheque send §f<amount> <player> [memo]");

        double worth;
        try {
            worth = Double.parseDouble(args[1]);
        } catch (NumberFormatException exception) {
            sender.sendMessage(Messages.INVALID_CHEQUE_VALUE);
            return;
        }

        int min = ChequesLite.MIN_CHEQUE_VALUE;
        if (worth < min) {
            sender.sendMessage(Messages.CHEQUE_NOT_WORTH_ENOUGH.replace("%min%", ChequesLite.economy.format(min)));
            return;
        }

        Player target = main.getServer().getPlayer(args[2]);
        if (target == null) {
            sender.sendMessage(Messages.TARGET_IS_OFFLINE);
            return;
        } else if (target.getName().equalsIgnoreCase(sender.getName())) {
            sender.sendMessage(Messages.CANNOT_SEND_TO_SELF);
            return;
        }

        String memo = "";
        if (args.length >= 4)
            memo = String.join(" ", Arrays.copyOfRange(args, 3, args.length));

        if (!memo.equals("") && memo.length() > ChequesLite.MAX_MEMO_LENGTH) {
            sender.sendMessage(Messages.MEMO_TOO_LONG);
            return;
        }

        NBTItemStack cheque = new NBTItemStack(main.chequesManager.createCheque(sender, worth, memo));

        cheque.setNBTData("worth", worth);
        cheque.setNBTData("creator", sender instanceof Player ? ((Player) sender).getUniqueId().toString() : sender.getName());
        cheque.setNBTData("memo", memo);

        cheque.spawnForPlayer(target);

        if(sender instanceof Player)
            ChequesLite.economy.withdrawPlayer((Player) sender, worth);

        sender.sendMessage(Messages.CHEQUE_SENT.replace("%target%", target.getName()));
    }
}
