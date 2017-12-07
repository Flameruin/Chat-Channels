package il.ac.kinneret.mjmay.threeTier.processing;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Calendar;

//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.SQLException;
import java.util.IllegalFormatException;
//import java.util.regex.Matcher;
//import java.util.Vector;
//import java.util.regex.Pattern;
import java.util.Vector;

import il.ac.kinneret.mjmay.threeTier.GradeServer;

public class HandleClient extends Thread {

	Socket clientSocket;
	int threadPoitionInVecotr;
	Vector<String> myTopics = new Vector<String>();


	public HandleClient (Socket socket, int threadPoitionInVecotr)
	{
		clientSocket = socket;	
		this.threadPoitionInVecotr = threadPoitionInVecotr;//save the number to lookup in the vector
	//	GradeServer.clients.insertElementAt(clientSocket, threadPoitionInVecotr);////// the safe one
		GradeServer.clients.addElement(clientSocket);//////
		//GradeServer.topic.setSize(GradeServer.topic.size()+1);
		//GradeServer.topic.setElementAt(null, threadPoitionInVecotr);
		myTopics.addElement(socket.toString());///////////////
		GradeServer.topic.add(myTopics);////////////////
		//System.out.println("the position of this thread in the vector is: " + this.threadPoitionInVecotr);
	}


