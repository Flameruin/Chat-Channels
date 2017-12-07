package il.ac.kinneret.mjmay.threeTier;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
//import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

import java.util.Enumeration;
import java.util.Vector;

import il.ac.kinneret.mjmay.threeTier.processing.Constants;
import il.ac.kinneret.mjmay.threeTier.processing.Listening;

public class GradeServer {
	public static Vector<Vector<String>> topic = new Vector<Vector<String>>();
	
	//Vector<InetAddress> adds = new Vector<InetAddress>();
	public static int threadPoitionInVecotr = 0;
	public static Vector<Socket> clients = new Vector<Socket>(); //all IPs store here
	// boolean stop = false;
	public static Boolean quit = false;

	
	public static void main(String[] args) {

		
		// we need to get a port from the user
		int port = 0;
		
		
		int stop=0;
		while(stop<2){
		// read the configuration file
		try {
			stop++;	
			//BufferedReader configFileIn = new BufferedReader(new InputStreamReader(new FileInputStream(args[1])));
			BufferedReader configFileIn = new BufferedReader(new InputStreamReader(new FileInputStream(Constants.configFileName)));
			// read the connection strings
			//String connectionString1 = configFileIn.readLine();
			//Constants.setConnectionStringBooklets("lol");
			port = Integer.parseInt(configFileIn.readLine());
			//System.out.println(port);
			//String connectionString2 = configFileIn.readLine();
			//Constants.setConnectionStringGrades(connectionString2);;
			configFileIn.close();	
			//Class.forName("org.sqlite.JDBC");	
			stop++;	
		} catch (FileNotFoundException ex) {
			System.out.println("Error: can't open configuration file: " + Constants.configFileName + ": " + ex.getMessage());
			if(stop==1){
				System.out.println("Last chanch to enter your config file");					
				getFileName();		
			}
			else
			{
				System.out.println("Bye!");
						return;
			} 
		} catch (IOException e) {
			System.out.println("Error reading configuration file: " + Constants.configFileName + ": " + e.getMessage());
			
			return;
		}catch (NumberFormatException nfe)
		{
			System.out.println("Error: Invalid port: " + nfe.getMessage());
			
			return;
		}
		}
		/*catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/	

		// make a list of addresses to choose from
		// add in the usual ones
		Vector<InetAddress> adds = new Vector<InetAddress>();
		try {
			adds.add(InetAddress.getByAddress(new byte[] {0, 0, 0, 0})); //isn't synchronized
			adds.addElement(InetAddress.getLoopbackAddress()); //synchronized
		} catch (UnknownHostException ex) {
			// something is really weird - this should never fail
			System.out.println("Can't find IP address 0.0.0.0: " + ex.getMessage());
			ex.printStackTrace();
			return;
		}

		try {
			// get the local IP addresses from the network interface listing
			Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();

			while ( interfaces.hasMoreElements() )
			{
				NetworkInterface ni = interfaces.nextElement();
				// see if it has an IPv4 address
				Enumeration<InetAddress> addresses =  ni.getInetAddresses();
				while ( addresses.hasMoreElements())
				{
					// go over the addresses and add them
					InetAddress add = addresses.nextElement();
					if (!add.isLoopbackAddress())
					{
						adds.addElement(add);
					}				
				}
			}
		}
		catch (SocketException ex)
		{
			// can't get local addresses, something's wrong
			System.out.println("Can't get network interface information: " + ex.getLocalizedMessage());
			return;
		}

		System.out.println("Choose an IP address to listen on :");
		for (int i = 0; i < adds.size(); i++)
		{
			// show it in the list
			System.out.println(i + ": " + adds.elementAt(i).toString());			
		}
		System.out.println(adds.size() + ": Insert manually");
		BufferedReader brIn = new BufferedReader(new InputStreamReader(System.in));
		int choice = -1;

		while ( choice < 0 || choice >= (adds.size()+1))
		{
			System.out.print(": ");
			try {				
				String line = brIn.readLine();
				choice = Integer.parseInt(line.trim());
			}
			catch (Exception ex) {
				System.out.print("Error parsing choice\n: ");
			}			
		}

		// the listen/stop loop
		String lineIn = "";		
		
		do {
			// start to listen on the one that the user chose
			ServerSocket listener = null;
			
				if(choice==(adds.size()))
				{
					String input=null;
					try {
						input = brIn.readLine();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						System.out.print("Error parsing choice\n: ");
					}
					try{
						InetAddress IP = InetAddress.getByName(input);
						listener = new ServerSocket(port, 50,IP);	
						Listening listening = new Listening(listener);
						listening.start();
						
							// write to the configuration file
							try {
								BufferedWriter configFileOut = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(Constants.configFileName)));					
								configFileOut.write(port + "\r\n" + IP );
								configFileOut.close();
							} catch (FileNotFoundException ex) {
								System.out.println("Error: can't open configuration file: " + Constants.configFileName + ": " + ex.getMessage());
								
								return;
							} catch (IOException e) {
								System.out.println("Error reading configuration file: " + Constants.configFileName + ": " + e.getMessage());
								
								return;
							}catch (NumberFormatException nfe)
							{
								System.out.println("Error: Invalid port: " + nfe.getMessage());
								
								return;
							}	} 
				catch (Exception e) {
					// fatal error, just quit // probably NSA listening
					System.out.println("Can't listen on " + input + ":" + port);		
					System.out.println("Bye!");
					//e.printStackTrace();
					return;
				}
						
				}
				else
				{
					try {
						listener = new ServerSocket(port, 50, adds.elementAt(choice));
						Listening listening = new Listening(listener);
						listening.start();
						
							// write to the configuration file
							try {BufferedWriter configFileOut = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(Constants.configFileName)));					
								configFileOut.write(port + "\r\n" + adds.elementAt(choice) );
								configFileOut.close();
							} catch (FileNotFoundException ex) {
								System.out.println("Error: can't open configuration file: " + Constants.configFileName + ": " + ex.getMessage());
								
								return;
							} catch (IOException e) {
								System.out.println("Error reading configuration file: " + Constants.configFileName + ": " + e.getMessage());
								
								return;
							}catch (NumberFormatException nfe)
							{
								System.out.println("Error: Invalid port: " + nfe.getMessage());
								
								return;
							}
						} 
					catch (IOException e) {
						// fatal error, just quit // probably NSA listening
						System.out.println("Can't listen on " + adds.elementAt(choice) + ":" + port);
						System.out.println("Bye!");
						//e.printStackTrace();
						return;
					}
				}
			

			// listen for the command to stop listening
			do {
				// we now have a working server socket, we'll use it later
				System.out.println("Listening on " + listener.getLocalSocketAddress().toString());
				System.out.println("Enter 'STOP' to stop listening");

				try {
					lineIn = brIn.readLine();
				} catch (IOException ex) 
				{ 
					System.out.println("Error in reading from console: " + ex.getMessage()); 
				}
				
			} while ( !lineIn.trim().toLowerCase().equals("stop"));
			
			// stop listening
			try {
			//	Socket dummy = new Socket(InetAddress.getLocalHost(),6667);	
				
				quit=true;
				for (Socket clien : clients) {
					clien.close();
				//	PrintWriter pwOut = new PrintWriter(clien.getOutputStream());
					
				}
				listener.close();
				//dummy.close();
			} catch (IOException e) {
				// error while stopping to listen?  weird
				System.out.println("Error stopping listening: " + e.getMessage());
			}

			// now we can resume listening if we want
			System.out.println("Resume listening? [y/n]");
			do {
				System.out.print(": ");
				try {
					lineIn = brIn.readLine();
				} catch (IOException ex) 
				{ 
					System.out.println("Error in reading from console: " + ex.getMessage()); 
				}

			} while ( !lineIn.trim().toLowerCase().equals("y") && !lineIn.trim().toLowerCase().equals("n"));

			// see whether we have an n or a y
			if ( lineIn.trim().toLowerCase().equals("y"))
			{
				quit = false;
				System.out.println("Resuming listening");
			}
			else
			{
				//quit = true;	
				// quitting
				try {brIn.close();} catch (IOException e) {}
				System.out.println("Bye!");
				
			}
		} while (!quit);
		
		return;
	}


	
	private static void getFileName() {
		System.out.println("Enter the config file name  or full path::");
		try {
			BufferedReader brFileName = new BufferedReader(new InputStreamReader(System.in));
			Constants.setConfigFileName(brFileName.readLine());;	
			return;
			//BufferedReader configFileIn = new BufferedReader(new InputStreamReader(new FileInputStream(Constants.configFileName)));;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	

}
