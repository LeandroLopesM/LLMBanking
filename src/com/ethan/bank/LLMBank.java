package com.ethan.bank;

import java.io.*;
import java.text.NumberFormat;
import java.util.Random;
import java.util.Scanner;

/*
    TBD:
        - Admin mode
        -   - Adding accs
        -   - setting balance
        -   - etc.
        - hashing system for password
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

        boolean shouldClose = false;
        String command;
        while(!shouldClose) {
            System.out.print("LLM@" + client.getCurrentUser()[0] + "> ");
            command = scn.next();

            switch (command.toLowerCase()) {
                case "withdraw" -> {
                    int value = Integer.parseInt(scn.next().replace(" ", ""));
                    client.withdraw(value);
                }

                case "balance" -> System.out.println("Your current account balance is: " + UserSession.format(client.getBalance()));

                case "exit", "logout" -> {
                    System.out.println("Exiting...");
                    client.exit();
                }

                case "deposit" -> {
                    long value = Integer.parseInt(scn.next().trim());
                    client.deposit(value);
                }

                case "history", "log", "extract" -> {
                    client.printLog();
                }
            }
        }

        client.exit();
        scn.close();
    }
}
