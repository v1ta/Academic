package cs213.photoAlbum.control;

import cs213.photoAlbum.model.ModelLink;

/**
 * Responsible for receiving user input, processing the data, and correctly storing it in hashmap.
 * @author Joseph
 *
 */
public class Control implements ModelLink{
	
	/**
	 * Will determine where in the hashmap to insert/remove 
	 * a new user.
	 */
	void userHashed(){};

	/**
	 * Will determine where to insert an album in the hashmap/remove.
	 */
	void albumHashed(){};
	
	/**
	 * Will determine where to insert a photo in the hashmap/remove
	 */
	void photoHashed(){};
	
	@Override
	public void createAlbum(String albumName) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void destroyAlbum(String albumName) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void tagPerson(String personName) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void tagLocation(String location) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addCaption(String caption) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void tagMisc(String misc) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addPhoto(String fileName) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deletePhoto(String fileName) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setCaption(String fileName) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addUser(String name) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteUser(String UserID) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void fileIO() {
		// TODO Auto-generated method stub
		
	}



		
}
