package me.towaha.chequeslite;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.yaml.snakeyaml.parser.ParserException;

import java.io.File;
import java.util.HashMap;

public class Messages {
    private YamlConfiguration config;

    public Messages(ChequesLite main) {
        loadConfig(main);

        for(Keys key : Keys.values()) {
            if (key != Keys.INVALID_CLICK_CHEQUE && !config.isSet(key.toString().toLowerCase())) {
                main.getLogger().warning("The message " + key.toString().toLowerCase() + " could not be found in messages.yml, message will not show up.");
            } else {
                if(key != Keys.INVALID_CHEQUE_VALUE || main.getConfig().getBoolean("invalid_cheque_click_msg"))
                    messages.put(Keys.INVALID_CLICK_CHEQUE, config.getString(Keys.INVALID_CHEQUE.toString().toLowerCase()));
            }
        }
    }

    private void loadConfig(ChequesLite main) {
        main.saveResource("messages.yml", false);

        config = YamlConfiguration.loadConfiguration(new File(main.getDataFolder(), "messages.yml"));
    }

    public enum Keys {
        NO_PERMISSION,
        NO_PERMISSION_CLICK,
        COMMAND_DOESNT_EXIST,
        INVENTORY_FULL,
        ONLY_PLAYERS_CAN_EXECUTE,
        TARGET_IS_OFFLINE,
        CANNOT_SEND_TO_SELF,
        MEMO_ALREADY_EMPTY,
        MEMO_TOO_LONG,
        INVALID_CHEQUE,
        INVALID_CLICK_CHEQUE,
        INVALID_CHEQUE_VALUE,
        CHEQUE_NOT_WORTH_ENOUGH,
        CHEQUE_CREATED,
        CHEQUE_MEMO_CHANGED,
        CHEQUE_SENT,
        CHEQUE_CASHED,
        CHEQUE_CASHED_MULTIPLE,
        CHEQUE_NAME,
        WORTH_LINE,
        MEMO_LINE,
        SIGNER_LINE,
        UNKNOWN_SENDER,
        CONSOLE_SENDER,
        DESCRIPTION_CREATE,
        DESCRIPTION_SEND,
        DESCRIPTION_MEMO,
        DESCRIPTION_CASH
    }

    private static HashMap<Keys, String> messages = new HashMap<>();
    public static void sendMessage(Keys key, Player player) {
        sendMessage(key, (CommandSender) player);
    }
    public static void sendMessage(Keys key, CommandSender sender) {
        if(messages.containsKey(key))
            sender.sendMessage(messages.get(key));
    }
    public static String getMessage(Keys key) {
        return messages.get(key);
    }
}
