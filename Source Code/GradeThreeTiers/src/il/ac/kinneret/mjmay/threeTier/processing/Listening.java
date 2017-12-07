package il.ac.kinneret.mjmay.threeTier.processing;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

import il.ac.kinneret.mjmay.threeTier.GradeServer;

public class Listening extends Thread {
	
	ServerSocket listeningSocket;
	public Listening (ServerSocket serverSocket)
	{
		// save the socket we've been provided
		listeningSocket = serverSocket;
	}
	
	@Override
	public void run()
	{
		// start to listen on the socket
		try {
			Socket clientSession=null;
			while (true)
			{  	listeningSocket.setSoTimeout(1000);//setSoTimeout Dekel added
			
				try {//try catch for timeout
				clientSession = listeningSocket.accept();
				}catch (SocketTimeoutException e) {
					// TODO: handle exception
					if(GradeServer.quit)
						break;
					continue;
				}
				// see if we were interrupted - then stop
				if (this.isInterrupted())
				{
					System.err.println("Stopped listening since we were interrupted.");
					return;
				}
				// create a new handling thread for the client
				
				HandleClient clientThread = new HandleClient(clientSession,GradeServer.threadPoitionInVecotr++);
				clientThread.start();
				
			}			
		} catch (IOException e) {
			if(!GradeServer.quit)//check if server forced quitting
			// problem with this connection, show the output and quit
			System.err.println("Error listening for connections: " + e.getMessage());
			return;
		}		
	}

}
