package control;
import java.awt.GridBagConstraints;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

import model.Album;
import model.Backend;
import model.Photo;
import model.PhotoThumbNail;
import model.Tag;
import model.User;
import util.ParseFilePath;
import util.TagToken;
import view.AdminLogin;
import view.CmdView;
import view.MainLogin;
import view.SearchScreen;
import view.UserAlbum;
import view.UserHome;


/**
 * Control implements a controlLink to manage any given data structure that stores the photoalbum's state.
 * Also stores certain information about the current user while they are logged in.
 * @author Joe
 *
 */
public class Control implements ControlLink{
	
	static Backend state;
	static User currentUser;
	static String previousWindow;
	static String inAlbum;
	private Image image;
	
	public Control(){
	}
	
	public User currentUser(){
		
		return currentUser;
	}
	
	public boolean login(String userID) {
		
			if(state.hasUser(userID)){
				
				currentUser = state.getUser(userID);
				return true; 
				
			}else{
				
				return false;
				
			}
		
	}
	
	public void addFrame(String frameName, JFrame window){
		
		CmdView.addScreen(frameName, window);
	}

	public void logout() {
		
		currentUser = null;
		state.savePhotoAlbum();
		
	}

	public boolean addNewUser(String userID, String userName) {
		
		if(state.addUser(userID, userName))
			return true;
		else
			return false;
	}

	public void deleteUser(String userID) {
		
		if(state.delUser(userID))
			System.out.println("");
		else
			System.out.println("");
		
	}
	
	
	//only need userIDs
	public ArrayList<User> listUsers() {
		
		return state.listUsers();
	}


	public void load() {
		state = new Backend();
		state.loadPhotoAlbum();
	}

	public boolean createAlbum(String albumName) {
		
		if(currentUser != null){
			if(!currentUser.checkForAblum(albumName)){
				
				currentUser.addAlbum(albumName);
				//System.out.println("created album for " + currentUser.toString() +":\n" + albumName);
				return true;
				
			}else{
				
				//System.out.println("album exists for " + currentUser.toString() +":\n" + albumName);
				return false;
			}
		}
		
		return false;
	}

	public void deleteAlbum(String albumName) {
		
		if(currentUser != null)
			if(currentUser.checkForAblum(albumName)){
				
				currentUser.deleteAblum(albumName);
				//System.out.println("deleted album for " + currentUser.toString() +":\n" + albumName);
				return;
				
			}else{
				//System.out.println("album does not exist for " + currentUser.toString() +":\n" + albumName);
			}
	}

	public void listAllAlbums(JPanel panel) {
		
		if(currentUser != null){
			
			if(currentUser.listAlbums().size() > 0){
				
				currentUser.loadAlbums(panel);
				
			}else{
				
				//System.out.println("No albums exist for user " + currentUser.toString());
				
			}
		}
	}

	public void listAllPhotos(String albumName, JPanel panel) {
		
		//ArrayList<Photo> photos = new ArrayList<Photo>();
		if(currentUser != null){
			
			if(currentUser.checkForAblum(albumName)){
				
				currentUser.hashAlbum(albumName).loadPhotos(panel);
				
			
				//System.out.println("Photos for album " + albumName + ":");
				
				//for(Photo photo : photos){
					//System.out.println(photo.toString() + " - " + photo.getDate().getTime().toString());
				//}
				return;
					
			}else{
			//	System.out.println("Album " + albumName + " does not exist");
				return;
			}
		}
	}

	public boolean addPhoto(String photoName, String albumName) {
		
		String filePath = photoName;
		
	     try {                
	         image = ImageIO.read(new File(photoName));
	       } catch (IOException ex) {
	    	   
	            UserAlbum.setMessage("ERROR\n*File not found*");
				return false;
	       } 
			
			
			
		
		
		ArrayList<String> userInput = ParseFilePath.strToPath(photoName);
		
		String path = "";
		
		for(String str : userInput)
			path += str;
		
		
		photoName = userInput.get(userInput.size() - 1);
		
		Photo photoToAdd = null;
		
		if(currentUser != null){
			
			//check if photo exists somewhere else to get correct caption
			for(Album album : currentUser.listAlbums()){
				
					if(album.hasPhoto(photoName)){
						photoToAdd = album.hashPhoto(photoName);
						break;
					
				}
			}
			
			/*
			//photo wasn't found
			if(photoToAdd == null && caption.equals("")){
				System.out.println("Error: File " + photoName + " does not exist");
				return false;
			}
			*/
			
			//default caption is the file name.
			if(photoToAdd == null){
				photoToAdd = new Photo(photoName, photoName);
				photoToAdd.setFilePath(filePath);
			}
			
				
			if(currentUser.checkForAblum(albumName)){
				
				if(!currentUser.hashAlbum(albumName).hasPhoto(photoName)){
					
					photoToAdd.addAlbumRef(albumName);
					currentUser.hashAlbum(albumName).addPhoto(photoToAdd);
				//	System.out.println("Added photo " + photoName + ":\n"  +  " - Album: " + albumName);
					return true;
					
				}else{
					
					UserAlbum.setMessage("ERROR\n*Photo already exists*");
				//	System.out.println("Photo " + photoName+ " already exists");
					return false;
				}
				
			}else{
				
				UserAlbum.setMessage("ERROR\n*Album does not exist*");
			//	System.out.println("Album " + albumName + " does not exist");
				return false;
			}
			

		}
		
		UserAlbum.setMessage("ERROR\n*User does not exist*");
		return false;
		
	}

