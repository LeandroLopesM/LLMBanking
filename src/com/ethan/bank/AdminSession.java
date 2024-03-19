package com.ethan.bank;

import java.io.BufferedWriter;
import java.io.FileWriter;

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

	public void exitMode() {
		regularClient.admin(KEY);

	}
}
