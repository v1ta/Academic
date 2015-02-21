package cs213.photoAlbum.model;

import java.io.Serializable;


/**
 * Object to represent an individual photo, will store associated captions, access dates, user tags, etc...
 * Members are package private unless stated otherwise.
 * @author Tyler
 *
 */
public class Photo implements Serializable{

	private static final long serialVersionUID = 1L;
	private String fileName;
	private String accessDate;
	private String caption;
	private Tag tag;

	
	/**
	 * Default constructor, all photos must have a filename.
	 * @param fileName
	 */
	Photo(String fileName){
		this.fileName = fileName;
	}

	/**
	 * returns the caption of a photo object 
	 * @return
	 */
	public String getCaption() {
		return caption;
	}

	/**
	 * returns the access date of a photo object
	 * @return
	 */
	public String getAccessDate() {
		return accessDate;
	}

	/**
	 * returns the name of a photo object
	 * @return
	 */
	public String getFileName() {
		return fileName;
	}
	
	/**
	 * returns the tag object assoicated wit a photo object.
	 * @return
	 */
	Tag getTag() {
		return tag;
	}
	

}