	public boolean movePhoto(String photoName, String currentAlbum, String newAlbum) {
		
		Album source = null;
		Album dest = null;
		Photo toMove = null;
		
		if(currentUser != null){
			
			if(currentUser.checkForAblum(currentAlbum)){
				
				source = currentUser.hashAlbum(currentAlbum);
				
			}else{
				
			//	System.out.println("Album " + currentAlbum + " does not exist");
				return false;
			}
			
			if(currentUser.checkForAblum(newAlbum)){
				
				dest = currentUser.hashAlbum(newAlbum);
				
			}else{
				
				//System.out.println("Album " + newAlbum + " does not exist");
				return false;
			}
			
			
			
			if(source != null && dest != null){
				
				if(source.hasPhoto(photoName))
					toMove = source.hashPhoto(photoName);
				
				if(toMove != null && !dest.hasPhoto(photoName)){
					
					toMove.addAlbumRef(dest.toString());
					toMove.removeAlbumRef(source.toString());
					source.deletePhoto(photoName);
					dest.addPhoto(toMove);
					
				//	System.out.println("Moved Photo " + photoName + ":\n" + photoName + " - From album " + currentAlbum + " to album " + newAlbum);
					
					return true;
					
				}else{
					
					//System.out.println("Photo " + photoName + " already exists in " + newAlbum);
					return false;
				}
			}
			
			return false;
		}
		
		return false;
		
	}

	public void removePhoto(String photoName, String albumName) {
		
		if(currentUser != null){
			
			if(!currentUser.checkForAblum(albumName)){
				//System.out.println("Album " + albumName + " does not exist");
				return;
			}
				
				if(currentUser.hashAlbum(albumName).hasPhoto(photoName)){
					
					currentUser.hashAlbum(albumName).hashPhoto(photoName).removeAlbumRef(albumName);
					currentUser.hashAlbum(albumName).deletePhoto(photoName);
			//		System.out.println("Removed photo:\n" + photoName + " - From album " + albumName);
					return;
					
				}else{
					
					//System.out.println("Photo " + photoName + " is not in album " + albumName);
					return;
				}
			
		}
		
	}

	public boolean addTagToPhoto(String photoName, String tagType, String tagValue) {
		
		ArrayList<Photo> photos = new ArrayList<Photo>();
		if(currentUser != null){
			
			//a list might not be needed here, simply store reference so we only need one item to operate on...
			photos = currentUser.searchForPhotos(photoName);
			
			if(!photos.isEmpty()){
				
				//create new tag and store in tag user's tag repository, then reference to add to photos. 
				Tag toAdd = currentUser.addTag(tagType, tagValue);
				
				//photos only keep a copy of the key
					for(Photo photo: photos){
					
						//pass copy of new tag to photo for reference 
						if(photo.addTag(toAdd)){
							
							toAdd.addPhoto(photo);
							currentUser.addTagToMap(toAdd);
							//System.out.println("Added Tag:\n" + photoName + " " + toAdd.toString());
							return true;
							
						}else{
							
						//	System.out.println("Tag already exist for " + photoName + " " + toAdd.toString());
							return false;
						}
				}
			}
			
		//	System.out.println("Photo " + photoName + " does not exist");
			return false;
		}
		
		return false;
		
	}

	public boolean delTagPhoto(String photoName, String tagType, String tagValue) {
		
		Tag temp = new Tag(tagType, tagValue);
		
		ArrayList<Photo> photos = currentUser.searchForPhotos(photoName);
		
		if(photos.size() > 0){
		//photos = currentUser.searchForPhotos(photoName);
		
			for(Photo photo: photos){
				if(photo.hasTag(tagType, tagValue)){
					photo.deleteTag(temp.getTagData());
				//	System.out.println("Deleted Tag:\n" + photoName + " " + temp.toString());
					return true;
				}else{
					//System.out.println("Tag does not exist for " + photoName + " " + temp.toString());
					return false;
				}
			}
		}else{
			
		//	System.out.println("Photo " + photoName + "does not exist");
			return false;
		}
		
		return false;
		
	}

	public String listPhotoInfo(String photoName) {
		
		String output = "";
		ArrayList<Photo> photos = new ArrayList<Photo>();
		
		
			
			photos = currentUser.searchForPhotos(photoName);
			
			if(photos.size() == 0){
			//	System.out.println("Error: " + photoName + " does not exist");
			}
			for(Photo photo: photos){
				
				output += "\tPhoto Caption: " + photo.toString() + "\n"; // this needs formating to match the project specifications "
				
				output +="\tContained in albums: ";
				for(String album : photo.listAlbumRef()){
					output += album.toString() + " ";
				}
				
			  output += "\n\tDate: " + photo.getDate().getTime().toString() + "\n\tFilePath: " +photo.getFilePath() + "\n\tTags: ";
				
			  int count = 0;
				for(Tag tag : photo.listTags()){
					output += tag.toString() + "  ";
					count++;
					if(count == 3){
						
						count = 0;
						output += "\n";
					}
				}
			}
			
		return output;
		
	}

