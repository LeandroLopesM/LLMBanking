package com.ethan.bank;

import java.text.NumberFormat;
import java.util.Arrays;

public class Utils {
    private static final String ESC = "\u001b";
    public static final int CLIENT = 0;
    public static final int ADMIN = 1;
    private static final String[] BOLD = { ESC + "[1m", ESC + "[22m"};
    private static final String[] ITALIC = { ESC + "[2m", ESC + "[22m"};
    private static final String[] UNDERLINE = { ESC + "[3m", ESC + "[23m"};

    private static final String[] clientCommands = {
      "deposit", "withdraw"
    };

    private static final String[] adminCommands = {
        "new", "balance", "name", "pass", "password", "key"
    };

    public static boolean isNumeric(String str) {
        return str.matches("\\G[0-9]+$");
    }

    public static boolean isHelpCommand(String comm, int mode) {
        if(mode == CLIENT) {
            return Arrays.asList(clientCommands).contains(comm.toLowerCase());
        } else if(mode == ADMIN) {
            return Arrays.asList(adminCommands).contains(comm.toLowerCase());
        }
        return false;
    }


    public static String style(String format) {
        if(format.startsWith("**") && format.endsWith("**")) {
            return BOLD[0] + format.replace("**", "") + BOLD[1];
        }
        else if(format.startsWith("*") && format.endsWith("*")) {
            return ITALIC[0] + format.replace("*", "") + ITALIC[1];
        }
        else if(format.startsWith("__") && format.endsWith("__")) {
            return UNDERLINE[0] + format.replace("*", "") + UNDERLINE[1];
        }
        return "Invalid format";
    }

    public static String dollarFormat(long value) {
        return NumberFormat.getCurrencyInstance().format(value);
    }
}
