package client;
/* File: FishStickClient.java
 * Author: Jennifer Aube, based on starter code from Stanley Pieda
 * Modified Date: February 2018
 * Description: Networking client that uses simple protocol to send and receive transfer objects.
 */
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.UUID;

import datatransfer.FishStick;
import datatransfer.Message;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * FishStickClient class will connect with server, will take user input and insert into fishstick object then send message to server to insert into database
 * @author Jennifer Aube
 */
public class FishStickClient {


	private Socket connection;
	private ObjectOutputStream output;
	private ObjectInputStream input;	
	private String serverName = "localhost";
	private int portNum = 8081;
	BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

	Message msg = null;
	FishStick fishstick;
	private String message = "";
	private String recordNum = "";
	private String omega = "";
	private String lambda = "";
	String endReply = "";
	boolean anotherFishStick;
	/**
	 * main point of execution for program
	 * @param args
	 * @author Jennifer Aube
	 */
	public static void main(String[] args) {
		switch (args.length){
		case 2:
			(new FishStickClient(args[1],Integer.parseInt(args[2]))).runClient();
			break;
		case 1:
			(new FishStickClient("localhost",Integer.parseInt(args[1]))).runClient();
			break;
		default:
			(new FishStickClient("localhost",8081)).runClient();
		}
	}
	/**
	 * constructor that will instantiate the server name and port number
	 * @param serverName
	 * @param portNum
	 * @author Jennifer Aube
	 */
	public FishStickClient(String serverName, int portNum){
		this.serverName = serverName;
		this.portNum = portNum;
	}
	/**
	 * addingFishStick method will print out and ask for user input and what to put into fishstick object
	 * @throws IOException
	 * @author Jennifer Aube
	 */
	public void addingFishStick() throws IOException{
		System.out.println("Enter data for new FishStick:");
		System.out.print("Please enter record number: ");
		recordNum = br.readLine();
		System.out.print("Please enter omega: ");
		omega = br.readLine();
		System.out.print("Please enter lambda: ");
		lambda = br.readLine();
	}
	/**
	 * openAll method will open all connections for socket, objectoutputstream and objetinputstream
	 */
	public void openAll(){
		try {
			connection = new Socket(InetAddress.getByName(serverName), portNum);
			output = new ObjectOutputStream (connection.getOutputStream());
			input = new ObjectInputStream( connection.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}  
	}
	/**
	 * runClient method will connect with server, get user input, pass message to server, and close connections when done
	 * @author Jennifer Aube
	 */
	public void runClient(){
		String myHostName = null;
		LocalDateTime dateTime = LocalDateTime.now();
		DateTimeFormatter format = DateTimeFormatter.ofPattern("MMM d yyyy hh:mm a");
		try {
			InetAddress myHost = Inet4Address.getLocalHost();
			myHostName = myHost.getHostName();
			System.out.println("FishStickClient by: Jennifer Aube run on " +  dateTime.format(format));
			openAll();

			addingFishStick(); //ask questions
			insertFishStick();	//insert into fishstick object and send to server	
			
			insertAnotherFishStick();
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		finally{
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
				if(connection != null){//if(!connection.isClosed()){
					connection.close();
				}
			}catch(IOException ex){
				System.out.println(ex.getMessage());
			}
		}
	}
	/**
	 * closeAll method will close all connections for socket, inputstream and outputstream
	 * @throws IOException
	 */
	public void closeAll() throws IOException{
		output.close();
		input.close();
		connection.close();
	}
	/**
	 * insertFishStick method will insert user input into fishstick object and send in message object to server
	 * @throws IOException
	 * @throws ClassNotFoundException 
	 */
	public void insertFishStick() throws IOException, ClassNotFoundException{
		fishstick = new FishStick();
		fishstick.setRecordNumber(Integer.parseInt(recordNum));
		fishstick.setOmega(omega);
		fishstick.setLambda(lambda);

		//generate UUID for fishstick is cited from https://kodejava.org/how-do-i-generate-uuid-guid-in-java/
		UUID uuid = UUID.randomUUID();
		String generateUUID = uuid.toString();
		fishstick.setUUID(generateUUID);

		msg = new Message("add", fishstick);		
		output.writeObject(msg);
		output.flush();

		msg = (Message)input.readObject(); //read msg object from server
		if(msg.getCommand().equals("command_failed")){
			System.out.println("Server failed to perform requested operation\nShutting down connection to server");
			closeAll();
		}
		if(msg.getCommand().equals("command_worked")){
			fishstick = msg.getFishStick();
			String s = "Command: success Returned FishStick: " + fishstick.getId() + ", " + fishstick.getRecordNumber() + ", " + fishstick.getOmega() 
			+ ", " + fishstick.getLambda() + ", " + fishstick.getUUID();
			System.out.println(s);
		}
		closeAll();
	}
	/**
	 * insertAnotherFishStick method will ask the user if they wish to insert another fishtick object into database or exit the program
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public void insertAnotherFishStick() throws IOException, ClassNotFoundException{		
		System.out.println("Do you want to insert another fish stick?(y or n):");				
		endReply = br.readLine();
		while(!endReply.equalsIgnoreCase("n")){
			openAll();
			addingFishStick();
			insertFishStick();	
			System.out.println("Do you want to insert another fish stick?(y or n):");
			endReply = br.readLine();
		}
			anotherFishStick = false;
			openAll();
			msg = new Message("disconnect");
			output.writeObject(msg);
			output.flush();
			System.out.println("Shutting down connection to server");
			closeAll();
		}

	

}



