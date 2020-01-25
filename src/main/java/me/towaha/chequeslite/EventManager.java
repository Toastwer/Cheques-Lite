package me.towaha.chequeslite;

import me.towaha.chequeslite.Classes.NBTItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class EventManager implements Listener {
    public EventManager() { }

    @EventHandler
    public void onPlayerClick(PlayerInteractEvent event) {
        if(!event.getAction().equals(Action.RIGHT_CLICK_AIR) && !event.getAction().equals(Action.RIGHT_CLICK_BLOCK))
            return;

        Player player = event.getPlayer();

        ItemStack item = event.getItem();
        if (!item.hasItemMeta() || !item.getItemMeta().hasDisplayName() || !item.getItemMeta().hasLore()) {
            player.sendMessage(Messages.INVALID_CHEQUE);
            return;
        }

        NBTItemStack cheque = new NBTItemStack(item);

        if(!player.hasPermission("chequeslite.cashclick")) {
            player.sendMessage(Messages.NO_PERMISSION_CLICK);
            return;
        }

        if (!cheque.hasNBTData("creator")) {
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

        ChequesLite.economy.depositPlayer(player, total);

        if(cheque.getAmount() > 1)
            player.sendMessage(Messages.CHEQUE_CASHED_MULTIPLE
                    .replace("%worth%", ChequesLite.economy.format(worth))
                    .replace("%count%", String.valueOf(cheque.getAmount()))
                    .replace("%total%", ChequesLite.economy.format(total)));
        else
            player.sendMessage(Messages.CHEQUE_CASHED.replace("%worth%", ChequesLite.economy.format(worth)));
    }
}
