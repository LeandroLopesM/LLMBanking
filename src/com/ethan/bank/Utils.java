package com.ethan.bank;

import java.text.NumberFormat;

public class Utils {
    private static final String ESC = "\u001b";
    private static final String[] BOLD = { ESC + "[1m", ESC + "[22m"};
    private static final String[] ITALIC = { ESC + "[2m", ESC + "[22m"};
    private static final String[] UNDERLINE = { ESC + "[3m", ESC + "[23m"};

    public static boolean isNumeric(String str) {
        return str.matches("\\G[0-9]+$");
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