	public void run()
	{
		BufferedReader brIn = null;
		PrintWriter pwOut = null;
		try {
			brIn = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			pwOut = new PrintWriter(clientSocket.getOutputStream());

		} catch (IOException ex) {
			// this client isn't usable
			System.out.println("Error setting up connection with the client: " + clientSocket.getRemoteSocketAddress().toString() 
					+ ": " + ex.getMessage());
			try {clientSocket.close();} catch (Exception e) {}
		}

		System.out.println(getDataAndTime() + "Got connection from: " + clientSocket.getRemoteSocketAddress());



		try {
			// get all the sentence from client
			while (true && clientSocket!=null && !clientSocket.isClosed())
			{
				// get the sentence from the client
				String sentence = brIn.readLine();

				//System.out.println("here");
				//if(clientSocket.isClosed())break;
				// see if this is blank, if yes, client is done
				if ( sentence == null || sentence.trim().length() == 0)
				{
					break;
				}
				if (sentence.equalsIgnoreCase("Disconnect"))
				{
					for(int i=0;i<GradeServer.clients.size();i++)
					{
						if(clientSocket==GradeServer.clients.elementAt(i))
							GradeServer.clients.remove(i);
					}
					
					
					//try {clientSocket.close();} catch (Exception e) {}
		return;			
					//break;
				}

				// reset the topic set
				//topic.clear();
				
				// the sentence should be with subject/topic
				String subject = null;
				try {
					subject = getFirstWord(sentence);
				} catch (IllegalFormatException e)
				{
					// something is screwy, just send back a blank and try again
					pwOut.println();
					pwOut.flush();
					continue;
				}
				String topic;
				 switch (subject) {
		            case "REGISTER":
		            	
		            	 
		            	
		            	 System.out.println(getDataAndTime() + clientSocket.getRemoteSocketAddress() + " " + "client command: " + subject);
		            	//check if the client is already register to the topic
		            	try
		            	{
		            	    //System.out.println(LocalTime.now().getHour() + ":" + LocalTime.now().getMinute()+ ":" + LocalTime.now().getSecond());
		            		 if(countWords(sentence)>2)
			            	 {
			            		 System.out.println("two words");
			 		            		System.out.println(getDataAndTime()  + clientSocket.getRemoteSocketAddress() + " " + "reply the worker thread: " + "ERROR");
					            		//return error to the user
										pwOut.println("ERROR");
										// send an empty line to indicate we're done
										pwOut.println();
										pwOut.flush();
										          	
			            	 }else{
			            	   topic = getSecondtWord(sentence); //got the topic here
	
			            		 boolean notRegisteredForTopic = true;
			            		 for(int i=0;i<myTopics.size();i++)
			            		 {
				 		            	if(myTopics.elementAt(i).equalsIgnoreCase(topic))//client not register for this topic
				 		            	{      
				 		            		System.out.println(getDataAndTime()  + clientSocket.getRemoteSocketAddress() + " " + "reply the worker thread: " + "ERROR");
						            		//return error to the user
											pwOut.println("ERROR");
											// send an empty line to indicate we're done
											pwOut.println();
											pwOut.flush();
											notRegisteredForTopic = false;
				 		            	}
			            		 }
			            		
				            	if(notRegisteredForTopic)// need to register the client
				            	{
				            		myTopics.add(topic.toUpperCase());          		
				            		for(int i=0;i<GradeServer.topic.size();i++) {
				            		if(GradeServer.topic.elementAt(i).firstElement().equalsIgnoreCase(clientSocket.toString()))
				            		GradeServer.topic.set(i, myTopics);
				            		}
				            		//GradeServer.topic.set(threadPoitionInVecotr,myTopics);
				            		System.out.println(getDataAndTime() + clientSocket.getRemoteSocketAddress() + " " + "Registerd to " + topic);
									System.out.println(getDataAndTime()  + clientSocket.getRemoteSocketAddress() + " " +  "reply the worker thread: " + "OK");
									//send to the client
									pwOut.println("OK" );
									// send an empty line to indicate we're done
									pwOut.println();
									pwOut.flush();
				            	}
			            	 }
		            	}
		        		catch (IndexOutOfBoundsException ex) //if the array not even created yet
						{
		        			/*
		        			myTopics.add(topic.toUpperCase());
		            		for(int i=0;i<GradeServer.topic.size();i++) {
		            		if(GradeServer.topic.elementAt(i).firstElement().equalsIgnoreCase(clientSocket.toString()))
		            		GradeServer.topic.set(i, myTopics);
		            		}
		            		
							System.out.println(getDataAndTime() + clientSocket.getRemoteSocketAddress() + " " +  "reply the worker thread: " + "OK");
								//send to the client
								pwOut.println("OK");
								// send an empty line to indicate we're done
								pwOut.println();
								pwOut.flush();	
								*/
						}

		                     break;
		            case "LEAVE":
		            	 System.out.println(getDataAndTime() + clientSocket.getRemoteSocketAddress() + " " + "client command: " + subject);
		            	 
		            	 
		            	 try
		            	 {
			            		 if(countWords(sentence)>2)
			            	 {
			            		 System.out.println("two words");
			 		            		System.out.println(getDataAndTime()  + clientSocket.getRemoteSocketAddress() + " " + "reply the worker thread: " + "ERROR");
					            		//return error to the user
										pwOut.println("ERROR");
										// send an empty line to indicate we're done
										pwOut.println();
										pwOut.flush();
										          	
				            	 }else{topic = getSecondtWord(sentence); //got the topic here
				            		 boolean notRegisteredForTopic = true;
				            		 for(int i=0;i<myTopics.size();i++)
				            		 {	            			 
				            			 if(myTopics.elementAt(i)==null)
				            				 continue;
					 		            	if(myTopics.elementAt(i).equalsIgnoreCase(topic))//client not register for this topic
					 		            	{      
					 		            		myTopics.remove(topic); //remove the topic at specific place
					 		            		System.out.println(getDataAndTime() + clientSocket.getRemoteSocketAddress() + " " + "Left " + topic);
					 		            		System.out.println(getDataAndTime() + clientSocket.getRemoteSocketAddress() + " " +  "reply the worker thread: " + "OK");			 						
						 						//send to the client
						 						pwOut.println("OK");
						 						// send an empty line to indicate we're done
						 						pwOut.println();
						 						pwOut.flush();	
						 						notRegisteredForTopic = false;
						 						break;
					 		            	}
				            		 }
				 		            	if(notRegisteredForTopic) //equals not contain //check if the topic already in the array
				 		            	{
				 		            		System.out.println(getDataAndTime() + clientSocket.getRemoteSocketAddress() + " " +  "reply the worker thread: " + "ERROR");
											//send to the client
											pwOut.println("ERROR");
											// send an empty line to indicate we're done
											pwOut.println();
											pwOut.flush();
				 		            	}
				            	 }

		            	 }
			        		catch (IndexOutOfBoundsException ex) //if the array not even created yet
							{
			        			System.out.println(getDataAndTime()  + clientSocket.getRemoteSocketAddress() + " " +  "reply the worker thread: " + "ERROR");
								//send to the client
								pwOut.println("ERROR");
								// send an empty line to indicate we're done
								pwOut.println();
								pwOut.flush();
							}
		            	
		                     break;
		                    
		            case "SEND": //send everyone Apart from the sender  in his chosen topic 
		            	
		            	
		            	 System.out.println(getDataAndTime() + clientSocket.getRemoteSocketAddress() + " " + "client command: " + subject);
		            	topic = getSecondtWord(sentence); //got the topic here
		            	sentence = sentence.substring(sentence.indexOf(" ")).trim(); //cut the first word - "SEND"
		            	try 
		            	{
		            	sentence = sentence.substring(sentence.indexOf(" ")).trim(); //cut the second word - topic. got only the sentence
						//System.out.println("("+topic+") "+clientSocket.getRemoteSocketAddress() + ": got meesge: " + sentence);
		            	System.out.println(getDataAndTime() + clientSocket.getRemoteSocketAddress() + " " +  "reply the worker thread: " + "OK");	
		            	pwOut.println("OK");
						pwOut.println();
						pwOut.flush();
		            	}
		            	catch(Exception e)
		            	{
		            		System.out.println(getDataAndTime() + clientSocket.getRemoteSocketAddress() + " " +  "reply the worker thread: " + "ERROR");
			            	pwOut.println("ERROR");
							pwOut.println();
							pwOut.flush();
							break;
		            		//sentence = "";
		            		//System.out.println("("+topic+") "+clientSocket.getRemoteSocketAddress() + ": got meesge: "); //in case there is no sentence. needs to be blocked from the client!!!
		            	}
		            	

						
						//send to the client

							Vector<String> topicsArr = new Vector<String>();
							for(int i=0;i<GradeServer.topic.size();i++)
							{
								topicsArr = GradeServer.topic.elementAt(i);
									
								for(int j=0;j<topicsArr.size();j++)
								{
									
									try
									{	PrintWriter pwForward = null;
										if(topicsArr.elementAt(j).equalsIgnoreCase(topic)) //need to send this client Messge
										{
											for(int l=0;l<GradeServer.clients.size();l++) {
												if(topicsArr.firstElement().equalsIgnoreCase(GradeServer.clients.elementAt(l).toString()) && clientSocket!=GradeServer.clients.elementAt(l))
												{	
												System.out.println(getDataAndTime() + clientSocket.getRemoteSocketAddress() + " Forward messege: \""+ "(" + topic.toUpperCase() + ") " + clientSocket.getRemoteSocketAddress() +" " + getTime() +"- " + sentence  +"\" to " + GradeServer.clients.elementAt(l).getRemoteSocketAddress());
												pwForward = new PrintWriter(GradeServer.clients.elementAt(l).getOutputStream());
												pwForward.println("(" + topic.toUpperCase() + ") " + clientSocket.getRemoteSocketAddress() +" " + getTime() +"- " + sentence );//+ " " +GradeServer.clients.elementAt(threadPoitionInVecotr) );
												// send an empty line to indicate we're done
												pwForward.println();
												pwForward.flush();
												}
											}
										}	

										
									}
									catch(NullPointerException nullex)
									{
										//nullex.getMessage();
									}
	
								}
							}
						
							
		            	
		                     break;
		            case "DISCONNECT":
		            	
		                     return;
		            default:
		            	
		                     break;
		        }
			}
		}
		catch (IOException ex)
		{	if(!GradeServer.quit)//check if server forced quitting or// something is wrong		
			System.out.println("Error communicating with the client: " + clientSocket.getRemoteSocketAddress().toString() 
					+ ": " + ex.getMessage());
			try {clientSocket.close();} catch (Exception e) {}
		/*} catch (SQLException e) {
			System.err.println("Error querying databases: " + e.getMessage());		
			*/
		}
		finally
		{
			// close up shop
			/*
			if (connBooklets != null)
			{
				try { connBooklets.close();} catch (Exception ex) {}
			}
			if ( connGrades != null)
			{
				try { connGrades.close();} catch (Exception ex) {}
			}
			*/
			if (clientSocket != null)
			{
				try 
				{ 
					System.out.println(getDataAndTime() + clientSocket.getRemoteSocketAddress() + " closing.");
					clientSocket.close();
					
				} catch (Exception ex) {}
			}
			
		}
		return;
	}
	private String getDataAndTime()
	{
		return ( new SimpleDateFormat("yyyy/MM/dd HH:mm:ss ").format(Calendar.getInstance().getTime()) ).toString();
	}
	private String getTime()
	{
		return (LocalTime.now().getHour() + ":" + LocalTime.now().getMinute()+ ":" + LocalTime.now().getSecond() +" ").toString();
	}
	
