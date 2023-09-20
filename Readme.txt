Student Name: Feyza Sakin 
=============
Project Title: Ping Programming
Files for the project: PINGServer.java, PINGServer.java
=============

File name: PINGServer.java
Class name: PINGServer
=============
Description: With the given inputs of the port number, the program receives packets from the client. It prints out the header and payload of the received packets. Then it generates a random number from 1 to 100 for each packet. If the random number is bigger than the second input percentage loss, then it sends a response packet to the client and it prints out the response packet's header and pay load size. If the random number is smaller than or equal to the second input percentage loss, then it does not send a response packet to the client.
=============  
Inputs:	argv[0] || The port number
		argv[1] || The percentage loss
=============
Usage:
		Compile by writing "javac PINGServer.java".
		Then write "java PINGServer" with the following commands.
		For argv[0], write the port number. It must be between 10000 - 11000 because they are open for use.
		For argv[1], write a percentage loss number from 0 to 100.
		*This must be run before compiling and running the commands for the PINGClient.java

=============
File name: PINGClient.java
Class name: PINGClient
=============
Description: With the given inputs IP or the hostname of the server and the port number, the program sends the input number of packets to the server. For each packet, it generates a random number from 150 to 300 as the payload size and gives a random of letters and numbers as the rest. Then it prints out the header and payload of the sent packets, and it waits for the response packets from the server for the input number of seconds. If the response is received from the server within the wait seconds, then it prints out the received packet's header and payload size. If the response is not received from the server within the input seconds, then it sends the next packet to the server. At the end, it summarizes the number of sent packets, the number of response packets, and calculated minimum RTT, maximum RTT, average RTT, and the lost packets' percentage, and the average payload size.
=============  
Inputs:	argv[0] || The IP or the hostname of the server
		argv[1] || The port number
		argv[2] || The Client ID
		argv[3] || The Number of Packets
		argv[4] || The seconds the client can wait for the response 
=============
Usage:
		Compile by writing "javac PINGClient.java".
		Then write "java PINGClient" with the following commands.
		For argv[0], write the IP or the hostname of the server.
		For argv[1], write the port number. It must be the same port number that's used in the PINGServer.
		Also, it must be between 10000 - 11000 because they are open for use.
		For argv[2], type the client ID
		For argv[3], write the number of packets
		For argv[4], write the seconds the client can wait for the response 
		*This must be run after compiling and running the commands for the PINGServer.java

=============
