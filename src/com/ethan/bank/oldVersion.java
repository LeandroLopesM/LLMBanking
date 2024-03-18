package com.ethan.bank;

import java.io.*;
import java.text.NumberFormat;
import java.util.Random;
import java.util.Scanner;

public class oldVersion {
    public static final String YELLOW = "\u001B[33m";
    public static final String RESET = "\u001B[0m";


    File log;
    String username;
    String userPass;
    public oldVersion() {
        Scanner scn = new Scanner(System.in);

        System.out.print("Username: ");
        username = scn.next();
        System.out.print("Password: ");
        userPass = scn.next();

        us();

        if (!validateCredentials()) {
            System.out.println("Invalid credentials");
            System.exit(1);
        }
        boolean shouldExit = false;
        String command;
        long accBalance = getBalance();

        if(accBalance == 0x0EFFA) {
            System.out.println("Account doesn't have balance in registry, please add to create or verify your account information");
        }

        while(!shouldExit) {
            System.out.print("LLM@" + username + " > ");
            command = scn.next();

            switch (command.toUpperCase()) {
                case "BALANCE" -> System.out.println("Total account balance: " + format(accBalance));

                case "LOGOUT", "EXIT" -> {
                    System.out.println("Exiting...");
                    shouldExit = true;
                }

                case "WITHDRAW" -> {
                    if(accBalance <= 0) {
                        System.out.println(YELLOW + "Your account is currently " + format(Math.abs(accBalance)) + " in debt, please rectify before withdrawing." + RESET);
                        accLog("LOG: Withdrawal attempt blocked due to negative or empty account balance");
                        continue;
                    }

                    long withdrawn = Integer.parseInt(scn.nextLine().replace(" ", ""));

                    if((accBalance - withdrawn) < 0) {
                        System.out.println(YELLOW + "WARNING: This action will put your account " + format(Math.abs(accBalance - withdrawn)) + " in debt.");
                        System.out.println("Are you sure you'd like to proceed? [Y/N]: " + RESET);
                        command = scn.next();

                        if(command.equalsIgnoreCase("Y")) {
                            accBalance -= withdrawn;
                            accLog("WITHDREW:  " + format(withdrawn) + "  Remaining:  " + format(accBalance));
                            accLog("WARNING: Account locked due to " + format(Math.abs(accBalance)) + " debt, please rectify before withdrawing");
                            System.out.println("Successfully withdrawn " + format(withdrawn) + " from balance, Remaining: " + format(accBalance));
                        }
                        continue;
                    }

                    accBalance -= withdrawn;

                    System.out.println("Successfully withdrawn " + format(withdrawn) + " from balance, Remaining: " + format(accBalance));
                    accLog("WITHDREW:  " + format(withdrawn) + "  Remaining:  " + format(accBalance));
                }

                case "DEPOSIT" -> {
                    long deposit = Integer.parseInt(scn.next().replace(" ", ""));

                    accBalance += deposit;

                    System.out.println("Successfully deposited " + format(deposit) + " into account. New balance: " + format(accBalance));
                    accLog("DEPOSITED: " + format(deposit) + " Balance: " + format(accBalance));
                }

                case "EXTRACT", "HISTORY" -> {
                    getHistory();
                }

                case "INTEREST" -> {
                    String values = scn.nextLine();
                    System.out.println("interesting" + values);
                }

                default -> {
                    System.out.println("Unknown command");
                }

            }
        }
        scn.close();
        while(!log.renameTo(new File("PAST--" + username + (new Random().nextInt(100)) + ".log")));
    }

    public void getHistory() {
        try {
            BufferedReader logReader = new BufferedReader(new FileReader(log));
            String line;

            while((line = logReader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String format(long rawValue) {
        NumberFormat formatted = NumberFormat.getCurrencyInstance();
        return formatted.format(rawValue);
    }

    public void accLog(String toLog) {
        BufferedWriter logger = null;

        try {
            log = new File("--" + username + ".log");
            if(!log.exists()) {
                if(!log.createNewFile()) {
                    System.err.println("accLog :: LLMBank.java ERROR: Something went wrong while creating a new log file");
                }
            }

            logger = new BufferedWriter(new FileWriter(log, true));
            logger.append(toLog);
            logger.newLine();

            logger.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (logger != null) {
                try {
                    logger.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public long getBalance() {
        try {
            BufferedReader file = new BufferedReader(new FileReader("balance.txt"));
            String fileLine;
            while((fileLine = file.readLine()) != null) {
                if(fileLine.startsWith(username)) {
                    return Integer.parseInt(fileLine.split("\\t+")[1]);
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return 0x0EFFA;
    }

    public void us() {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter("users.txt"));
            bw.write("admin\tsudo");
            bw.close();

            bw = new BufferedWriter(new FileWriter("balance.txt"));

            bw.write("admin\t200000");

            bw.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean validateCredentials() {
        String dbGet;
        String dbCompare = username + '\t' + userPass;
        try {
            BufferedReader reader = new BufferedReader(new FileReader("users.txt"));
            while((dbGet = reader.readLine()) != null) {
                if(dbGet.equals(dbCompare)) {
                    return true;
                }
            }

            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
