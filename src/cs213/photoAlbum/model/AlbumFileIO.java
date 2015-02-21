package cs213.photoAlbum.model;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * This class is responsible for implementing the session persistence of the program, members function are
 * all accessed via model link or package members. Members are package private unless stated otherwise.
 * @author Joseph
 *
 */
public class AlbumFileIO implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private static ObjectInputStream in;
	private static ObjectOutputStream out;
	
	/**
	 * opens the input file for read when the program loads
	 * @param fileName
	 */
	void openInput(String fileName){
		
	}
	
	/**
	 * opens the input file for write when the program closes
	 * @param fileName
	 */
	void openOutput(String fileName){
		
	}
	
	/**
	 * write an album object to the file
	 * @param album
	 */
	void writeAlbum(Album album){
		//add new album(directory) 
	}
	
	/**
	 * write a user object to the file
	 * @param user
	 */
	void writeUser(User user){
		//allocate new storage for user
	}
	
	/**
	 * write a photo object to the file
	 * @param photo
	 */
	void writePhoto(Photo photo){
		
	}
	
	/**
	 * write a tag object to the file
	 * @param tag
	 */
	void writeTag(Tag tag){
		
	}
}	