	public void getPhotosByDate(Calendar start, Calendar end, JPanel panel) {
		
		start.set(Calendar.MILLISECOND,0);
		end.set(Calendar.MILLISECOND,0);
		panel.removeAll();
		panel.invalidate();
	
		
		
		GridBagConstraints insert = new GridBagConstraints();
		insert.gridy = 0;
		insert.gridx = 0;
		insert.weightx = .24;
		insert.weighty = 1;
		
		insert.anchor = GridBagConstraints.NORTHWEST;
		
		
		ArrayList<Photo> photos = new ArrayList<Photo>();
		
		photos = currentUser.searchForDates(start, end);
		
		if(photos.size() == 0)
		{
			//System.out.println("No photos found btween those dates");
			return;
		}
			
			
		Collections.sort(photos);
		System.out.println("found " + photos.size());
		//clear duplicates
		HashSet<Photo> hs = new HashSet<Photo>();
		hs.addAll(photos);
		photos.clear();
		photos.addAll(hs);
		SearchScreen.potentialAlbum.clear();
		for(Photo photo : photos){
			
			panel.add(new PhotoThumbNail(photo.toString(), photo.getFilePath()));
			SearchScreen.potentialAlbum.put(photo.toString(), photo);
			insert.gridx++;
			
			if(insert.gridx == 4){
				
				insert.gridy++;
				insert.gridx = 0;
				
			}
		//	System.out.print(photo.getCaption() + " - Album: ");
			
			for(String album : photo.listAlbumRef()){
				System.out.print(album + ", ");
			}
			
			//System.out.print("- Date: " + photo.getDate().getTime().toString() + "\n");
		}
		panel.revalidate();
		panel.repaint();
	}
	

	public void getPhotosByTag(ArrayList<TagToken> tags, JPanel panel) {
		
		panel.removeAll();
		panel.invalidate();
		
		GridBagConstraints insert = new GridBagConstraints();
		insert.gridy = 0;
		insert.gridx = 0;
		insert.weightx = .24;
		insert.weighty = 1;
		
		insert.anchor = GridBagConstraints.NORTHWEST;
		
		ArrayList<Photo> photos = currentUser.searchForTags(tags);
		
		Collections.sort(photos);
		
		//System.out.println("Photos for user " + currentUser.toString() + " with tags " + ParseTagString.printSearchString(tags) + ":");
		
		//clear duplicates
		HashSet<Photo> hs = new HashSet<Photo>();
		hs.addAll(photos);
		photos.clear();
		photos.addAll(hs);
		SearchScreen.potentialAlbum.clear();
		for(Photo photo : photos){
			
			SearchScreen.potentialAlbum.put(photo.toString(), photo);
			
		panel.add(new PhotoThumbNail(photo.toString(), photo.getFilePath()));
			
			insert.gridx++;
			
			if(insert.gridx == 4){
				
				insert.gridy++;
				insert.gridx = 0;
				
			}
			
			//System.out.print(photo.toString() + " - Album: ");
			
			for(String album : photo.listAlbumRef()){
			//	System.out.print(album + ", ");
			}
		//	System.out.print("- Date: " + photo.getDate().getTime().toString() + "\n");
		}
		
		panel.revalidate();
		panel.repaint();
	}

	/**
	 * functionality to swtich screens
	 */
	public void switchScreens(String source, String destination) {
	
		previousWindow = source;
		CmdView.addScreen(destination, getFactory(destination));
		CmdView.removeScreen(source);
		

	}
	
	/**
	 * reference to album in use
	 * @return
	 */
	public String getCurrentAlbum(){
		
		return inAlbum;
		
	}
	/**
	 * return logged in user
	 * @param userID
	 * @return
	 */
	public User getUser(String userID){
		
		return state.getUser(userID);
	}
	
	/**
	 * store previous frame for back button
	 * @return
	 */
	public static String getPrevious(){
		
		return previousWindow;
	}
	
	/**
	 * factory function for new frames
	 * @param frameName
	 * @return
	 */
	public JFrame getFactory(String frameName){
		
		
		if(frameName.equals("UserHome")){
			
			return UserHome.factory();
			
		}else if(frameName.equals("MainLogin")){
			
			return MainLogin.factory();
			
		}else if(frameName.equals("AdminLogin")){
			
			return AdminLogin.factory();
			
		}else if(frameName.equals("UserAlbum")){
			
			return UserAlbum.factory();
			
		}else if(frameName.equals("SearchScreen")){
			
			return SearchScreen.factory();
			
		}else{
			
			return null;
			
		}
		
		
		
	}
	
	/**
	 * store which album the user is in
	 * @param album
	 */
	public void setCurrentAlbum(String album){
		
		inAlbum = album;
	}





}