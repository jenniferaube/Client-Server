package datatransfer;
/* File: Message.java
 * Author: Jennifer Aube
 * Modified Date: February 2018
 * Description: Used to store a string and fishstick object to pass between client and server for communication
 */
import java.io.Serializable;

/**
 * Message class used to store a string and fishstick object for communication between client and server
 * @author Jennifer Aube
 */
public class Message implements Serializable{

	long serialVersionUID;
	String command;
	FishStick fishstick;
	/**
	 * constructor that will instantiate a string command
	 * @param command
	 * @author Jennifer Aube
	 */
	public Message(String command){
		this.command = command;
	}
	/**
	 * constructor that will instantiate a string command and fishstick object
	 * @param command
	 * @param fishstick
	 * @author Jennifer Aube
	 */
	public Message(String command, FishStick fishstick){
		this.command = command;
		this.fishstick = fishstick;
	}
	/**
	 * getCommand method will return the string command
	 * @return
	 * @author Jennifer Aube
	 */
	public String getCommand(){
		return command;
	}
	/**
	 * setCommand method will set the string command
	 * @param command
	 * @author Jennifer Aube
	 */
	public void setCommand(String command){
		command = command;
	}
	/**
	 * getFishStick method will return a fishstick
	 * @return
	 * @author Jennifer Aube
	 */
	public FishStick getFishStick(){
		return fishstick;
	}
	/**
	 * setFishStick method will set a fishstick
	 * @param fishstick
	 * @author Jennifer Aube
	 */
	public void setFishtick(FishStick fishstick){
		fishstick = fishstick;
	}
	
}
