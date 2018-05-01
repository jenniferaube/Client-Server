package server;
/* File: FishStickServer.java
 * Author: Jennifer Aube, based on starter code by Stanley Pieda
 * Modified Date:February 2018
 * Description: Networking server that uses simple protocol to send and receive transfer objects.
 */
import java.io.IOException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.ServerSocket;
import java.net.SocketAddress;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.ServerSocket;
import java.net.SocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import datatransfer.FishStick;
import datatransfer.Message;

import dataaccesslayer.FishStickDao;
import dataaccesslayer.FishStickDaoImpl;

/**
 * Server class will create the connection to listen for any clients wanting to make a connection
 * @author Jennifer Aube
 */
public class FishStickServer {
	private ServerSocket server;
	private Socket connection;
	private int messagenum;
	private int portNum = 8081;	
	public static ExecutorService threadExecutor = Executors.newCachedThreadPool();

	Message msg;
	FishStick fs = new FishStick();
	String inputMsg;
	/**
	 * main point of execution for program
	 * @param args
	 * @author Jennifer Aube
	 */
	public static void main(String[] args) {
		if(args.length > 0){
			(new FishStickServer(Integer.parseInt(args[0]))).runServer();
		}else{
			(new FishStickServer(8081)).runServer();
		}

	}
	/**
	 * contruction that will instantiate the port number
	 * @param portNum
	 * @author Jennifer Aube
	 */
	public FishStickServer(int portNum){
		this.portNum = portNum;
	}
	/**
	 * talkToClient method will listen for any message being passed to the server from the client
	 * @param connection
	 * @author Jennifer Aube
	 */
	public void talkToClient(final Socket connection){

		threadExecutor.execute( new Runnable () {
			public void run(){	
				ObjectOutputStream output = null;
				ObjectInputStream input = null;
				String message = "";
				System.out.println("Got a connection");
				try {
					SocketAddress remoteAddress = connection.getRemoteSocketAddress();
					String remote = remoteAddress.toString();
					output = new ObjectOutputStream (connection.getOutputStream());
					input = new ObjectInputStream( connection.getInputStream());  

					msg = (Message)input.readObject();
					if (msg.getCommand().equalsIgnoreCase("add")){
						try {
							FishStickDaoImpl fishstickDB = new FishStickDaoImpl();
							fs = msg.getFishStick();
							fishstickDB.insertFishStick(fs);
							fs = fishstickDB.findByUUID(fs.getUUID());
							if(fs != null){
								if(fs.getId() != 0){
									msg = new Message("command_worked", fs);
									String s = " Command: insert Returned FishStick: " + fs.getId() + ", " + fs.getRecordNumber() + ", " + fs.getOmega() 
									+ ", " + fs.getLambda() + ", " + fs.getUUID();
									System.out.println("From: " + remote + s);
									output.writeObject(msg);
								}
							}else{
								String fail = "command_failed";
								msg = new Message(fail, fs);
								System.out.println("From: " + remote + " Command: " + fail);
								output.writeObject(msg);
								output.flush();
							}
						} catch (SQLException e) {
							e.printStackTrace();
						}
					}
					if(msg.getCommand().equalsIgnoreCase("disconnect")){
						String d = msg.getCommand();
						System.out.println("From: " + remote + " Command: " + d + " FishStick: null");
						System.out.println(remote + " disconnected via request");
					}
				} catch (IOException exception) {
					System.err.println(exception.getMessage());
					exception.printStackTrace();
				}catch (ClassNotFoundException exception) {
					System.out.println(exception.getMessage());
					exception.printStackTrace();
				} 
				finally {
					try{
						if(input != null){
							input.close();
						}
					}catch(IOException ex){
						System.out.println(ex.getMessage());
						}
					try{
						if(output != null){
							output.flush(); 
							output.close();	
						}
					}catch(IOException ex){
						System.out.println(ex.getMessage());
					}
					try{
						if(connection != null){
							connection.close();
							}
					}catch(IOException ex){
						System.out.println(ex.getMessage());
					}
				}
			}
		});
		
	}
	/**
	 * runServer method will start the server connection
	 * @author Jennifer Aube
	 */
	public void runServer(){
		LocalDateTime dateTime = LocalDateTime.now();
		DateTimeFormatter format = DateTimeFormatter.ofPattern("MMM d yyyy hh:mm a");
		try {
			server = new ServerSocket(portNum);
		}catch (IOException e){
			e.printStackTrace();
		}
		System.out.println("FishStickServer by: Jennifer Aube run on " +  dateTime.format(format));
		System.out.println("Listenting for connections...");
		while(true){
			try{
				connection = server.accept();
				talkToClient(connection);
			} catch (IOException exception) {
				exception.printStackTrace();
			}
		}
		//new Thread(new WorkerRunnable(connection, "Multithreaded Server")).start();
	}
}
