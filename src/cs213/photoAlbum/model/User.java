package cs213.photoAlbum.model;

import java.io.Serializable;


/**
 * Object to represent an individual user, members are package private unless stated otherwise.
 * @author Tyler
 *
 */
public class User implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private String name; 
	private String UserID; //each user will have a directory based on his UserID
	

	
	User(String name, String UserID){
		this.name = name;
		this.UserID = UserID;
		//auto generate unique ID
	}
	
	/**
	 * provides access to a User's name for modellink
	 * @return
	 */
	String getName() {
		return name;
	}


	/**
	 * get userID provides access to user's unique ID, in order to facilitate list all for admin mode
	 * @return
	 */
	public String getUserID() {
		return UserID;
	}


	

	

}
