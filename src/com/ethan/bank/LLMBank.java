package com.ethan.bank;

import java.io.IOException;
import java.util.Scanner;

/*
    TBD:
        - Admin mode
        - - Something?????
        - hashing system for password
        - actually hide the password input somehow
        - figure out json parsing
        - comment this mess of a code
        - fix logfile creation
        - Normalize function???
 */




public class LLMBank {
    public LLMBank() {
        Scanner scn = new Scanner(System.in);

        System.out.println("Please log in. If creating a new account please contact bank administrator");
        System.out.print("Username: ");
        String cName = scn.next();
        System.out.print("Password: ");
        String cPass = scn.next();

        UserSession client = new UserSession(cName, cPass);

        scn.useDelimiter("\\n");
        boolean shouldClose = false;
        String[] command;
        while(!shouldClose) {
            System.out.print("LLM@" + client.getCurrentUser()[0] + "> ");
            command = scn.next().split(" ");

            switch (command[0].trim().toLowerCase()) {

                case "withdraw" -> {
                    // { withdraw, command }

                    if(command[1].equals("?")) {
                        UserSession.getHelp("withdraw");
                        continue;
                    }
                    else if(!Utils.isNumeric(command[1].trim())) {
                        System.out.println(Utils.style("**ERROR: Incorrect usage of 'withdraw' (argument passed is not integer)**"));
                        System.out.println(Utils.style("**Please use 'withdraw ?' or 'withdraw help' for detailed help about the command**"));
                        continue;
                    }

                    int value;

                    value = Integer.parseInt(command[1].trim());
                    client.withdraw(value);
                }

                case "deposit" -> {
                    if(command[1].equals("?")) {
                        UserSession.getHelp("deposit");
                        continue;
                    }
                    else if(!Utils.isNumeric(command[1])) {
                        System.out.println(Utils.style("**ERROR: Incorrect usage of 'deposit' (argument passed is not integer)**"));
                        System.out.println(Utils.style("**Please use 'deposit ?' or 'deposit help' for detailed help about the command**"));
                        continue;
                    }

                    long value = Integer.parseInt(command[1]);
                    client.deposit(value);
                }

                case "balance" -> System.out.println("Your current account balance is: " + Utils.dollarFormat(client.getBalance()));

                case "exit", "logout" -> {
                    System.out.println("Exiting...");
                    shouldClose = true; // will fall down to the end of the while loop and into client.exit();
                }

                case "history", "log", "extract" -> client.printLog();

                case "help", "?" -> System.out.println(
                            """
                                    The following is a list of available command
                                    < Command[String] > help    | Get detailed help on a specific command
                                    withdraw < Value[int/long]> | Take/withdraw money from account
                                    deposit < Value[int/long] > | Give/deposit money into account
                                    admin < Key[int/long] >     | Enter admin mode
                                    balance                     | Print total account balance
                                    exit                        | Exits the program
                                    log                         | Print account actions log
                            """
                    );

                case "admin", "sysad", "enter" -> {
                    int key = (Utils.isNumeric(command[1]))? Integer.parseInt(command[1]) : -1;

                    if(client.checkAdmin()) {
                        client.admin(key);
                    }

                    AdminSession sysadmin = new AdminSession(client, key);

                    boolean exitAdmin = false;
                    while(!exitAdmin) {
                        System.out.print("SYS@" + client.getCurrentUser()[0] + "> ");
                        command = scn.next().split(" ");

                        switch(command[0].toLowerCase().trim()) {

                            case "exit" -> {
                                sysadmin.exitMode();
                                exitAdmin = true;
                            }

                            case "new" -> {

                                if(command[1].equals("?")) {
                                    UserSession.getHelp("admin.new");
                                    continue;
                                } else if(command.length != 4 || !Utils.isNumeric(command[3])) {
                                    System.out.println(Utils.style("**ERROR: Incorrect usage of admin." + command[0] + " (" +
                                        ((!Utils.isNumeric((command[3])))? "Expected int, got char" : "Missing arguments") +
                                    ")**"));
                                    System.out.println(Utils.style("**Please use '" + command[0] + " ?' or '" + command[0] + " help' for detailed documentation on the command.**"));
                                    continue;
                                }

                                String accName = command[1];
                                String accPass = command[2];
                                long accInitBalance = Integer.parseInt(command[3]);

                                sysadmin.newAccount(accName, accPass, accInitBalance);
                            }

                            case "set" -> { //, "swap", "change" -> {
//                                { set, value, param1, param2, :param3: }

                                if(command[2].equals("?")) {
                                    UserSession.getHelp("admin.set." + command[1].toLowerCase().trim());
                                    continue;
                                }

                                switch (command[1].toLowerCase()) {
                                    case "balance" -> {
                                        if(!Utils.isNumeric(command[3])) {
                                            System.out.println(Utils.style("**ERROR: Incorrect usage of 'admin.set.balance' (argument passed is not integer)**"));
                                            System.out.println(Utils.style("**Please use 'set balance ?' or 'set balance help' for detailed help about the command**"));
                                            continue;
                                        }

                                        String name = command[2];
                                        int newVal = Integer.parseInt(command[3]);

                                        try { sysadmin.setBalance(name, newVal); }
                                        catch (IOException e) { throw new RuntimeException(e); }
                                        System.out.println("Balance for account " + name + " successfully set to " + Utils.dollarFormat(newVal));
                                    }

                                    case "name" -> {
                                        String oldName = command[2];
                                        String newName = command[3];

                                        try { sysadmin.setName(oldName, newName); }
                                        catch (IOException e) { throw new RuntimeException(e); }
                                    }

                                    case "password", "pass", "key" -> {
                                        String name = command[2];
                                        String newPass = command[3];

                                        try { sysadmin.setPass(name, newPass); }
                                        catch (IOException e) { throw new RuntimeException(e); }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        client.exit();
        scn.close();
    }
}
