package me.twoaster.chequeslite.commands;

import me.twoaster.chequeslite.ChequesLite;
import me.twoaster.chequeslite.util.ItemStackUtil;
import me.twoaster.chequeslite.Messages;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class Send {
    public Send(ChequesLite main, CommandSender sender, String[] args) {
        if (!sender.hasPermission("chequeslite.send")) {
            Messages.sendMessage(Messages.Keys.NO_PERMISSION, sender);
            return;
        }

        if (args.length < 3) {
            sender.sendMessage("§9Usage: §e/cheque send §f<amount> <player> [memo]");
            return;
        }

        double worth;
        try {
            worth = Double.parseDouble(args[1]);
        } catch (NumberFormatException exception) {
            Messages.sendMessage(Messages.Keys.INVALID_CHEQUE_VALUE, sender);
            return;
        }

        int min = main.getConfig().getInt("min_cheque_value");
        if (worth < min) {
            sender.sendMessage(Messages.getMessage(Messages.Keys.CHEQUE_NOT_WORTH_ENOUGH).replace("%min%", ChequesLite.economy.format(min)));
            return;
        }

        if (sender instanceof Player && !main.chequesManager.hasEnoughMoneyEssentials((Player) sender, worth)) {
            Messages.sendMessage(Messages.Keys.NOT_ENOUGH_MONEY, sender);
            return;
        }

        Player target = main.getServer().getPlayer(args[2]);
        if (target == null) {
            Messages.sendMessage(Messages.Keys.TARGET_IS_OFFLINE, sender);
            return;
        } else if (target.getName().equalsIgnoreCase(sender.getName())) {
            Messages.sendMessage(Messages.Keys.CANNOT_SEND_TO_SELF, sender);
            return;
        }

        String memo = "";
        if (args.length >= 4)
            memo = String.join(" ", Arrays.copyOfRange(args, 3, args.length));

        if (!memo.equals("") && memo.length() > main.getConfig().getInt("max_memo_length")) {
            Messages.sendMessage(Messages.Keys.MEMO_TOO_LONG, sender);
            return;
        }

        ItemStack cheque = new ItemStack(main.chequesManager.createCheque(sender, worth, memo));

        cheque = ItemStackUtil.setNBTData(cheque, "worth", worth);
        cheque = ItemStackUtil.setNBTData(cheque, "creator", sender instanceof Player ? ((Player) sender).getUniqueId().toString() : sender.getName());
        cheque = ItemStackUtil.setNBTData(cheque, "memo", memo);

        ItemStackUtil.spawnForPlayer(cheque, target);

        if (sender instanceof Player)
            ChequesLite.economy.withdrawPlayer((Player) sender, worth);

        sender.sendMessage(Messages.getMessage(Messages.Keys.CHEQUE_SENT).replace("%target%", target.getName()));
    }
}
