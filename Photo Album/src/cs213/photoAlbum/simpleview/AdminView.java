package cs213.photoAlbum.simpleview;

import cs213.photoAlbum.control.ControlLink;

/**
 * Provides administrator functions to the program,
 * member functions also provide format error checking.
 * @author Tyler
 *
 */
public class AdminView implements ControlLink{
	
	@SuppressWarnings("unused")
	private String input;
	
	/**
	 * checks for non-program related format checking and then stores the string.
	 */
	static void getInput(){
		//get admin input
	}
	
	/** 
	 * List the the users of photoAlbum30 
	 */
	static void listUsers(){
		
	}
	
	/**
	 * Prompt for new users
	 * "adduser <user id> "<user name>""
	 */
	static void promptForNewUser(){
		
	}
	
	/**
	 * prompt to delete a user 
	 * "deleteuser <user id>
	 */
	static void promptDelUser(){
		
	}
	
	/**
	 * login as existing user
	 * "login <user id>"
	 */
	static void promptToLogin(){
		
	}

	@Override
	public void sendUserInput(String input) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void checkUserInput(String input) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void terminateprogram() {
		// TODO Auto-generated method stub
		
	}
}
