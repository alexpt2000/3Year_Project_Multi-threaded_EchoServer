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

	public ArrayList<ClientBean> loadClientsFile() throws FileNotFoundException {

		Scanner scan = new Scanner(new File(filename));

		while (scan.hasNextLine()) {
			String line = scan.nextLine();
			String[] clientValue = line.split(",");
			clientsBank.add(new ClientBean(clientValue[0], clientValue[1], clientValue[2], clientValue[3], clientValue[4]));
		}

		scan.close();

		return clientsBank;
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

			boolean finish = false;

			do {
				try {

					sendMessage("\nOP 1 - Login\nOP 2 - Register\n\nOP 99 - Exit");

					message = (String) in.readObject();

					if (message.compareTo("1") == 0) {
						// sendMessage("Are Ok");

						boolean loginConfimation = false;

						clientsBank = loadClientsFile();

						do{

							sendMessage("Enter your Username");
							clientUserName = (String) in.readObject();

							sendMessage("Enter your Password");
							clientPassword = (String) in.readObject();

						}while(loginConfimation == false);



						//sendMessage(clientsBank.toString());
						sendMessage(clientsBank.get(0).getClientName());


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

						// sendMessage(clientName + " " + clientAddress + " " +
						// clientACnumber + " " + clientUserName + " " +
						// clientPassword);

						appendFileLogin.println(clientName + "," + clientAddress + "," + clientACnumber + ","
								+ clientUserName + "," + clientPassword);// appends
						// the
						// string
						// to
						// the
						// file

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
