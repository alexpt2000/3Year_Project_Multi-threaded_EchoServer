/**
* Server Application Rules
* 
* 1. The server application should not provide any service to a client 
* application that can complete the authentication.
*
* 2. The server should hold a list of valid users of the service and a 
* list of all the users previous transactions.
*
* 3. When the user logs in they should receive their current balance.
* 
*
* @author  Alexander Souza - G00317835
* @version 1.0
* @since   20/12/2016 
*/

package gmit;

import java.beans.beancontext.BeanContext;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.Map.Entry;

public class EchoServer {
	public static void main(String[] args) throws Exception {
		ServerSocket m_ServerSocket = new ServerSocket(2004, 10);
		int id = 0;
		
		
		
		while (true) {
			Socket clientSocket = m_ServerSocket.accept();
			ClientServiceThread cliThread = new ClientServiceThread(clientSocket, id++);
			cliThread.start();
		}
	}
}

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

	String clientName = "";
	String clientAddress = "";
	String clientACnumber = "";
	String clientUserName = "";
	String clientPassword = "";
	
	double balance = 0.00;
	double transaction = 0.00;

	ArrayList<ClientBean> clientsBank = new ArrayList<ClientBean>();
	ArrayList<BankBean> moviments = new ArrayList<BankBean>();
	
	Hashtable<String, ArrayList<BankBean>> clientsStatements = new Hashtable<String, ArrayList<BankBean>>();
	


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
	 * This is the method which makes use of add values into a Arraylist.
	 * 
	 * @param add Strings ref fields represents the register to the system.
	 * @return clientsBank.
	 */
	public synchronized ArrayList<ClientBean> addCLientList(String clientName, String clientAddress, String clientACnumber, String clientUserName, String clientPassword) {

		clientsBank.add(new ClientBean(clientName, clientAddress, clientACnumber, clientUserName, clientPassword));
	
		return clientsBank;
	}

	
	/**
	 * This is the method which makes use of load values from file where the data are storage and
	 * load into a Arraylist.
	 * 
	 * @param no param.
	 * @return clientsBank.
	 */
	public ArrayList<ClientBean> loadClientsFile() throws FileNotFoundException {

		System.out.println("Loading Array");
		
		Scanner scan = new Scanner(new File(filename));

		while (scan.hasNextLine()) {
			String line = scan.nextLine();
			String[] clientValue = line.split(",");
			
			System.out.println(clientValue[0]);
			
			clientsBank.add(new ClientBean(clientValue[0], clientValue[1], clientValue[2], clientValue[3], clientValue[4]));
		}

		scan.close();

		return clientsBank;
	}
	
	
	/**
	 * This is the method verify if the user exist into the system
	 * 
	 * @param verifyUser(String clientACnumber, String clientUserName, String clientPassword).
	 * @return boolean loginConfimation.
	 */
	
	public boolean verifyUser(String clientACnumber, String clientUserName, String clientPassword){
		
		loginConfimation = false;
		
			for (int i = 0; i < clientsBank.size(); i++ ){
				
				if (clientACnumber.equals(clientsBank.get(i).getClientACnumber()) && clientUserName.equals(clientsBank.get(i).getClientUserName()) && clientPassword.equals(clientsBank.get(i).getClientPassword())) {
					loginConfimation = true;
					System.out.println("OK ");
					
					i = clientsBank.size();
				}

			}
	
		return loginConfimation;
	}

	
	
	/**
	 * This is the method which makes use of load values from file where the data are storage and
	 * load into a Arraylist.
	 * 
	 * @param no param.
	 * @return clientsBank.
	 */
	public Hashtable<String, ArrayList<BankBean>> loadStatementsFile() throws FileNotFoundException {

		System.out.println("Loading Array");
		
		Scanner scan = new Scanner(new File(statements));

		while (scan.hasNextLine()) {
			String line = scan.nextLine();
			String[] clientValue = line.split(",");
			
			System.out.println(clientValue[0]);
			
			double trans = Double.parseDouble(clientValue[1]);
			double bal = Double.parseDouble(clientValue[2]);
			
			
			moviments.add(new BankBean(trans, bal));
			
			clientsStatements.put(clientValue[0], moviments);
			
			
		}

		scan.close();

		return clientsStatements;
	}
	

	public void run() {
		
		//BankBean account = new BankBean();
		
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
						// sendMessage("Are Ok");

						loginConfimation = false;

						clientsBank = loadClientsFile();

						do {

							sendMessage("Enter your A/C Number");
							clientACnumber = (String) in.readObject();
							
							sendMessage("Enter your Username");
							clientUserName = (String) in.readObject();

							sendMessage("Enter your Password");
							clientPassword = (String) in.readObject();
							
							loginConfimation = verifyUser(clientACnumber, clientUserName, clientPassword);

							loginConfimation = true;
							
						} while (loginConfimation == false);

						// sendMessage(clientsBank.toString());
						//sendMessage(clientsBank.get(0).getClientName());
						
						exit = false;
						
						// Load all values from statement file
						loadStatementsFile(); 
						
						do{
							sendMessage("\nOP 3 - Change customer details\nOP 4 - Make Lodgements\nOP 5 - Make Withdrawal\nOP 6 - View the last ten transactions\n\nOP 99 - Exit");
							message = (String) in.readObject();
							
							//Change customer details
							if (message.compareTo("3") == 0){
								
							}
	
							// Make Lodgements
							if (message.compareTo("4") == 0){
								
								FileWriter fw = new FileWriter(statements, true);
								BufferedWriter bw = new BufferedWriter(fw);
								PrintWriter appendFileStatements = new PrintWriter(bw);
								
								sendMessage("Enter amount Lodgement");
								String amount = (String) in.readObject();
								
								double value = Double.parseDouble(amount);
								
								balance = balance + value;
								transaction = value;
								
								//logment(amount);
								
								appendFileStatements.println(clientACnumber + "," + transaction + "," + balance);

								
								moviments.add(new BankBean(transaction, balance));
								
								clientsStatements.put(clientACnumber, moviments);
								
								

								appendFileStatements.close();
								bw.close();
								fw.close();

								
							}
							
							// Make Withdrawal
							if (message.compareTo("5") == 0){
								
								
								FileWriter fw = new FileWriter(statements, true);
								BufferedWriter bw = new BufferedWriter(fw);
								PrintWriter appendFileStatements = new PrintWriter(bw);
								
								double value;
								
								do{
									sendMessage("Enter amount Withdrawal (limit 1000.00)");
									String amount = (String) in.readObject();
	
									value = Double.parseDouble(amount);
								
								} while (value > 1000 && value > balance);
							
								balance = balance - value;
			
								value *= -1; // Convert to a negative number
								
								transaction = value;
								
								appendFileStatements.println(clientACnumber + "," + transaction + "," + balance);
								
								
								moviments.add(new BankBean(transaction, balance));
								
								clientsStatements.put(clientACnumber, moviments);
			
								appendFileStatements.close();
								bw.close();
								fw.close();
								
							}
							
							// View the last ten transactions
							if (message.compareTo("6") == 0){
								
								System.out.println(balance);
								System.out.println(transaction);
								
								//System.out.println(clientsStatements.get(clientACnumber).listIterator());
			
								
								
//							    Set set = clientsStatements.entrySet();
//							    Iterator it = set.iterator();
//							    while (it.hasNext()) {
//							      Map.Entry entry = (Map.Entry) it.next();
//							      System.out.println(entry.getKey() + " : " + entry.getValue());
//							    }
							    
							    for(Entry<String, ArrayList<BankBean>> entry : clientsStatements.entrySet()) {
							    	  String key = entry.getKey();
							    	  for(BankBean val : entry.getValue()) {
							    	    // do something with key and each val
							    		  System.out.println(val.getTransactions() + "  " + val.getBalance());
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
						addCLientList(clientName, clientAddress, clientACnumber, clientUserName, clientPassword);

						// Add the information into the file
						appendFileLogin.println(clientName + "," + clientAddress + "," + clientACnumber + "," + clientUserName + "," + clientPassword);
						
			

						appendFileLogin.close();
						bw.close();
						fw.close();

					}

					if (message.compareTo("99") == 0) {
						sendMessage("exit");
						finish = true;

					}

					// System.out.println("Got the message: "+ message);

					// System.out.println("client>"+clientID+" "+ message);
					// //if (message.equals("bye"))
					// sendMessage("server got the following: "+message);
					// message = (String)in.readObject();

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

	private void logment() {
		// TODO Auto-generated method stub
		
	}
}
