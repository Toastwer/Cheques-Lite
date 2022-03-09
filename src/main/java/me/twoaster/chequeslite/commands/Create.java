package me.twoaster.chequeslite.commands;

import me.twoaster.chequeslite.ChequesLite;
import me.twoaster.chequeslite.util.ItemStackUtil;
import me.twoaster.chequeslite.Messages;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class Create {
    public Create(ChequesLite main, CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            Messages.sendMessage(Messages.Keys.ONLY_PLAYERS_CAN_EXECUTE, sender);
            return;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("chequeslite.create")) {
            Messages.sendMessage(Messages.Keys.NO_PERMISSION, player);
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
            Messages.sendMessage(Messages.Keys.INVALID_CHEQUE_VALUE, player);
            return;
        }

        if (worth < main.getConfig().getInt("min_cheque_value")) {
            sender.sendMessage(Messages.getMessage(Messages.Keys.CHEQUE_NOT_WORTH_ENOUGH).replace("%min%", ChequesLite.economy.format(main.getConfig().getInt("min_cheque_value"))));
            return;
        }

        if (!main.chequesManager.hasEnoughMoneyEssentials(player, worth)) {
            Messages.sendMessage(Messages.Keys.NOT_ENOUGH_MONEY, player);
            return;
        }

        String memo = "";
        if (args.length >= 3)
            memo = String.join(" ", Arrays.copyOfRange(args, 2, args.length));

        if (!memo.equals("") && memo.length() > main.getConfig().getInt("max_memo_length")) {
            Messages.sendMessage(Messages.Keys.MEMO_TOO_LONG, player);
            return;
        }

        ItemStack cheque = new ItemStack(main.chequesManager.createCheque(player, worth, memo));

        cheque = ItemStackUtil.setNBTData(cheque, "worth", worth);
        cheque = ItemStackUtil.setNBTData(cheque, "creator", player.getUniqueId());
        cheque = ItemStackUtil.setNBTData(cheque, "memo", memo);

        if (!ItemStackUtil.spawnForPlayer(cheque, player)) {
            Messages.sendMessage(Messages.Keys.INVENTORY_FULL, player);
            return;
        }

        ChequesLite.economy.withdrawPlayer(player, worth);

        Messages.sendMessage(Messages.Keys.CHEQUE_CREATED, player);
    }
}
