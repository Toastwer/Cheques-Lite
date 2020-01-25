package me.towaha.chequeslite;

public class Messages {
    public static String NO_PERMISSION = "§cYou do not have the permission to execute that command.";
    public static String NO_PERMISSION_CLICK = "§cYou do not have the permission to cash the check via clicking.";
    public static String COMMAND_DOESNT_EXIST = "§cThat command does not exist.";
    public static String INVENTORY_FULL = "§cYour inventory is full.";
    public static String ONLY_PLAYERS_CAN_EXECUTE = "§cOnly players can execute that command.";
    public static String TARGET_IS_OFFLINE = "§cThe target player could not be found.";
    public static String CANNOT_SEND_TO_SELF = "§cYou can not send cheques to yourself.";

    public static String MEMO_ALREADY_EMPTY = "§cThe memo is already empty.";
    public static String MEMO_TOO_LONG = "§cThere is not enough space for that memo.";

    public static String INVALID_CHEQUE = "§cThat is not a valid cheque.";
    public static String INVALID_CHEQUE_VALUE = "§cThat is not a valid cheque value.";
    public static String CHEQUE_NOT_WORTH_ENOUGH = "§cCheques need to be worth at least %min%.";


    public static String CHEQUE_CREATED = "§aCreated a new cheque.";
    public static String CHEQUE_MEMO_CHANGED = "§aMemo has successfully been set.";
    public static String CHEQUE_SENT = "§aA cheque has been sent to %target%.";
    public static String CHEQUE_CASHED = "§aYou have cashed a check worth %worth%.";
    public static String CHEQUE_CASHED_MULTIPLE = "§aYou have cashed %count% cheques worth %worth%. §7(total: %total%)";


    public static String CHEQUE_NAME = "§aCheck of §2%worth%";

    public static String WORTH_LINE = "§eValue: §2%worth%";
    public static String MEMO_LINE = "§eMemo: §f%memo%";
    public static String SIGNER_LINE = "§eSigner: §f%signer%";
    public static String UNKNOWN_SENDER = "§oUnknown";
    public static String CONSOLE_SENDER = "§oServer";
    
    public static String DESCRIPTION_CREATE = "§fThis commands creates a check. You can walk up to your target player and click on them to open up an interface. There you can confirm your action. Optionally, you can add a memo to provide a short description as to what the check is for.";
    public static String DESCRIPTION_SEND = "§fDirectly sends a check to someone. Optionally, you can add a memo to provide a short description as to what the check is for.";
    public static String DESCRIPTION_MEMO = "§fEdits the memo of the currently held cheque. You can enter 'clear' in order to remove the message.";
    public static String DESCRIPTION_CASH = "§fCashes the cheque that you're currently holding in your hand.";
}
