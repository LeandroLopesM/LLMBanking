package com.ethan.bank;

import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

/*
    TBD:
        - Admin mode
        - - Something?????
        - hashing system for password
        - actually hide the password input somehow
        - figure out json parsing
        - comment this mess of a code
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

            if(Arrays.asList(command).contains("?") && Utils.isHelpCommand(command[0], Utils.CLIENT)) {
                UserSession.getHelp(command[0].toLowerCase());
                continue;
            }

            switch (command[0].trim().toLowerCase()) {

                case "withdraw" -> {
                    if(!Utils.isNumeric(command[1].trim())) {
                        System.out.println(Utils.style("**ERROR: Incorrect usage of 'withdraw' (argument passed is not integer)**"));
                        System.out.println(Utils.style("**Please use 'withdraw ?' or 'withdraw help' for detailed help about the command**"));
                        continue;
                    }

                    int value;

                    value = Integer.parseInt(command[1].trim());
                    client.withdraw(value);
                }

                case "deposit" -> {
                    if(!Utils.isNumeric(command[1])) {
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

                case "history", "log" -> client.printLog();

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

                case "admin", "sysadmin", "enter" -> {
                    int key = (Utils.isNumeric(command[1]))? Integer.parseInt(command[1]) : -1;

                    if(client.checkAdmin()) {
                        client.admin(key);
                    }

                    AdminSession sysadmin = new AdminSession(client, key);

                    boolean exitAdmin = false;
                    while(!exitAdmin) {
                        System.out.print("SYS@" + client.getCurrentUser()[0] + "> ");
                        command = scn.next().split(" ");

                        if(Arrays.asList(command).contains("?") && Utils.isHelpCommand(command[0], Utils.ADMIN)) {
                            UserSession.getHelp("admin." + command[Arrays.asList(command).lastIndexOf("?") - 1]);
                            continue;
//                            Yes overcomplicated but its basically to save me the trouble when I eventually add more functionality to admin mode
                        }

                        switch(command[0].toLowerCase().trim()) {

                            case "exit" -> {
                                sysadmin.exitMode();
                                exitAdmin = true;
                            }

                            case "help", "?" -> System.out.println(Utils.style(
                                    """
                                            **The following is a list of available commands
                                            new < Name[String] > < Password[String] > < Balance[long/int] >  | Creates a new account
                                            set balance < Name[String] > < newBalance[long/int] >            | Changes the balance of the given account.
                                            set name < oldName[String] > < newName[String] >                 | Changes the name of the given account.
                                            set password < Name[String] > < newPass[String] >                | Changes the password of the given account.**
                                    """
                            ));

                            case "new" -> {
                                if(command.length != 4 || !Utils.isNumeric(command[3])) {
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

                            case "set" -> {
                                if(command[2].equals("?") && Utils.isHelpCommand(command[1], Utils.ADMIN)) {
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

                                        sysadmin.setBalance(name, newVal);
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
