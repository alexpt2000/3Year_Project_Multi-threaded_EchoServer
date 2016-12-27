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

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

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
	boolean loginConfimation;
	
	ObjectOutputStream out;
	ObjectInputStream in;

	String filename = "loginData.dat";

	
	// User Dada for register
	ClientBean client;

	String clientName = "";
	String clientAddress = "";
	String clientACnumber = "";
	String clientUserName = "";
	String clientPassword = "";

	ArrayList<ClientBean> clientsBank = new ArrayList<ClientBean>();
	


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
	
	
	public boolean verifyUser(String clientACnumber, String clientUserName, String clientPassword){
		
		System.out.println("Contain.: " + clientACnumber);
		
		if(clientsBank.contains(clientACnumber)){
			
			System.out.println("IF Contain.: " + clientACnumber);
			
			for (int i = 0; i < clientsBank.size(); i++ ){
				
				if (this.clientUserName.equals(clientUserName) && this.clientPassword.equals(clientPassword)) {
					loginConfimation = true;
				}
				else {
					loginConfimation = false;
				}
				
			}
		}
		else {
			loginConfimation = false;
		}
		
		return loginConfimation;
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

							//loginConfimation = true;
							
						} while (loginConfimation == false);

						// sendMessage(clientsBank.toString());
						//sendMessage(clientsBank.get(0).getClientName());
						sendMessage("User Ok");

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
}
