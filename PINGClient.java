/**************************************************************************************
  *    PINGClient.java
  *
  * Client File
  ***************************************************************************************
  * Function:
  * 		- Sents packets to the server as a client
  * 		- Prints the header and pay load of the sent packet and response recieved packet.  
  * 		- At the end, summarizes the number of sent packets, the number of response packets, minimum RTT, maximum RTT, average RTT, and the lost packets' percentage, and the average pay load size
  *----------------------------------------------------------------------------------------------------------------------------------------
  *    Input:
  *          Parameters - 	argv[0] || The IP or the host name of the server
  *          				argv[1] || The port number
  *          				argv[2] || The Client ID
  *          				argv[3] || The Number of Packets
  *          				argv[4] || The seconds the client can wait for the response 
  *    Output:
  *          Return – The header and pay load of the sent packet and the header and pay load of the response received packet
  *----------------------------------------------------------------------------------------------------------------------------------------
  *    Author Feyza Sakin
  *    Version 05/02/2023   CMCS 440
 **************************************************************************************/ 

import java.io.*;
import java.net.*;
import java.util.Date;
import java.util.TimeZone;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

/**************************************************************************************
 *    PINGClient Class
 ************************************************************************************
 * Function:
 *    - Sents packets to the server as a client
  * 		- Prints the header and pay load of the sent packet and response recieved packet.  
  * 		- At the end, summarizes the number of sent packets, the number of response packets, minimum RTT, maximum RTT, average RTT, and the lost packets' percentage, and the average pay load size
 *    
 *--------------------------------------------------------------------------------------  
 * 
 *    @author Feyza Sakin
 *    @version 05/02/2023   CMCS 440 *************************************************************************************/


class PINGClient {

