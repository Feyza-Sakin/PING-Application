
/**************************************************************************************
 *    PINGServer.java
 *
 * Server File
 ***************************************************************************************
 * Function:
 * 		- Receives packets from the client as a server
 *  		- Prints the header and pay load of the received packets and the response packets
 *----------------------------------------------------------------------------------------------------------------------------------------
 *    Input:
 *          Parameters - 	argv[0] || The port number
 *          				argv[1] || The percentage loss
 *    Output:
 *          Return – The header and pay load of the received packet and the header and pay load of the response received packet
 *----------------------------------------------------------------------------------------------------------------------------------------
 *    Author Feyza Sakin
 *    Version 05/02/2023  CMSC 440
 **************************************************************************************/

import java.io.*;
import java.net.*;

/**************************************************************************************
 * PINGServer Class
 ************************************************************************************
 * Function:
 * - Receives packets from the client as a server
 *  		- Prints the header and pay load of the received packets and the response packets
 * 
 * --------------------------------------------------------------------------------------
 * 
 * @author Feyza Sakin
 * @version 05/02/2023 CMCS 440
 *************************************************************************************/

class PINGServer {

	public static void main(String argv[]) throws Exception {
		// socket variables
		DatagramSocket serverSocket;
		byte[] receiveData = new byte[1024];
		byte[] sendData = new byte[1024];
		InetAddress IPAddress;// The client's IP Address
		InetAddress serverIPAddress;// The server's IP address

		// server variables
		String serverSentence;// Stores the received packet's content in upper case
		int clientPort;// The port number of the client
		int randomLoss;// Randomly picked percentage loss for each packet
		int minLoss = 1;// The possibly min percentage loss
		int maxLoss = 100;// The possibly max percentage loss
		String IPString1[];// Store the IP address and split / from the IP address
		String[] splitReceived;// Stores the received packet content
		String[] splitResponse;// Stores the response packet content
		String IPString;// Store the IP address in a string to print it

		// command-line arguments
		int port;// The input port number
		int lossP;// The input percentage loss

		// If any arguments are missing, then print out the missing arguments
		if (argv.length < 2) {
			for (int i = argv.length + 1; i <= 2; i++) {
				System.out.println("ERR - arg " + i);
			}
			System.exit(-1);
		}

		port = Integer.parseInt(argv[0]);// Set port number to the first argv
		lossP = Integer.parseInt(argv[1]);// Set percentage loss to the second argv

		// If the percent input is less than 0 or bigger than 100, then give an error
		if (lossP < 0 || lossP > 100) {
			System.out.println("ERR - arg 1");
			System.exit(-1);
		}

		// Get the server's IP address and store in a string
		String stringServerIP = InetAddress.getLocalHost().getHostAddress();
		;
		serverIPAddress = InetAddress.getByName(stringServerIP);

		// If we can create a socket with the given port number
		try {
			// Create a new socket to the destination port
			serverSocket = new DatagramSocket(port);

			// Print out the server IP address and the port number at the start
			System.out.println("\r\nPINGServer started with server IP: " + stringServerIP + ", port: " + port + " ...");

			// try {
			// While loop to handle arbitrary sequence of clients making requests
			while (true) {

				// Waits for some client to send a packet and receives the packet
				DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
				serverSocket.receive(receivePacket);

				// Set the "clientSentence" to the received packet
				String clientSentence = new String(receivePacket.getData(), 0, receivePacket.getLength());

				// Convert received packet to all caps and store in "serverSentence" string
				serverSentence = clientSentence.toUpperCase();

				// Get the IP address of the packet and set the IP address without the "/" in a
				// string array
				IPAddress = receivePacket.getAddress();
				IPString1 = IPAddress.toString().split("/");
				IPString = IPString1[1];

				// Get the port number of the received packet
				clientPort = receivePacket.getPort();

				// Pick a random number for the packet's percentage loss between 1 - 100
				randomLoss = (minLoss + (int) (Math.random() * ((maxLoss - minLoss) + 1)));

				// If the randomly picked random percentage is less than or equal to the input
				// percentage, then drop the packet
				if (randomLoss <= lossP) {

					// Store the received packet header and pay load data by splitting with commas
					splitReceived = clientSentence.split(",");//

					// Print out the dropped packet's IP, port number, the client ID, and the
					// sequence number
					System.out.println("\nIP:" + IPString + " :: Port:" + clientPort + " : ClientID:" + splitReceived[1]
							+ " :: Seq#:" + splitReceived[2] + " : DROPPED");

					// Print out the received request packet's header and pay load
					System.out.println("----------Received Ping Request Packet Header----------");
					System.out.println("Version: " + splitReceived[0]);// Print out the version of the system
					System.out.println("Client ID: " + splitReceived[1]);// Print out the client ID
					System.out.println("Sequence No.: " + splitReceived[2]);// Print out the sequence number of the
																			// received packet
					System.out.println("Time: " + splitReceived[3]);// Print out the current time the packet was
																	// constructed
					System.out.println("Payload Size: " + splitReceived[4]);
					;// Print out the randomly picked pay load size
					System.out.println("--------- Ping Request Packet Payload ------------");
					System.out.println("Host: " + splitReceived[5]);// Not sure if the input is host name
					System.out.println("Class-name: " + splitReceived[6]);// Print out the class name
					System.out.println("User-name: " + splitReceived[8] + ", " + splitReceived[7]);// Print out the
																									// user's name and
																									// surname
					System.out.println("Rest: " + splitReceived[9]);// Print out the unused bytes left from the random
																	// pay load size
					System.out.println("---------------------------------------");
				}

				// If the randomly picked random percentage is bigger than the input percentage,
				// then send a response the client
				if (randomLoss > lossP) {

					// Store the received packet header and pay load data by splitting with commas
					splitReceived = clientSentence.split(",");//

					// Store the response packet header and pay load data by splitting with commas
					splitResponse = serverSentence.split(",");//

					// Print out the received packet's IP, port number, the client ID, and the
					// sequence number
					System.out.println("\nIP:" + IPString + " :: Port:" + clientPort + " : ClientID:" + splitReceived[1]
							+ " :: Seq#:" + splitReceived[2] + " :: RECEIVED");

					// Place response packet in the buffer
					sendData = serverSentence.getBytes();

					// Create packet and send it to the client
					DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, clientPort);
					serverSocket.send(sendPacket);

					// Print out the received request packet's header and pay load
					System.out.println("----------Received Ping Request Packet Header----------");
					System.out.println("Version: " + splitReceived[0]);// Print out the version of the system
					System.out.println("Client ID: " + splitReceived[1]);// Print out the client ID
					System.out.println("Sequence No.: " + splitReceived[2]);// Print out the sequence number of the
																			// received packet
					System.out.println("Time: " + splitReceived[3]);// Print out the current time the packet was
																	// constructed
					System.out.println("Payload Size: " + splitReceived[4]);
					;// Print out the randomly picked pay load size
					System.out.println("---------Received Ping Request Packet Payload------------");
					System.out.println("Host: " + splitReceived[5]);// Not sure if the input is host name
					System.out.println("Class-name: " + splitReceived[6]);// Print out the class name
					System.out.println("User-name: " + splitReceived[8] + ", " + splitReceived[7]);// Print out the
																									// user's name and
																									// surname
					System.out.println("Rest: " + splitReceived[9]);// Print out the unused bytes left from the random
																	// pay load size
					System.out.println("---------------------------------------\n");

					// Print out the packet's response header and pay load
					System.out.println("----------- Ping Response Packet Header ----------");
					System.out.println("Version: " + splitResponse[0]);// Print out the version of the system
					System.out.println("Client ID: " + splitResponse[1]);// Print out the client ID
					System.out.println("Sequence No.: " + splitResponse[2]);// Print out the sequence number of the
																			// received packet
					System.out.println("Time: " + splitResponse[3]);// Print out the current time the packet was
																	// constructed
					System.out.println("Payload Size: " + splitResponse[4]);// Print out the randomly picked pay load
																			// size
					System.out.println("---------- Ping Response Packet Payload -------------");
					System.out.println("Host: " + splitResponse[5]);// Not sure if the input is host name
					System.out.println("Class-name: " + splitResponse[6]);// Print out the class name
					System.out.println("User-name: " + splitResponse[8] + ", " + splitResponse[7]);// Print out the
																									// user's name and
																									// surname
					System.out.println("Rest: " + splitResponse[9]);// Print out the unused bytes left from the random
																	// pay load size
					System.out.println("---------------------------------------\r\n");
				} // end if statement
			} // end of while loop
		} catch (IOException socketException) {
			System.out.println("ERR - cannot create PINGServer socket using port number " + port);
			System.exit(-1);
		}
	}// end main
}// end class
