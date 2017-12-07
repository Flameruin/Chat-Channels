Chat Channels
========

##  Students in the group and approximately how many hours of work:
Dekel Moshe  200862142 - 50 hours.

Dekel Temam 305242125 - 83.5 hours.

##  List of work:


Dekel Moshe - readme, log output, debugging,server register topic, server leave topic, server send topic sentence,handle client quit, exceptions handling


Dekel Temam - readme,thread safety, config file, handle client disconnect, server stop listening, server quit,handle client register for a topic, handle client leave a topic, handle client send a message, server close the worker thread, debugging, exceptions handling






##  Instructions for compiling the source code for both the client and server programs:
### Compiling source code for client program:
   * First Import the project into Eclipse
   * To create a new runnable JAR file in the workbench:
   * From the menu bar's File menu, select Export.
   * Expand the Java node and select Runnable JAR file. Click Next.
   * In the Runnable JAR File Specification page, select a 'Java Application' launch configuration to use to create a runnable JAR.
   * In the Export destination field, either type or click Browse to select a location for the JAR file.
   * In the Library handling select Extract required libraries into generated JAR
   * Select Launch configuration menu select GradeClient - GradeClient 
   * Press Finish 

### Compiling source code for server program:
   * First Import the project into Eclipse
   * To create a new runnable JAR file in the workbench:
   * From the menu bar's File menu, select Export.
   * Expand the Java node and select Runnable JAR file. Click Next.
   * In the Runnable JAR File Specification page, select a 'Java Application' launch configuration to use to create a runnable JAR.
   * In the Export destination field, either type or click Browse to select a location for the JAR file.
   * In the Library handling select Extract required libraries into generated JAR
   * Select Launch configuration menu select GradeServer - GradeThreeTiers
   * Press Finish 



##  Instructions for running the client and server programs:

 ### To Run a client program:

  * Launch Command Prompt:

     * To launch Command Prompt select Start -> Run and type cmd in the box.	or
				Open The Run Window (press Win+R on your keyboard to open it
				Then, type `cmd` or `cmd.exe` and press `Enter` or click/tap OK.



      * The Command Prompt shows up as a black terminal window,and it's not case sensitive.

      * The command prompt should look something like: `C:\`





       * Use the `cd` command to change directories, to the directory where your server jar is.

       * Type `cd` then the path to the directory containing the jar and press Enter(eg: cd c:\user\clientFolder)



  * To run the jar file use the java command.

       * Type `java -jar` and the complete server file name (eg: java -jar client.jar)



### To Run a server program:

   * Launch Command Prompt:

      * To launch Command Prompt select Start -> Run and type cmd in the box.	or
				Open The Run Window (press Win+R on your keyboard to open it
				Then, type `cmd` or `cmd.exe` and press `Enter` or click/tap OK.


      * The Command Prompt shows up as a black terminal window,and it's not case sensitive.

      * The command prompt should look something like: `C:\`





      * Use the `cd` command to change directories, to the directory where your server jar is.

      * Type `cd` then the path to the directory containing the jar and press Enter(eg: cd c:\user\serverFolder)



    * To run the jar file use the java command.

       * Type `java -jar` and the complete server file name (eg: java -jar server.jar)



##  Documentation of the configuration files:





 ### Documentation of the client configuration file:

   * The client configuration file is of `.txt` format

   * It’s name is clientConfig.txt

   * The first line is the port which the client program will try connect to

   * It is read by the client program.

   * The second line is the IP on which the client program will try to connect to

   * It is read by the client program.

   * The client configuration file should be at the same directory as the client jar.



   * It should look something like this:


```
       6667
       127.0.0.1
```


 ### Documentation of the server configuration file:

   * The client configuration file is of `.txt` format

   * It’s name is serverConfig.txt

   * The first line is the port which the server program will try listen to

   * It is read by the client program.

   * The second line is the IP on which the server program listened to

   * It is written by the server program after a successfull connection.

   * The server configuration file should be at the same directory as the server jar.

   * It should look something like this:

```
       6667
       127.0.0.1
```

  * Or this:
		
```
       6667
```		

 * Important Note: For the client to be able to connect to the server the ports in both configuration file must be the same.

 * To connect a client from a different PC to the server you need to write the IP of the PC which the server is on in the config file and the server need to listen to it or to all on (eg: 0.0.0.0)

 * P.S: You can find a PC’s IP by writing ipconfig in the command line

 * P.P.S: If you changed either of the config file the program will ask you to input the filename or full path

 * P.P.P.S: If you run either programs not from their directory you should input a full path for the config file.



## Documentation of how thread safety is ensured:

  * We ensured thread safety by synchronizing or locking key operations and resources, and making sure some resources are not reachable to threads that shouldn’t have access to them.

  * Key operations of the BufferedReader API are implemented using synchronized blocks, as such the Buffer reads each stream in a synchronized way.

  * We are using the Vector class methods that are synchronized (as well as most of the Vector API implements), we only use add and remove to change the vector as those methods work without an index and as such threads won’t try to add to same index.

  * When the client tries to get an important input from the user it locks the thread which listen to the server so it won’t write until it’s done, while it waits it saves all recent messages which it got from the server and once the user is done it writes them.

  * We have separated user input and outside input (client/server) to different threads to increase safety. 

  * The topics list which the client is subscribed to is saved inside each individual thread which handles the client as to not cause malfunction when looking for which clients is subscribed to which topic.

  * As serversocket accept method is unblockable we put a timeout on it inside the Listening class so it will always check if it should quit. 

  * As clients shouldn’t listen non stop we have given them a short sleep period.

  * When the server disconnects as to give some grace to the client it might reconnect we have not shut it off but due to the client program always waiting for input from user which is an unlockable method we always check before performing the called method if the server is connected before executing the called command, in case it’s not connected we call DISCONNECT and ask the user to try to reconnect (maybe call server operator) or quit.