	public static void main(String argv[]) throws Exception {
		// socket variables
		DatagramSocket clientSocket;
		DatagramPacket sendPacket;
		DatagramPacket receivePacket;
		byte[] receiveData = new byte[1024];
		byte[] sendData = new byte[1024];
		InetAddress serverIPAddress;//The server's IP address
		InetAddress clientIPAddress;//The client's IP address
		String clientHostName;//The client's host name
		
		// command-line arguments
		int port;//The input port number
		int clientID;//The input Client ID
		int nPackets;//The input number of packets
		int waitSec = 0;//The input amount of seconds to wait
		String server;//The input server
		
		// client variables
		String serverSentence;//Stores the received response
		int initialRestBytes;// The used bytes
		int restBytes;// The subtraction of the used bytes from the random pay load size "randomPay"
		int min = 150;// The minimum number of pay load size
		int max = 300;// The maximum number of pay load size
		int randomPay;// The random number for pay load size
		int nPingResponse = 0;// The number of ping responses
		int total_Pay_Load_size = 0;// The total pay load size
		int ave_Pay_Load_Size = 0;// The average pay load size
		int calcLoss;// The calculated loss
		float totalRTT = 0;// The total RTT
		float aveRTT = 0;// The average RTT
		double pRTT = 0;//Each time the packet is created, it stores its RTT
		double minRTT = Integer.MAX_VALUE;// Set the min RTT to max to compare with with min RTT numbers
		double maxRTT = Integer.MIN_VALUE;// Set the max RTT to min to compare with with max RTT numbers
		double iTimeStamp = 0;//Stores the current time the packet was constructed
		double rTimeStamp = 0;//Stores the current time the response was received 

		String className = "VCU-CMSC440-SPRING-2023";// The class name
		String userName = "Feyza";// The student name
		String userSurname = "Sakin";// The student surname
		String initialRest;//The string of packet pay load without the rest 
		String randomLetNum = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890";// Random letters & numbers
		String rest = "";//The rest string made of random letters and numbers
		String message;// Combination of header and pay load
		String timeStamp;//String type of the time stamp, converted from double
		String[] combinedArray;// The combination of the header and the pay load parts
		String[] splitRandomLetNum;//Stores the "randomLetNum" string's elements splited each comma
		String[] splitReceived;//Stores the received response content
		//Splitting "randomLetNum" string's elements each comma and store in splitRandomLetNum
		splitRandomLetNum = randomLetNum.split("(?!^)");//

		// Makes the value to have three decimal places
		DecimalFormat decimal = new DecimalFormat("#.###");
		
		// For missing any process command-line arguments, print
		if (argv.length < 5) {
			for (int i = argv.length + 1; i <= 5; i++) {
				System.out.print("ERR - arg " + i + "\r\n");
			}
			System.exit(-1);
		}

		//Get the client IP and host name
		clientIPAddress = InetAddress.getLocalHost();
		clientHostName = clientIPAddress.getHostName();
			    
		// Set the arguments to variables
		server = argv[0];//Set the first argv to the IP or the hostname "server"
		port = Integer.parseInt(argv[1]);//Set the second argv to the port number
		clientID = Integer.parseInt(argv[2]);//Set the third argv to the client ID
		nPackets = Integer.parseInt(argv[3]);//Set the fourth argv to the number of packets
		waitSec = Integer.parseInt(argv[4]);//Set the fifth argv to the seconds the client can wait for the response 
		
		// If the port number is not a positive integer or if it's bigger than 65536, then give error and return the error given arg
		if (port < 0 || port >= 65536) {
			System.out.print("ERR - arg 1\r\n");
			System.exit(-1);
			;
		}

		// To calculate the rest bytes, have a "initialRest" string 
		//Set "initialRest"'s bytes' length to the "initialRestBytes"
		initialRest = "Host: " + clientHostName + "\nClass-name: " + className + "\nUser-name: " + userSurname + ", " + userName
				+ "\nRest: ";
		initialRestBytes = initialRest.getBytes().length;

		// Create client socket to destination
		clientSocket = new DatagramSocket();

		// If the IP Address can be found from the hostName, then send and recieve packets and print out the packets headers and pay loads
		try {
			// Convert hostName to the IP Address
			String serverIP = InetAddress.getByName(server).getHostAddress();
			serverIPAddress = InetAddress.getByName(serverIP);

			// Print out the arguments: IP address, port number, client ID, number of packets to send, and the seconds to wait
			System.out.println("\r\nPINGClient started with server IP: " + serverIP + ", port: " + port + ", client ID: "
					+ clientID + ", packets: " + nPackets + ", wait: " + waitSec + "\n");

			// Create packet and send to server for the number of packets to send
			for (int i = 1; i <= nPackets; i++) {
			
				//Have a random number for the pay load size between 150 - 300
				randomPay = (min + (int) (Math.random() * ((max - min) + 1)));
				//Subtract the initial size from the random pay load size
				restBytes = randomPay - initialRestBytes;
				// Add the pay size to the total pay load size
				total_Pay_Load_size += randomPay;

				//Create a string of random letters & numbers as much as the unused bytes left from the random pay load size
				for (int j = 0; j < restBytes; j++) {
					int index = ((int) (Math.random() * ((splitRandomLetNum.length - 1) + 1)));
					//Add the randomly picked letter or number to the "rest" string
					rest = rest + splitRandomLetNum[index];
				}
				
				//Set the "iTimeStamp" to the current time the packet was constructed
				iTimeStamp = System.currentTimeMillis();
				//Convert iTimeStamp to string
				timeStamp = Double.toString(iTimeStamp);
				
				//Have a string array to store the header information
				String[] header = { "1", Integer.toString(clientID), Integer.toString(i), timeStamp,
						Integer.toString(randomPay) };
				
				//Have a string array to store the pay load information
				String[] payload = { clientHostName, className, userName, userSurname, rest };
				
				//Combine header and pay load string array in "combinedArray" string array
				combinedArray = new String[header.length + payload.length];
				System.arraycopy(header, 0, combinedArray, 0, header.length);
				System.arraycopy(payload, 0, combinedArray, header.length, payload.length);
	
				//Copy "combinedArray" string array to the "message" string and have "," between the elements 
				message = String.join(",", combinedArray);

				// Place packet's header and the pay load combined in the buffer
				sendData = message.getBytes();

				// Create packet and send to server
				sendPacket = new DatagramPacket(sendData, sendData.length, serverIPAddress, port);
				clientSocket.send(sendPacket);
				
				
				//Print out the request packet header and pay load for the packet
				System.out.println("---------- Ping Request Packet Header ----------");
				System.out.println("Version: 1");//Print out the version of the system
				System.out.println("Client ID: " + clientID);//Print out the client ID
				System.out.println("Sequence No.: " + i);//Print out the sequence number of the sent packet
				System.out.println("Time: " + timeStamp);//Print out the current time the packet was constructed
				System.out.println("Payload Size: " + randomPay);//Print out the randomly picked pay load size
				System.out.println("--------- Ping Request Packet Payload ------------");
				System.out.println("Host: " + clientHostName);//Print out the host name of the client
				System.out.println("Class-name: " + className);//Print out the class name
				System.out.println("User-name: " + userSurname + ", " + userName);//Print out the user's name and surname
				System.out.println("Rest: " + rest);//Print out the unused bytes left from the random pay load size
				System.out.println("---------------------------------------\n");
				
				// To reset random letters and numbers for each packets
				rest = "";

				//Wait for the given second if it's bigger than 0
				
/*
				if(waitSec > 0) {
					clientSocket.setSoTimeout((waitSec) * 1000);
				}
				if(waitSec == 0) {
					clientSocket.setSoTimeout((waitSec+1) * 1000);
				}
	*/			
				//If the response is received within the wait seconds 
				try {
					if(waitSec == 0) {
						waitSec = 1;
					}
					clientSocket.setSoTimeout((waitSec) * 1000);
					// Create receiving packet and receive from server
					receivePacket = new DatagramPacket(receiveData, receiveData.length);
					clientSocket.receive(receivePacket);
					
					// Set it to the time the packet was received
					rTimeStamp = System.currentTimeMillis();
					
					//Increase the number of received responses
					nPingResponse += 1;
					
					//Set the "serverSentence" to the received response
					serverSentence = new String(receivePacket.getData(), 0, receivePacket.getLength());
					//Store the received response in a string array and split in each comma
					splitReceived = serverSentence.split(",");
					
					//Print out the received packet's header and pay load
					System.out.println("---------- Received Ping Response Packet Header ----------");
					System.out.println("Version: " + splitReceived[0]);//Print out the version of the system
					System.out.println("Client ID: " + splitReceived[1]);//Print out the client ID
					System.out.println("Sequence No.: " + splitReceived[2]);//Print out the sequence number of the sent packet
					System.out.println("Time: " + splitReceived[3]);//Print out the current time the packet was constructed
					System.out.println("Payload Size: " + splitReceived[4]);//Print out the randomly picked pay load size
					System.out.println("--------- Ping Response Packet Payload ------------");
					System.out.println("Host: " + splitReceived[5]);// Not sure if the input is host name
					System.out.println("Class-name: " + splitReceived[6]);//Print out the class name
					System.out.println("User-name: " + splitReceived[8] + ", " + splitReceived[7]);//Print out the user's name and surname
					System.out.println("Rest: " + splitReceived[9]);//Print out the unused bytes left from the random pay load size
					System.out.println("---------------------------------------");
					pRTT = (rTimeStamp - iTimeStamp) / 1000;//Set RTT of the packet to the difference of the response received time and the time stamp field
					System.out.println("RTT: " + pRTT + " seconds\n");//Print out the RTT
					minRTT = Math.min(minRTT, pRTT);//Compare RTT values to find the min RTT
					maxRTT = Math.max(maxRTT, pRTT);//Compare RTT values to find the max RTT
					totalRTT += pRTT;//Add each RTT to the total RTT
					
					// If the response is not received before the wait seconds
				} catch (SocketTimeoutException ex) {
					System.out.println("--------------- Ping Response Packet Timed-Out ------------------\n");
					continue;
				}
			}
			
			// Close the socket
			clientSocket.close();

			// If there's any ping response, then calculate the average pay load size and the average RTT
			if (nPingResponse > 0) {
				aveRTT = (totalRTT / nPingResponse);
			}
			// If there's no ping response, then set the minimum, maximum, and average RTT to 0.
			if (nPingResponse == 0) {
				minRTT = 0;
				maxRTT = 0;
				aveRTT = 0;
			}
			
			// The percent loss is equal to (100 * (the number of packets - the number of ping responses)) / (number of packets)
			calcLoss = ((100) * (nPackets - nPingResponse)) / nPackets;

			//Calculate the average Pay Load Size
			ave_Pay_Load_Size = total_Pay_Load_size / nPackets;
			
			// Print out the summary which has the number of packets, the number of ping responses,the min RTT, the max RTT, the average RTT, the percent loss, and the average pay load size
			System.out.println("Summary: " + nPackets + " :: " + nPingResponse + " :: " + decimal.format(minRTT)
			+ " :: " + decimal.format(maxRTT) + " :: " + decimal.format(aveRTT) + " :: " + calcLoss + "% :: " + ave_Pay_Load_Size + "\r\n");
			
			// If the given server host does not exist, then give an error
		} catch (UnknownHostException e) {
			// If the host does not exist, then give error
			System.out.println("\r\nUnable to lookup IP address for host\n");
			System.exit(-1);
		}
	}// end main
}// end class
