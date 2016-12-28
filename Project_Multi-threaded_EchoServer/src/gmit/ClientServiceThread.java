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
import java.util.Hashtable;
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

	String filename = "loginData.dat";
	String statements = "Statements.dat";

	// User Dada for register
	ClientBean client = new ClientBean();
	BankBean account = new BankBean();
	ServicesBean services = new ServicesBean();

	String clientName = "";
	String clientAddress = "";
	String clientACnumber = "";
	String clientUserName = "";
	String clientPassword = "";

	double balance = 0.00;
	double transaction = 0.00;

	String lastTransactions = "";

	
	
//	ArrayList<ClientBean> clientsBank = new ArrayList<ClientBean>();
//	ArrayList<BankBean> moviments = new ArrayList<BankBean>();
//	Hashtable<String, ArrayList<BankBean>> clientsStatements = new Hashtable<String, ArrayList<BankBean>>();

	ClientServiceThread(Socket s, int i) {
		clientSocket = s;
		clientID = i;
	}

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
	 * data are storage and load into a Arraylist.
	 * 
	 * @param no
	 *            param.
	 * @return clientsBank.
	 */
	public void loadClientsFile() throws FileNotFoundException {

		Scanner scan = new Scanner(new File(filename));

		while (scan.hasNextLine()) {
			String line = scan.nextLine();
			String[] clientValue = line.split(",");

			services.addClients(clientValue[0], clientValue[1], clientValue[2], clientValue[3], clientValue[4]);
			//services.clientsBank.add(new ClientBean(clientValue[0], clientValue[1], clientValue[2], clientValue[3], clientValue[4]));
		}

		scan.close();

		//return services.clientsBank;
	}

	/**
	 * This is the method verify if the user exist into the system
	 * 
	 * @param verifyUser(String
	 *            clientACnumber, String clientUserName, String clientPassword).
	 * @return boolean loginConfimation.
	 */

	public synchronized boolean verifyUser(String clientACnumber, String clientUserName, String clientPassword) {

		loginConfimation = false;

		for (int i = 0; i < services.getClientsBank().size(); i++) {

			if (clientACnumber.equals(services.getClientsBank().get(i).getClientACnumber())
					&& clientUserName.equals(services.getClientsBank().get(i).getClientUserName())
					&& clientPassword.equals(services.getClientsBank().get(i).getClientPassword())) {
				loginConfimation = true;
				System.out.println("OK ");

				i = services.getClientsBank().size();
			}

		}

		return loginConfimation;
	}

	/**
	 * This is the method which makes use of load values from file where the
	 * data are storage and load into a Arraylist.
	 * 
	 * @param no
	 *            param.
	 * @return clientsBank.
	 */
	public void loadStatementsFile() throws FileNotFoundException {

		Scanner scan = new Scanner(new File(statements));

		while (scan.hasNextLine()) {
			String line = scan.nextLine();
			String[] clientValue = line.split(",");

			//System.out.println(clientValue[0]);

			double trans = Double.parseDouble(clientValue[1]);
			double bal = Double.parseDouble(clientValue[2]);

			services.addStatements(clientValue[0], trans, bal);
			
			
			//services.moviments.add(new BankBean(trans, bal));

			//services.clientsStatements.put(clientValue[0], services.moviments);

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

			loadClientsFile();

			boolean finish = false;

			do {
				try {

					sendMessage("\nOP 1 - Login\nOP 2 - Register\n\nOP 99 - Exit");

					message = (String) in.readObject();

					if (message.compareTo("1") == 0) {

						loginConfimation = false;

						//loadClientsFile();

						do {

							sendMessage("Enter your A/C Number");
							clientACnumber = (String) in.readObject();

							sendMessage("Enter your Username");
							clientUserName = (String) in.readObject();

							sendMessage("Enter your Password");
							clientPassword = (String) in.readObject();

							loginConfimation = verifyUser(clientACnumber, clientUserName, clientPassword);

							// *********** Disable Login just for Testing
					
							 loginConfimation = true;

						} while (loginConfimation == false);

						exit = false;

						// Load all values from statement file
						loadStatementsFile();

						do {

							double bal = 0.00;

							for (Entry<String, ArrayList<BankBean>> entry : services.getClientsStatements().entrySet()) {
								String key = entry.getKey();

								if (key.equals(clientACnumber)) {

									for (BankBean val : entry.getValue()) {
										bal = val.getBalance();
									}
								}

							}

							sendMessage("\n----------------------------------\n--> Bank AC Number.: " + clientACnumber
									+ "\n--> Current balance.: " + bal
									+ "\n\nOP 3 - Change customer details\nOP 4 - Make Lodgements\nOP 5 - Make Withdrawal\nOP 6 - View the last ten transactions\n\nOP 99 - Exit\n\n"
									+ lastTransactions);
							message = (String) in.readObject();

							lastTransactions = "";

							// Change customer details
							if (message.compareTo("3") == 0) {

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
								
								
//								services.moviments.add(new BankBean(transaction, balance));
//								services.clientsStatements.put(clientACnumber, services.moviments);

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
								
//								services.moviments.add(new BankBean(transaction, balance));
//								services.clientsStatements.put(clientACnumber, services.moviments);

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

								for (Entry<String, ArrayList<BankBean>> entry : services.getClientsStatements().entrySet()) {
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

						} while (exit == false);

					}

					if (message.compareTo("2") == 0) {

						FileWriter fw = new FileWriter(filename, true);
						BufferedWriter bw = new BufferedWriter(fw);
						PrintWriter appendFileLogin = new PrintWriter(bw);

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
						sendMessage("exit");
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
