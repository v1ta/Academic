package cs213.photoAlbum.simpleview;

import cs213.photoAlbum.control.ControlLink;

/**
 * User-frontend. 
 * format error-checking is also handled via the appropriate member functions. 
 * @author Tyler
 *
 */
public class CmdView implements ControlLink{
	
	String input;
	/**
	 * prompt from user for album creation
	 * create "<album>"
	 * @return 
	 */
	static void promptCreateAlbum(){};
	
	/**
	 * prompt from user to delete an album
	 * deleteAlbum "<name>"
	 */
	static void promptDeleteAlbum(){};
	
	/**
	 * list all albums
	 * listAlbums
	 */
	static void listAllAlbums(){};
	
	/**
	 * list all photos in an album
	 * listPhotos "<name>"
	 */
	static void listAllPhotos(){};
	
	
	/**
	 * prompt for adding a photo
	 * addPhoto "<fileName>" "<caption>" "<albumName>"
	 */
	static void promptAddPhoto(){};
	
	/**
	 * prompt to move a photo to a different album
	 * movePhoto "<fileName>" "<oldAlbumName>" "<newAlbumName>"
	 */
	static void promptMove(){};
	
	/**
	 * prompt to move delete a photo from an album
	 * removePhoto "<filename>" "<albumname>"
	 */
	static void promptRemovePhoto(){};
	
	
	/**
	 * add a tag to a photo
	 * addTag "<fileName>" <tagType>:"<tagValue>"
	 */
	static void promptAddTagToPhoto(){};
	
	/**
	 * delete a tag from a photo
	 * deleteTag"<fileName>" <tagType>:"<tagValue>"
	 */
	static void promptDelTagPhoto(){};
	
	/**
	 * list associated info form a photo
	 * listPhotoInfo "<fileName>"
	 */
	static void promptListPhotoInfo(){};
	
	/**
	 * display photos within a time range, inclusive
	 * getPhotosByDate <start date> <end date>
	 */
	static void promptGetPhotosByDate(){};
	
	/**
	 * display photos by tqg
	 * getPhotosByTag [<tag type>:]"<tagValue>"[,[<tagType>:]"<tagValue>]...
	 */
	static void promptGetPhotosByTag(){};
	
	/**
	 * end session
	 */
	static void logout(){}

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
		
	};
	
}
