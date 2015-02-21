package cs213.photoAlbum.control;

/**
 * Provides a means to communicate to control from other elements in the MVC
 * @author Joseph
 *
 */
public interface ControlLink {
	
	/**
	 * This function will receive input from view.
	 * It will use the arguments passed via view which will
	 * be formatted into a single stream, it will when choose
	 * the appropriate functions based on the input.
	 * @param input
	 */
	public void sendUserInput(String input);
	
	/**
	 * This function is responsible for checking input in the event that it 
	 * passes the error checking in view, but still isn't valid input.
	 * @param input
	 */
	public void checkUserInput(String input);
	
	/**
	 * This will call appropriate functions that are needed to execute after a call to end the program
	 * has been received.
	 */
	public void terminateprogram();

}
