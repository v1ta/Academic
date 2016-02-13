package cs213.photoAlbum.model;

/**
 * Provides a means to access the members of model, These functions
 * will primarily interact with the Backend class. Data will be sorted via 
 * control, processed via control and then execute one of these functions to handle the data in 
 * the associated hashmap.
 * @author Joseph
 *
 */
public interface ModelLink {
	
	/**
	 * Will be called after control determines what to create, this function will have access to the static hash map in backend
	 * @param albumName
	 */
	public void createAlbum(String albumName);
	
	/**
	 * will be called after control determines what to delete, this function will have access to the static hash map in backend
	 * @param albumName
	 */
	public void destroyAlbum(String albumName);
	
	
	/**
	 * will be called after control determines what to create, this function will have access to the static hash map in backend
	 * @param personName
	 */
	public void tagPerson(String personName);
	
	/**
	 * will be called after control determines what to create, this function will have access to the static hash map in backend
	 * @param location
	 */
	public void tagLocation(String location);
	
	/**
	 * will be called after control determines what to create, this function will have access to the static hash map in backend
	 * @param misc
	 */
	public void tagMisc(String misc);
	
	/**
	 * will be called after control determines what to create, this function will have access to the static hash map in backend
	 * @param caption
	 */
	public void addCaption(String caption);
	
	/**
	 * will be called after control determines what to create, this function will have access to the static hash map in backend
	 * @param fileName
	 */
	public void addPhoto(String fileName);
	
	/**
	 * will be called after control determines what to delete, this function will have access to the static hash map in backend
	 * @param fileName
	 */
	public void deletePhoto(String fileName);
	
	/**
	 * will be called after control determines what to create, this function will have access to the static hash map in backend
	 * @param fileName
	 */
	public void setCaption(String fileName);
	
	/**
	 * will be called after control determines what to create, this function will have access to the static hash map in backend
	 * @param name
	 */
	public void addUser(String name);
	
	/**
	 * will be called after control determines what to destroy, this function will have access to the static hash map in backend
	 * @param UserID
	 */
	public void deleteUser(String UserID);
	
	/**
	 * Allows access to file logic via modellink
	 */
	public void fileIO();

}
