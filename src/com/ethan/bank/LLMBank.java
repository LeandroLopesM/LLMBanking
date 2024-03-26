package com.ethan.bank;

import java.io.IOException;
import java.util.Scanner;

/*
    TBD:
        - Admin mode
        -   - setting names
        -   - setting password
        - hashing system for password
        - actually hide the password input somehow
        - figure out json parsing
        - comment this mess of a code
        - add help command
        - add command help (error command format descriptors)
        - fix logfile creation
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

        boolean shouldClose = false;
        String command;
        while(!shouldClose) {
            System.out.print("LLM@" + client.getCurrentUser()[0] + "> ");
            command = scn.next();

            switch (command.toLowerCase()) {
                case "withdraw" -> {
                    int value = Integer.parseInt(scn.next().trim());
                    client.withdraw(value);
                }

                case "balance" -> System.out.println("Your current account balance is: " + UserSession.format(client.getBalance()));

                case "exit", "logout" -> {
                    System.out.println("Exiting...");
                    shouldClose = true; // will fall down to the end of the while loop and into client.exit();
                }

                case "deposit" -> {
                    long value = Integer.parseInt(scn.next().trim());
                    client.deposit(value);
                }

                case "history", "log", "extract" -> client.printLog();

                case "admin", "sysad", "enter" -> {
                    int key = Integer.parseInt(scn.next().trim());

                    if(client.checkAdmin()) {
                        client.admin(key);
                    }

                    AdminSession sysadmin = new AdminSession(client, key);

                    boolean exitAdmin = false;
                    while(!exitAdmin) {
                        System.out.print("SYS@" + client.getCurrentUser()[0] + "> ");
                        command = scn.next();

                        switch(command.toLowerCase()) {
                            case "exit", "dead", "out" -> {
                                sysadmin.exitMode();
                                exitAdmin = true;
                            }

                            case "create", "new", "make" -> {
                                String accName = scn.next().trim();
                                String accPass = scn.next().trim();
                                long accInitBalance = Integer.parseInt(scn.next().trim());

                                sysadmin.newAccount(accName, accPass, accInitBalance);
                            }

                            case "set", "swap", "change" -> {
                                command = scn.next();

                                switch (command.toLowerCase()) {
                                    case "balance" -> {
                                        String name = scn.next();
                                        int newVal = Integer.parseInt(scn.next().trim());

                                        try { sysadmin.setBalance(name, newVal); }
                                        catch (IOException e) { throw new RuntimeException(e); }
                                        System.out.println("Balance for account " + name + " successfully set to " + UserSession.format(newVal));
                                    }

                                    case "name" -> {
                                        String oldName = scn.next();
                                        String newName = scn.next();

                                        try { sysadmin.setName(oldName, newName); }
                                        catch (IOException e) { throw new RuntimeException(e); }
                                    }

                                    case "password" -> {
                                        String name = scn.next();
                                        String newPass = scn.next();

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
