package cs213.photoAlbum.model;

import java.util.HashMap;



/**
 * Backend will implement a 3 Hashmaps to store objects, the maps hash functions will be 
 * controlled by the preceeding object in the hierarchy User --> Album --> Photos. All memebrs
 * and functions are accessed via model link. Members are package private unless stated otherwise.
 * @author Joseph
 *
 */
public class Backend{
	

	private static HashMap<String,User> UserMap;
	private static HashMap<String,Album> AlbumMap;
	private static  HashMap<String,Photo> PhotoMap;
	
	/**
	 * This will allow access to the Hashmap via the modellink interface which will contain all the users of the application
	 * @return
	 */
	static HashMap<String,User> getUserMap() {
		return UserMap;
	}
	
	/**
	 * This will allow access to the Hashmap, via the modellink interface which will contain all the albums of the application
	 * @return
	 */
	static HashMap<String,Album> getAlbumMap() {
		return AlbumMap;
	}
	
	/**
	 * This will allow access to the Hashmap, via the modellink interface which will contain all the photo of the application
	 * @return
	 */
	static HashMap<String,Photo> getPhotoMap() {
		return PhotoMap;
	}

	


}
