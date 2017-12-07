package il.ac.kinneret.mjmay.threeTier.gradeClient;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.Vector;








public class GradeClient extends Thread implements Runnable  {

	public static String configFileName = "clientConfig.txt";
	static Socket clientSocket = null;
	static volatile Boolean wait =false;
	volatile static Boolean isHandling=true;
	
	private static boolean endProgram = true;
	@Override
	public void run(/*Socket clientSocket*/)
	{	
		handleServer();
	}
	

	public void handleServer()
	{
		BufferedReader brServer = null;
		String sentence ="";
		Vector<String> sentences = new Vector<String>();
		
		try {
			brServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			while(isHandling)
			{
			//	 if (!Thread.currentThread().isInterrupted())
				//        break;

				
				while(isHandling/*sentence.trim().length() > 0*/){
				// read from the network
					
					sentence = brServer.readLine();					 
					if(sentence==null)return;
					if(sentence.trim().length() > 0)
					{
						synchronized (this) {
							//while(wait) {this.wait(10000);}
								
									sentences.addElement(sentence);
								 
									//if(!sentences.isEmpty())
									
									while(!sentences.isEmpty()&&!wait){
									System.out.println();
									System.out.println(sentences.remove(0));
									//System.out.println(sentence);
									System.out.println();
									}
						
					}
						//just to lower workload
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {

						}
					
					}
					
				}
			}
		}catch (IOException iox)
		{
			// network communication doesn't work.  just quit
			System.out.println("Error setting up network communication: " + iox.getMessage());
		}catch(java.lang.NullPointerException npe)
		{
			System.out.println("Error: " + npe.getMessage());
			
		}finally{
			try { clientSocket.close();return; } catch (Exception ex) {}
			
		}

	}
	
	
	public static void main(String[] args) {

		InetAddress serverIP = null;
		int port = 0;
		int stop=0;
		while(stop<2){
			// read the configuration file
		
			try {
				stop++;	
				// get the IP and port from the parameters
				BufferedReader configFileIn = new BufferedReader(new InputStreamReader(new FileInputStream(configFileName)));
				port = Integer.parseInt(configFileIn.readLine());
				serverIP = InetAddress.getByName(configFileIn.readLine());
				configFileIn.close();	
				//port = Integer.parseInt(args[1]);
				stop++;	
			}catch (FileNotFoundException ex) {
				System.out.println("Error: can't open configuration file: " + configFileName + ": " + ex.getMessage());
				if(stop==1){
					System.out.println("Last chanch to enter your config file");					
					getFileName();		
				}
				else
				{
					System.out.println("Bye!");
							return;
				} 
			}
			catch (UnknownHostException unx)
			{
				System.out.println("Error: Can't resolve host: " + unx.getMessage());
				showUsage();
				return;
			}
			catch (NumberFormatException nfe)
			{
				System.out.println("Error: Invalid port: " + nfe.getMessage());
				showUsage();
				return;
			}
			catch (Exception ex)
			{
				System.out.println("Error: ex: " + ex.getMessage());
				showUsage();
				return;
			}
		}
		

		System.out.println("Connecting to the server.");
		 clientSocket = null;
		try {
			clientSocket = new Socket(serverIP, port);
		} catch (IOException e) {
			System.out.println("Error connecting to server: " + e.getMessage());
			System.out.println("Bye!");
			return;
		}

		System.out.println("Connected to the server.");

		/////////////
		Thread threadListener = new Thread(new GradeClient());
		threadListener.start();
		////////////
		
		// read from the keyboard
		BufferedReader brKeyboard = new BufferedReader(new InputStreamReader(System.in));
	//	BufferedReader brNetwork = null;
		PrintWriter pwNetwork = null;
		try {
			// read from the network
		//	brNetwork = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			// write to the network
			pwNetwork = new PrintWriter(clientSocket.getOutputStream());
		}
		catch (IOException iox)
		{
			// network communication doesn't work.  just quit
			System.out.println("Error setting up network communication: " + iox.getMessage());
			try { clientSocket.close(); } catch (Exception ex) {}
			return;
		}
		
		Scanner reader=null;// = new Scanner(System.in);  // Reading from System.in
		try {		
			while (endProgram) //when user click D/C end the loop!
			{	
				reader = new Scanner(System.in);
				int choice=-1;
				do
				{
					try{
					System.out.println("Enter your choice: ");
					System.out.println("1: REGISTER");
					System.out.println("2: LEAVE");
					System.out.println("3: Send a message - enter the topic + sentence");
					System.out.println("4: Disconnect");
					
				//	String line = brIn.readLine();
					
					String line=reader.nextLine();
					choice = Integer.parseInt(line.trim());
		
					}
							catch (Exception ex) {
							//	reader.reset();
								 choice=-1;
								 if(!threadListener.isAlive()){choice=4;continue;}
								System.out.print("Error parsing choice\n: ");
							}
				}
				while(choice < 0 || choice > 4 && threadListener.isAlive());
				
				//reader.close();
				String sendTopic;
				if(!threadListener.isAlive()) {System.out.println("Connection lost");choice=4;}//if Listener dead disconnect
				 switch (choice) {
		            case 1: //REGISTER
						System.out.print("Enter a Topic to Register: ");
						sendTopic=getUserInput(brKeyboard); //sendTopic = brKeyboard.readLine();
						// it's not blank, look it up
						if (sendTopic.trim().length() == 0 || sendTopic==null)
						{
							System.out.println("Blank is not a legal format");
							System.out.println();
							break;
						}
						sendTopic = "REGISTER " +  sendTopic.toUpperCase();
						pwNetwork.println(sendTopic);
						pwNetwork.flush();			
		                     break;
		            case 2: //LEAVE
						System.out.print("Enter a Topic to Leave: ");
						// if it's blank, quit
						sendTopic=getUserInput(brKeyboard);//sendTopic = brKeyboard.readLine();
						if (sendTopic.trim().length() == 0 || sendTopic==null)
						{
							System.out.println("Blank is not a legal format");
							System.out.println();
							break;
						}
						sendTopic = "LEAVE " +  sendTopic.toUpperCase();
						// it's not blank, look it up
						pwNetwork.println(sendTopic);
						pwNetwork.flush();
		            	
		                     break;
		            case 3://SEND - the user not need to be registered he just need to send "topic + sentence" - ex dog I Love dogs
						System.out.print("Enter sentence: ");
						// if it's blank, quit
						 //sendTopic =brKeyboard.readLine();
						synchronized (brKeyboard) {
							sendTopic=getUserInput(brKeyboard);
						}
						if (sendTopic.trim().length() == 0|| sendTopic==null)
						{
							System.out.println("Blank is not a legal format");
							System.out.println();
							break;
						}
						
					//	String sen =sendTopic;
						sendTopic = "SEND " +  sendTopic;
						// it's not blank, look it up
						pwNetwork.println(sendTopic);
						pwNetwork.flush();
		            	
		                     break;
		            case 4://Disconnect
		            	//threadListener.stop();
		            	isHandling=false;
		            	
		            	///
		            	
		            	//threadListener.interrupt();
		            	//threadListener.join();
		            	pwNetwork.println("Disconnect");
		            	pwNetwork.flush();
		            	System.out.println("Press:" );
		            	System.out.println("1: to try and connect again: ");
		            	System.out.println("any other keys: to quit  ");
		            	choice = reader.nextInt();
		          
		            	if(choice==1) //restart the connection
		            	{
		            		isHandling=true;
		            		//this part is copied for the top.
		            		//////////////////////////////////////////////////////////////////////////////
		            		try {
		            			// get the IP and port from the parameters
		            			BufferedReader configFileIn = new BufferedReader(new InputStreamReader(new FileInputStream(configFileName)));
		            			port = Integer.parseInt(configFileIn.readLine());
		            			serverIP = InetAddress.getByName(configFileIn.readLine());
		            			configFileIn.close();	
		            		}
		            		catch (UnknownHostException unx)
		            		{
		            			System.out.println("Error: Can't resolve host: " + unx.getMessage());
		            			showUsage();
		            			return;
		            		}
		            		catch (NumberFormatException nfe)
		            		{
		            			System.out.println("Error: Invalid port: " + nfe.getMessage());
		            			showUsage();
		            			return;
		            		}
		            		catch (Exception ex)
		            		{
		            			System.out.println("Error: ex: " + ex.getMessage());
		            			showUsage();
		            			return;
		            		}		

		            		System.out.println("Connecting to the server.");
		            		 clientSocket = null;
		            		try {
		            			clientSocket = new Socket(serverIP, port);
		            		} catch (IOException e) {
		            			System.out.println("Error connecting to server: " + e.getMessage());
		            			System.out.println("Bye!");
		            			return;
		            		}

		            		System.out.println("Connected to the server.");
		        /////////////
		            		//threadListener.resume(); //the problem is here!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		            		////////////
		            		
		            		// read from the keyboard
		            		 brKeyboard = new BufferedReader(new InputStreamReader(System.in));
		            	//	BufferedReader brNetwork = null;
		            		 pwNetwork = null;
		            		try {
		            			// read from the network
		            		//	brNetwork = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		            			// write to the network
		            			pwNetwork = new PrintWriter(clientSocket.getOutputStream());
		            		}
		            		catch (IOException iox)
		            		{
		            			// network communication doesn't work.  just quit
		            			System.out.println("Error setting up network communication: " + iox.getMessage());
		            			System.out.println("Bye!");
		            			try { clientSocket.close(); } catch (Exception ex) {}
		            			return;
		            		}
		            		/////////////////////////////////////////////////////////////////////////////
		        /////////////
		            		threadListener = new Thread(new GradeClient());
		            		threadListener.start();
		            		////////////
		            		break;
		            		
		            	}else //end program
		            	{
			            	endProgram = false;
		            	}
		            	

		            	break;
		            default:
		            	System.out.print("Error \n: ");
		                     break;
		        }
				
				

				// wait for a response
				
				// String resultLine = "";

				
				/*
				do {
					// read the next line
					resultLine = brNetwork.readLine();
					if ( resultLine != null)
					{
						// show it if it's not null
						System.out.println(resultLine.trim());
					}
					else
					{
						// something wacky happened
						break;
					}
				} while (resultLine.trim().length() > 0);
*/
				// now get the next line
				///////close reader
				
			}
					
		}catch(Exception w) {}
		/*catch (IOException iox)
		{
			System.out.println("Error in network communication: " + iox.getMessage());					
		}*/
		finally {
			try { clientSocket.close(); reader.close();} catch (Exception ex) {}
		}
		
		// all done!
		System.out.println("Closed connection and done.");

	}

	private static void showUsage()
	{
		System.out.println("Usage: GradeClient ServerIP ServerPort");
	}
	
	private static String getUserInput(BufferedReader brKey)// throws IOException
	{
		wait=true;
		String sentence=null;
		try {
			sentence = brKey.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		wait=false;
		return sentence;
		
	}
	
	private static void getFileName() {
		System.out.println("Enter the config file name or full path:");
		try {
			BufferedReader brFileName = new BufferedReader(new InputStreamReader(System.in));
			configFileName=brFileName.readLine();	
			return;
			//BufferedReader configFileIn = new BufferedReader(new InputStreamReader(new FileInputStream(Constants.configFileName)));;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
