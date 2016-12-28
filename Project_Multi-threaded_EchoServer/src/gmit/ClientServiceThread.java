/*
 * 
 * Topics to solve
 * 
 * Change Customer Details
 * Send to Amazon Server
 * Aceita valores acima do saldo disponivel
 * 
 * 
 * */

package gmit;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Map.Entry;

class ClientServiceThread extends Thread {
	Socket clientSocket;
	String message;
	int clientID = -1;
	boolean running = true;
	boolean exit;
	boolean loginConfimation;

	ObjectOutputStream out;
	ObjectInputStream in;

	// Variables to set filenames
	String filename = "loginData.dat";
	String statements = "Statements.dat";

	// Instance of Bean Classes
	ClientBean client = new ClientBean();
	BankBean account = new BankBean();
	ServicesBean services = new ServicesBean();

	// Local Variables
	String clientName = "";
	String clientAddress = "";
	String clientACnumber = "";
	String clientUserName = "";
	String clientPassword = "";
	double balance = 0.00;
	double transaction = 0.00;
	String lastTransactions = "";

	ClientServiceThread(Socket s, int i) {
		clientSocket = s;
		clientID = i;
	}

	// Method to Send MSG
	void sendMessage(String msg) {
		try {
			out.writeObject(msg);
			out.flush();
			System.out.println("client> " + msg);
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
	}

	/**
	 * This is the method which makes use of load values from file where the
	 * data are storage and load into a List.
	 * 
	 * @return void
	 */
	public void loadClientsFile() throws FileNotFoundException {
		Scanner scan = new Scanner(new File(filename));

		// Read each line into the file
		while (scan.hasNextLine()) {
			String line = scan.nextLine();
			String[] clientValue = line.split(",");

			services.addClients(clientValue[0], clientValue[1], clientValue[2], clientValue[3], clientValue[4]);
		}

		scan.close();
	}

	/**
	 * Change details into the file users
	 * 
	 * 
	 */

	public void changeClientsFile(String acNumber, String newAddress, String newUserName, String newPassword)
			throws FileNotFoundException {
		Scanner scan = new Scanner(new File(filename));

		System.out.println(acNumber + " " + newAddress + " " + newUserName + " " + newPassword);

		while (scan.hasNextLine()) {
			String line = scan.nextLine();
			String[] clientValue = line.split(",");

			if (clientValue[2].equals(acNumber)) {

				line.replaceAll(clientValue[1], newAddress);
				line.replaceAll(clientValue[3], newUserName);
				line.replaceAll(clientValue[4], newPassword);

				System.out.println(line);
			}
		}

		scan.close();
	}

	/**
	 * This is the method verify if the user exist into the system
	 * 
	 * @param verifyUser(String
	 *            clientACnumber, String clientUserName, String clientPassword).
	 * @return loginConfimation.
	 */

	public synchronized boolean verifyUser(String clientACnumber, String clientUserName, String clientPassword) {

		loginConfimation = false;

		for (int i = 0; i < services.getClientsBank().size(); i++) {

			if (clientACnumber.equals(services.getClientsBank().get(i).getClientACnumber())
					&& clientUserName.equals(services.getClientsBank().get(i).getClientUserName())
					&& clientPassword.equals(services.getClientsBank().get(i).getClientPassword())) {
				loginConfimation = true;
				i = services.getClientsBank().size();
			}
		}

		return loginConfimation;
	}
	
	
	
	
	/**
	 * This is the method verify if the user AC exist into the system
	 * 
	 * @param verifyUser(String
	 *            clientACnumber, String clientUserName, String clientPassword).
	 * @return loginConfimation.
	 */

	public synchronized boolean verifyUserAC(String clientACnumber) {

		loginConfimation = true;

		for (int i = 0; i < services.getClientsBank().size(); i++) {

			if (clientACnumber.equals(services.getClientsBank().get(i).getClientACnumber())) {
				loginConfimation = false;
				i = services.getClientsBank().size();
			}
		}

		return loginConfimation;
	}
	

	/**
	 * This is the method which makes use of load values from file where the
	 * data are storage and load into a list.
	 * 
	 * @param no
	 *            param.
	 * @return void.
	 */
	public void loadStatementsFile() throws FileNotFoundException {

		Scanner scan = new Scanner(new File(statements));

		while (scan.hasNextLine()) {
			String line = scan.nextLine();
			String[] clientValue = line.split(",");

			double trans = Double.parseDouble(clientValue[1]);
			double bal = Double.parseDouble(clientValue[2]);

			services.addStatements(clientValue[0], trans, bal);
		}
		scan.close();
	}

	public void run() {

		System.out.println(
				"Accepted Client : ID - " + clientID + " : Address - " + clientSocket.getInetAddress().getHostName());
		try {

			out = new ObjectOutputStream(clientSocket.getOutputStream());
			out.flush();
			in = new ObjectInputStream(clientSocket.getInputStream());
			System.out.println("Accepted Client : ID - " + clientID + " : Address - "
					+ clientSocket.getInetAddress().getHostName());

			sendMessage("Connection successful\n----------------------\n \nWelcome Bank of GMIT");

			loadClientsFile(); // Load user into a list

			boolean finish = false;

			do {
				try {

					sendMessage("\nOP 1 - Login\nOP 2 - Register\n\nOP 99 - Exit");

					message = (String) in.readObject();

					/*
					 * Login menu
					 * 
					 */
					if (message.compareTo("1") == 0) {

						loginConfimation = false;

						do {

							sendMessage("Enter your A/C Number");
							clientACnumber = (String) in.readObject();

							sendMessage("Enter your Username");
							clientUserName = (String) in.readObject();

							sendMessage("Enter your Password");
							clientPassword = (String) in.readObject();

							loginConfimation = verifyUser(clientACnumber, clientUserName, clientPassword);

							// *********** Disable Login just for Testing
							// ******************************************************************
							// loginConfimation = true;

						} while (loginConfimation == false);

						exit = false;

						// After the login
						// Load all values from statement file
						loadStatementsFile();

						do {

							// check the balance to show in menu
							double bal = 0.00;

							for (Entry<String, ArrayList<BankBean>> entry : services.getClientsStatements()
									.entrySet()) {
								String key = entry.getKey();
								if (key.equals(clientACnumber)) {
									for (BankBean val : entry.getValue()) {
										bal = val.getBalance();
									}
								}
							}

							// Print the menu OP
							// In message is include the balance
							sendMessage("\n----------------------------------\n--> Bank AC Number.: " + clientACnumber + " - " + clientUserName
									+ "\n--> Current balance.: " + bal
									+ "\n\nOP 3 - Change customer details\nOP 4 - Make Lodgements\nOP 5 - Make Withdrawal\nOP 6 - View the last ten transactions\n\nOP 99 - Exit\n\n"
									+ lastTransactions);

							message = (String) in.readObject();

							lastTransactions = "";

							// Change customer details
							if (message.compareTo("3") == 0) {

								sendMessage("Enter your Address");
								clientAddress = (String) in.readObject();

								sendMessage("Enter your Username");
								clientUserName = (String) in.readObject();

								sendMessage("Enter your Password");
								clientPassword = (String) in.readObject();

								changeClientsFile(clientACnumber, clientAddress, clientUserName, clientPassword);

							}

							// Make Lodgements
							if (message.compareTo("4") == 0) {

								FileWriter fw = new FileWriter(statements, true);
								BufferedWriter bw = new BufferedWriter(fw);
								PrintWriter appendFileStatements = new PrintWriter(bw);

								sendMessage("Enter amount Lodgement");
								String amount = (String) in.readObject();

								double value = Double.parseDouble(amount);

								balance = balance + value;
								transaction = value;

								appendFileStatements.println(clientACnumber + "," + transaction + "," + balance);

								services.addStatements(clientACnumber, transaction, balance);

								appendFileStatements.close();
								bw.close();
								fw.close();
							}

							// Make Withdrawal
							if (message.compareTo("5") == 0) {

								FileWriter fw = new FileWriter(statements, true);
								BufferedWriter bw = new BufferedWriter(fw);
								PrintWriter appendFileStatements = new PrintWriter(bw);

								double value;

								do {
									sendMessage("Enter amount Withdrawal (limit 1000.00)");
									String amount = (String) in.readObject();

									value = Double.parseDouble(amount);

								} while (value > 1000 && value > balance);

								balance = balance - value;
								value *= -1; // Convert to a negative number
								transaction = value;
								appendFileStatements.println(clientACnumber + "," + transaction + "," + balance);
								services.addStatements(clientACnumber, transaction, balance);

								appendFileStatements.close();
								bw.close();
								fw.close();

							}

							// View the last ten transactions
							if (message.compareTo("6") == 0) {

								int test = services.getClientsStatements().size();
								int test2 = services.getClientsStatements().values().size();

								System.out.println(test + " Size " + test2);

								int count = 1;
								lastTransactions = "Transactions    Balance\n-------------------------\n";

								for (Entry<String, ArrayList<BankBean>> entry : services.getClientsStatements()
										.entrySet()) {
									String key = entry.getKey();

									System.out.println(key);

									if (key.equals(clientACnumber)) {

										System.out.println("OK " + key);

										int sizeValuesStatement = entry.getValue().size() - 10;

										for (BankBean val : entry.getValue()) {

											if (count > sizeValuesStatement) {
												System.out.println(sizeValuesStatement + " " + key + "  "
														+ val.getTransactions() + "  " + val.getBalance());

												lastTransactions += "\n" + val.getTransactions() + "     --->     "
														+ val.getBalance();

											}

											count++;
										}
									}

								}
							}
							
							
							
							if (message.compareTo("99") == 0) {
								sendMessage("Exit - Finish connection");
								finish = true;
								
								
							}
							

						} while (finish == false);

					}

					/*
					 * 
					 * Create a new User
					 * 
					 */
					if (message.compareTo("2") == 0) {

						FileWriter fw = new FileWriter(filename, true);
						BufferedWriter bw = new BufferedWriter(fw);
						PrintWriter appendFileLogin = new PrintWriter(bw);

						loginConfimation = false;

						do {
							sendMessage("Enter your Name");
							clientName = (String) in.readObject();

							sendMessage("Enter your Address");
							clientAddress = (String) in.readObject();

							sendMessage("Enter A/C Number");
							clientACnumber = (String) in.readObject();

							sendMessage("Enter your Username");
							clientUserName = (String) in.readObject();

							sendMessage("Enter your Password");
							clientPassword = (String) in.readObject();

							loginConfimation = verifyUserAC(clientACnumber);

						} while (loginConfimation == false);

						// Send values to the method
						// to add arrayList
						services.addClients(clientName, clientAddress, clientACnumber, clientUserName, clientPassword);

						// Add the information into the file
						appendFileLogin.println(clientName + "," + clientAddress + "," + clientACnumber + ","
								+ clientUserName + "," + clientPassword);

						appendFileLogin.close();
						bw.close();
						fw.close();

					}

					if (message.compareTo("99") == 0) {
						sendMessage("Exit - Finish connection");
						finish = true;

					}

				} catch (ClassNotFoundException classnot) {
					System.err.println("Data received in unknown format");
				}

			} while (finish == false);

			System.out.println(
					"Ending Client : ID - " + clientID + " : Address - " + clientSocket.getInetAddress().getHostName());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
