package com.ethan.bank;

import java.io.*;
import java.util.ArrayList;

public class AdminSession {
    UserSession regularClient;
    private final int KEY;
    public AdminSession(UserSession clientInfo, int key) {
        if(!clientInfo.checkAdmin()) {
            System.exit(0);
        }
        regularClient = clientInfo;
        KEY = key;
    }

    public void newAccount(String accName, String accPass, long initialBalance) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("users.txt", true));

            writer.append(accName + '\t' + accPass);
            writer.newLine();

            writer.close();

            writer = new BufferedWriter(new FileWriter("balance.txt", true));

            writer.append(accName + '\t' + initialBalance);
            writer.newLine();

        writer.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void setBalance(String accName, long newValue) {
        try {
            BufferedReader read = new BufferedReader(new FileReader("balance.txt"));
            String line;
            ArrayList<String> file = new ArrayList<>();


            while((line = read.readLine()) != null) {
                file.add(line);
            }

            BufferedWriter writer = new BufferedWriter(new FileWriter("balance.txt"));

            for(String sub : file) {
                if(sub.startsWith(accName)) {
                    sub = accName + '\t' + newValue;
                }
                writer.write(sub);
                writer.newLine();
            }

            writer.close();
            read.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void setName(String oldName, String newName) throws IOException {
        BufferedWriter write;
        ArrayList<String> file = new ArrayList<>();
        BufferedReader read;

        {
            String line;
            read = new BufferedReader(new FileReader("users.txt"));

            while((line = read.readLine()) != null) {
                if(line.startsWith(oldName)) {
                    line = newName + line.replace(oldName, "");
                }
                file.add(line);
            }

            write = new BufferedWriter(new FileWriter("users.txt"));

            for(String sub : file) {
                write.write(sub);
                write.newLine();
            }
        }

        read.close();
        write.close();

        {
            read = new BufferedReader(new FileReader("balance.txt"));
            String line;
            file.clear();

            while((line = read.readLine()) != null) {
                if(line.startsWith(oldName)) {
                    line = newName + line.replace(oldName, "");
                }
                file.add(line);
            }

            write = new BufferedWriter(new FileWriter("balance.txt"));

            for(String sub : file) {
                write.write(sub);
                write.newLine();
            }
        }

        write.close();
        read.close();
    }

    public void setPass(String name, String newPass) throws IOException {
        BufferedReader read = new BufferedReader(new FileReader("users.txt"));
        ArrayList<String> file = new ArrayList<>();
        String line;

        while((line = read.readLine()) != null) {
            if(line.startsWith(name)) {
                line = name + '\t' + newPass;
            }
            file.add(line);
        }

        BufferedWriter write = new BufferedWriter(new FileWriter("users.txt"));

        for(String sub : file) {
            write.write(sub);
            write.newLine();
        }

        write.close();
        read.close();
    }

    public void exitMode() {
        regularClient.admin(KEY);
    }
}
