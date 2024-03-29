package me.twoaster.chequeslite;

import me.twoaster.chequeslite.util.ItemStackBuilder;
import me.twoaster.chequeslite.commands.Cash;
import me.twoaster.chequeslite.commands.Create;
import me.twoaster.chequeslite.commands.Memo;
import me.twoaster.chequeslite.commands.Send;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChequesManager implements CommandExecutor, TabCompleter {
    private ChequesLite main;

    public ChequesManager(ChequesLite main) {
        this.main = main;
    }

    private TextComponent getHelpLine(String command, String subcommand, String args, String description) {
        StringBuilder visualText = new StringBuilder("§e/" + command + " ");
        visualText.append("§e").append(subcommand);
        if (args.length() > 0)
            visualText.append("§f ").append(args).append(" ");

        StringBuilder descriptionText = new StringBuilder("§e/" + command + " " + subcommand + " §6Help\n§r");

        if (description != null) {
            int characters = 0;
            int index = 0;
            for (String word : description.split(" ")) {
                if (characters >= 70) {
                    descriptionText.append("\n");
                    characters = 0;
                }

                characters += word.length();
                descriptionText.append(word);

                if (index < description.length() - 1)
                    descriptionText.append(" ");
            }
        }

        TextComponent component = new TextComponent(visualText.toString() + "\n");
        if (description != null)
            component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(descriptionText.toString()).create()));
        return component;
    }

    private void sendHelp(CommandSender sender) {
        if (!(sender instanceof Player)) /*Console or command block*/ {
            sender.sendMessage("§8---------------- §9Cheques Pro Help §8----------------");
            sender.sendMessage("§e/cheque send §f<amount> <player> [memo]");
            sender.sendMessage("§8------------------------------------------------");
            return;
        }

        boolean hasLine = false;
        TextComponent mainComponent = new TextComponent("§8---------------- §9Cheques Lite Help §8----------------\n");

        if (sender.hasPermission("chequeslite.create")) {
            hasLine = true;
            mainComponent.addExtra(getHelpLine("cheque", "create", "<amount> [memo]", Messages.getMessage(Messages.Keys.DESCRIPTION_CREATE)));
        }

        if (sender.hasPermission("chequeslite.send")) {
            hasLine = true;
            mainComponent.addExtra(getHelpLine("cheque", "send", "<amount> <player> [memo]", Messages.getMessage(Messages.Keys.DESCRIPTION_SEND)));
        }

        if (sender.hasPermission("chequeslite.memo")) {
            hasLine = true;
            mainComponent.addExtra(getHelpLine("cheque", "memo", "<memo>", Messages.getMessage(Messages.Keys.DESCRIPTION_MEMO)));
        }

        if (sender.hasPermission("chequeslite.cash")) {
            hasLine = true;
            mainComponent.addExtra(getHelpLine("cheque", "cash", "", Messages.getMessage(Messages.Keys.DESCRIPTION_CASH)));
        }

        mainComponent.addExtra("\n§7§oYou can hover over any of the above commands for help" +
                               "\n§8-------------------------------------------------");

        if (hasLine)
            ((Player) sender).spigot().sendMessage(mainComponent);
        else
            Messages.sendMessage(Messages.Keys.NO_PERMISSION, sender);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length >= 1 && !Arrays.asList(main.commands.get(ChequesLite.subCommand.help)).contains(args[0].toLowerCase()))
            if (Arrays.asList(main.commands.get(ChequesLite.subCommand.create)).contains(args[0].toLowerCase()))
                new Create(main, sender, args);
            else if (Arrays.asList(main.commands.get(ChequesLite.subCommand.send)).contains(args[0].toLowerCase()))
                new Send(main, sender, args);
            else if (Arrays.asList(main.commands.get(ChequesLite.subCommand.memo)).contains(args[0].toLowerCase()))
                new Memo(main, sender, args);
            else if (Arrays.asList(main.commands.get(ChequesLite.subCommand.cash)).contains(args[0].toLowerCase()))
                new Cash(main, sender);
            else
                Messages.sendMessage(Messages.Keys.COMMAND_DOESNT_EXIST, sender);
        else
            sendHelp(sender);

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> options = new ArrayList<>();
            if (sender.hasPermission("chequeslite.create"))
                options.addAll(Arrays.asList(main.commands.get(ChequesLite.subCommand.create)));

            if (sender.hasPermission("chequeslite.send"))
                options.addAll(Arrays.asList(main.commands.get(ChequesLite.subCommand.send)));

            if (sender.hasPermission("chequeslite.memo"))
                options.addAll(Arrays.asList(main.commands.get(ChequesLite.subCommand.memo)));

            if (sender.hasPermission("chequeslite.cash"))
                options.addAll(Arrays.asList(main.commands.get(ChequesLite.subCommand.cash)));

            return main.getAvailableOptions(options, args[0]);
        } else if (args.length == 3) {
            if (args[0].equalsIgnoreCase("send") && sender.hasPermission("chequeslite.send")) {
                List<String> options = new ArrayList<>();
                for (Player player : main.getServer().getOnlinePlayers())
                    options.add(player.getName());

                return main.getAvailableOptions(options, args[2]);
            }
        }
        return new ArrayList<>();
    }

    public ItemStack createCheque(CommandSender sender, double amount, String memo) {
        if (amount < main.getConfig().getInt("min_cheque_value") || amount < 0)
            return null;

        String currency = ChequesLite.economy.format(amount);

        List<String> lore = new ArrayList<>();

        String signer;
        if (sender == null)
            signer = Messages.getMessage(Messages.Keys.UNKNOWN_SENDER);
        else if (sender.getName().equalsIgnoreCase("CONSOLE"))
            signer = Messages.getMessage(Messages.Keys.CONSOLE_SENDER);
        else
            signer = sender.getName();

        lore.add(Messages.getMessage(Messages.Keys.WORTH_LINE).replace("%worth%", currency));
        if (memo != null && !memo.equals(""))
            lore.add(Messages.getMessage(Messages.Keys.MEMO_LINE).replace("%memo%", memo));
        lore.add(" ");
        lore.add(Messages.getMessage(Messages.Keys.SIGNER_LINE).replace("%signer%", signer));

        return new ItemStackBuilder(Material.PAPER)
                .displayName(Messages.getMessage(Messages.Keys.CHEQUE_NAME).replace("%worth%", currency))
                .lore(lore)
                .build();
    }

    public boolean hasEnoughMoneyEssentials(Player player, double amount) {
        File essentialsConfig = new File(main.getDataFolder().getParent() + "/Essentials/config.yml");
        if (essentialsConfig.exists()) {
            double minMoney = YamlConfiguration.loadConfiguration(essentialsConfig).getDouble("min-money");
            return ChequesLite.economy.getBalance(player) - amount >= minMoney;
        }
        return false;
    }
}
