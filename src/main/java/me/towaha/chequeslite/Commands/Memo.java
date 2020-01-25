package me.towaha.chequeslite.Commands;

import me.towaha.chequeslite.ChequesLite;
import me.towaha.chequeslite.Classes.NBTItemStack;
import me.towaha.chequeslite.Messages;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

public class Memo {
    public Memo(ChequesLite main, CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Messages.ONLY_PLAYERS_CAN_EXECUTE);
            return;
        }

        Player player = (Player) sender;

        if(!player.hasPermission("chequeslite.memo")) {
            player.sendMessage(Messages.NO_PERMISSION);
            return;
        }

        if (args.length < 2) {
            player.sendMessage("§9Usage: §e/cheque memo §f<memo>" +
                    "\n§7You can enter 'clear' as memo to remove the memo");
            return;
        }

        NBTItemStack cheque = new NBTItemStack(main.compareVersion("1.9", ChequesLite.Conditions.GREATEROREQUAL) ? player.getInventory().getItemInMainHand() : player.getInventory().getItemInHand());

        if (!cheque.hasItemMeta() || !cheque.getItemMeta().hasLore() || !cheque.hasNBTData("creator") || !cheque.hasNBTData("memo")) {
            player.sendMessage(Messages.INVALID_CHEQUE);
            return;
        }

        if (args[1].equalsIgnoreCase("clear") && (!cheque.hasNBTData("memo") || cheque.getNBTData("memo").equalsIgnoreCase(""))) {
            player.sendMessage(Messages.MEMO_ALREADY_EMPTY);
            return;
        }

        ItemMeta meta = cheque.getItemMeta();
        List<String> lore = meta.getLore();
        if (cheque.hasNBTData("memo") && !cheque.getNBTData("memo").equals(""))
            lore.remove(1);

        String memo = "";
        if (!args[1].equalsIgnoreCase("clear")) {
            memo = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
            if(memo.length() > ChequesLite.MAX_MEMO_LENGTH) {
                player.sendMessage(Messages.MEMO_TOO_LONG);
                return;
            }

            lore.add(1, Messages.MEMO_LINE.replace("%memo%", memo));
        }
        meta.setLore(lore);
        cheque.setItemMeta(meta);

        cheque.setNBTData("memo", memo);
        cheque.updateCheque(player, player.getInventory().getHeldItemSlot());

        player.sendMessage(Messages.CHEQUE_MEMO_CHANGED);
    }
}
