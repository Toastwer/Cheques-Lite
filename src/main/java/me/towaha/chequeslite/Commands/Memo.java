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
            Messages.sendMessage(Messages.Keys.ONLY_PLAYERS_CAN_EXECUTE, sender);
            return;
        }

        Player player = (Player) sender;

        if(!player.hasPermission("chequeslite.memo")) {
            Messages.sendMessage(Messages.Keys.NO_PERMISSION, player);
            return;
        }

        if (args.length < 2) {
            player.sendMessage("§9Usage: §e/cheque memo §f<memo>" +
                    "\n§7You can enter 'clear' as memo to remove the memo");
            return;
        }

        NBTItemStack cheque = new NBTItemStack(main.compareVersion("1.9", ChequesLite.Conditions.GREATEROREQUAL) ? player.getInventory().getItemInMainHand() : player.getInventory().getItemInHand());

        if (!cheque.hasItemMeta() || !cheque.getItemMeta().hasLore() || !cheque.hasNBTData("creator") || !cheque.hasNBTData("memo")) {
            Messages.sendMessage(Messages.Keys.INVALID_CHEQUE, player);
            return;
        }

        if (args[1].equalsIgnoreCase("clear") && (!cheque.hasNBTData("memo") || cheque.getNBTData("memo").equalsIgnoreCase(""))) {
            Messages.sendMessage(Messages.Keys.MEMO_ALREADY_EMPTY, player);
            return;
        }

        ItemMeta meta = cheque.getItemMeta();
        List<String> lore = meta.getLore();
        if (cheque.hasNBTData("memo") && !cheque.getNBTData("memo").equals(""))
            lore.remove(1);

        String memo = "";
        if (!args[1].equalsIgnoreCase("clear")) {
            memo = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
            if(memo.length() > main.getConfig().getInt("max_memo_length")) {
                Messages.sendMessage(Messages.Keys.MEMO_TOO_LONG, player);
                return;
            }

            lore.add(1, Messages.getMessage(Messages.Keys.MEMO_LINE).replace("%memo%", memo));
        }
        meta.setLore(lore);
        cheque.setItemMeta(meta);

        cheque.setNBTData("memo", memo);
        cheque.updateCheque(player, player.getInventory().getHeldItemSlot());

        Messages.sendMessage(Messages.Keys.CHEQUE_MEMO_CHANGED, player);
    }
}
