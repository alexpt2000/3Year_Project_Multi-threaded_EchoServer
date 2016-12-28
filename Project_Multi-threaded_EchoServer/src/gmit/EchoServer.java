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

import java.net.ServerSocket;
import java.net.Socket;

public class EchoServer {


	
	/**
	 * This is the method main, this will create new socket and a thread for
	 * this socket.
	 * 
	 */

	public static void main(String[] args) throws Exception {
		
		
		
		// Creating a new socket
		ServerSocket m_ServerSocket = new ServerSocket(2004, 10);

		// This will give one ID for each socket created
		int id = 0;

		// Stay in loop waiting for conections (client)
		while (true) {

			Socket clientSocket = m_ServerSocket.accept();

			// Creating a thread and give a socket and ID
			ClientServiceThread cliThread = new ClientServiceThread(clientSocket, id++);

			// Start the thread
			cliThread.start();
		}
	}
}
