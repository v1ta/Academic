package cs213.photoAlbum.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * This class will store tag objects for photos. It is being 
 * Separated into a different class to avoid the issue of having 
 * duplicate tags for being used for different photos. TLDR to save
 * space. Member are package private unless stated otherwise 
 * @author Joseph
 *
 */
public class Tag implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private static ArrayList<String> locations;
	private static ArrayList<String> people;
	private static ArrayList<List<String>> miscTags;
	
	private ArrayList<Integer> location;
	private ArrayList<Integer> poeple;
	private ArrayList<Integer> miscTag;
	
	Tag(){}

	/**
	 * Provides access to the repository of location tags, via model link
	 * @return
	 */
	static ArrayList<String> getLocations() {
		return locations;
	}

	/**
	 * provides access to the repository of MiscTags, via model link
	 * @return
	 */
	static ArrayList<List<String>> getMiscTags() {
		return miscTags;
	}

	/**
	 * provides access to the indexing array for a tag object, via model link
	 * @return
	 */
	ArrayList<Integer> getPoeple() {
		return poeple;
	}

	/**
	 * Provides access to the people tag repository, via model link
	 * @return
	 */
	static ArrayList<String> getPeople() {
		return people;
	}

	/**
	 * provides access to the indexing element of tag object for location tags, via model link
	 * @return
	 */
	ArrayList<Integer> getLocation() {
		return location;
	}

	/**
	 * provides indexing to the tag object for misc tags via model link
	 * @return
	 */
	ArrayList<Integer> getMiscTag() {
		return miscTag;
	}

 }