	private String getFirstWord(String sentence)
	{	  
			    if (sentence.indexOf(' ') > -1) { // Check if there is more than one word.
			      return sentence.substring(0, sentence.indexOf(' ')); // Extract first word.
			    } else {
			      return sentence; // Text is the first word itself.
			    }			  	
	}	
	private  String getSecondtWord(String sentence) {
		if (sentence.indexOf(' ') > -1) { // Check if there is more than one word.
			sentence = sentence.substring(sentence.indexOf(" ")).trim();// delete the first word
			if (sentence.indexOf(' ') > -1) {
				return sentence.substring(0, sentence.indexOf(' ')); // Extract first word(the second word from the
															// original).
			}
			else { return sentence;}
		} else {
			return null; // Text is the first word itself.
		}
	}
	
	private  int countWords(String s){

	    int wordCount = 0;

	    boolean word = false;
	    int endOfLine = s.length() - 1;

	    for (int i = 0; i < s.length(); i++) {
	        // if the char is a letter, word = true.
	        if (Character.isLetter(s.charAt(i)) && i != endOfLine) {
	            word = true;
	            // if char isn't a letter and there have been letters before,
	            // counter goes up.
	        } else if (!Character.isLetter(s.charAt(i)) && word) {
	            wordCount++;
	            word = false;
	            // last word of String; if it doesn't end with a non letter, it
	            // wouldn't count without this.
	        } else if (Character.isLetter(s.charAt(i)) && i == endOfLine) {
	            wordCount++;
	        }
	    }
	    return wordCount;
	}


}
