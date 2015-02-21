package cs213.photoAlbum.model;

import java.io.Serializable;


/**
 * Object to represent an album and associated data.
 * Members are package private unless stated otherwise.
 * @author Tyler
 *
 */
public class Album implements Serializable{

	private static final long serialVersionUID = 1L;
	private String albumName;
	
	/**
	 * default constructor, does not require any photos.
	 * @param name
	 */
	Album(String name){
	}

	/**
	 * access the name of an Album, this can be used via modellink interface or for the 
	 * view if there is a list current files command is receieved 
	 * @return
	 */
	public String getName() {
		return albumName;
	}

}
