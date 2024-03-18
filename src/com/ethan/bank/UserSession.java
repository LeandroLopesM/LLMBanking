package com.ethan.bank;

import com.ethan.bank.exceptions.BalanceNotFoundException;

import java.io.*;
import java.text.NumberFormat;
import java.util.Scanner;

public class UserSession {
    private final String username;
    private final String password;
    private long accBalance;
    File logFile = null;
    private int logIndex = 0;

    public UserSession(String uName, String uPass) {
        if(uName.isBlank() || uPass.isBlank()) {
            System.exit(1);System.err.println("UserSession.java::UserSession() ERROR: Invalid credentials passed to constructor: (Blank " + ((uName.isBlank())? "username" : "password") + ")");
            System.exit(1);
        }

        if(!validateID(uName, uPass)) {
            System.out.println("Login not found! (If creating an account please contact banking administrator)");
            System.exit(1);
        }

        username = uName;
        password = uPass;
        createLog();

        try {
            accBalance = getAccBalance();
        } catch(BalanceNotFoundException bnfe) {
            System.out.println("Balance not found! Please contact system administrator.");
            System.exit(1);
        }
    }

    private void createLog() {
        File rootFolder = new File(".\\");
        File[] files = rootFolder.listFiles();
        assert files != null;

        for(File i : files) {
            if(i.getName().endsWith(username + ".log")) {
                logIndex = Integer.parseInt(i.getName().substring(0, 2).replace("-", ""));
                break;
            }
        }
        String logLead = (logIndex < 10)? ("-" + Integer.toString(logIndex)) : Integer.toString(logIndex);
        logFile = new File(logLead + username + ".log");
    }

    public void exit() {
        System.exit(0); // i wanted to do something here but i forgor
    }

    public void printLog() {
        final String CYAN = "\033[0;36m";
        final String RESET = "\033[0m";

        try {
            BufferedReader reader = new BufferedReader(new FileReader(logFile));
            String line;

            while((line = reader.readLine()) != null) {
                System.out.println(CYAN + line + RESET);
            }


        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    public void log(String format) {
        try {
            BufferedWriter logger = new BufferedWriter(new FileWriter(logFile, true));
            
            logger.append(format);
            logger.newLine();

            logger.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public long getAccBalance() throws BalanceNotFoundException {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("balance.txt"));
            String fileLine = " ";

            while((fileLine = reader.readLine()) != null) {
                if(fileLine.startsWith(username)) {
                    return Integer.parseInt(fileLine.split("\\t+")[1]);
                }
            }

            reader.close();
        } catch(Exception e) {
            e.printStackTrace();
        }

        throw new BalanceNotFoundException();
    }

    private String warn(String format) {
        final String YELLOW = "\u001B[33m";
        final String RESET = "\u001B[0m";

        return YELLOW + format + RESET;
    }





    public void withdraw(long amount) {
        if(accBalance <= 0) {
            System.out.println(warn("This account is currently " +
                ((accBalance == 0)? "empty" : (format(accBalance) + " in debt")) +
                " and cannot be withdrawn from."));
            log("LOG: Attempted withdraw; blocked due to insufficient funds");
            return;
        }
        else if((accBalance - amount) < 0) {
            Scanner tmp = new Scanner(System.in);
            String answer = "n"; // Set default to no (prevents accidental debt)

            System.out.println(warn("WARNING: This action will put your account " + format(Math.abs(accBalance - amount)) + " into debt."));
            System.out.print(warn("Are you sure you'd like to proceed? [Y/N]: "));
            answer = tmp.next();

            if(answer.equalsIgnoreCase("y")) {
                accBalance -= amount;

                log("WITHDREW: " + format(amount) + " Current balance: " + format(accBalance));
                log("WARN: Account locked from outbound transactions due to debt");
            }
            else {
                System.out.println("Transaction canceled");
            }

            return;
        }
        accBalance -= amount;

        System.out.println("Successfully withdrew " + format(amount) + " New balance: " + format(accBalance));
        log("WITHDREW: " + format(amount) + "Current balance: " + format(accBalance));
    }

    public long getBalance() {
        return accBalance;
    }

    public void deposit(long amount) {
        accBalance += amount;
        log("DEPOSIT: " + format(amount) + " Balance: " + format(accBalance));
        System.out.println("Successfully deposited " + format(amount) + " into your account. New balance: " + format(accBalance));
    }

    public static String format(long amount) {
        NumberFormat format = NumberFormat.getCurrencyInstance();
        return format.format(amount);
    }

    public String[] getCurrentUser() {
        return new String[] {username, password};
    }

    public boolean validateID(String uName, String uPass) {
        try {
            BufferedReader databaseReader = new BufferedReader(new FileReader("users.txt"));
            String databaseLine;

            while((databaseLine = databaseReader.readLine()) != null) {
                if(databaseLine.split("\\t+")[0].equals(uName) && databaseLine.split("\\t+")[1].equals(uPass)) {
                    return true;
                }
            }

            databaseReader.close();
            return false;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
