import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.ServerSocket;

/**
 * Multi-threaded echo server
 * @author Joseph
 *
 */
public class EchoServer implements Runnable{
	
	protected Socket socket;
	
	/**
	 * Accepts an initialized socket(client) object
	 * @param client
	 * @throws Exception
	 */
	public EchoServer(Socket client) throws Exception{
		socket = client;
	}
	
    public static void main(String[] args) throws Exception {
    	
    	/*
    	 * In case the user was dropped as a baby
    	 */
		if (args.length != 1) {
			System.out.println("SUPPLY A PORT NUMBER KTHX");
			System.exit(1);
		}
		
		/*
		 * The server will listen on this port for clients requesting connection(s)
		 */
		ServerSocket socket = new ServerSocket(Integer.parseInt(args[0]));
		
		
        while (true) {
        	/*
        	 * 1) Blocking call until new client connections
        	 * 2) Create new EchoServer object, this class implements runnable
        	 * 3) Create an interface type, launch new thread for client session
        	 */
        	Socket client = socket.accept();
        	Runnable echo = new EchoServer(client);
        	new Thread(echo).start();
        }
    }

	/**
	 * launch a new Thread for each client session
	 */
	@Override
	public void run() {
		PrintWriter out = null; //data to client
		BufferedReader in = null; //data from client
		
		try{
			out = new PrintWriter(socket.getOutputStream(), true); //assign socket output stream
			in = new BufferedReader(new InputStreamReader(socket.getInputStream())); //assign socket input stream
		} catch (IOException e) {
			System.out.println(e.getMessage());
			return;
		}
		String input; //local var for outputing to std in
		while(true) {
			try {
				input = in.readLine();
				if (!checkForTerminator(input)){ //check for termination conditions 
					out.println(new StringBuilder(input).reverse().toString()); //reverse input and send to client
					out.flush(); //clear stream
				} else {
					socket.close(); //end client session
					return;
				}
			} catch (IOException e) {
				System.out.println(e.getMessage());
				return;
			}
			
		}
	}
	
	/**
	 * Check for termination conditions [null, '#', '$']
	 * @param input
	 * @return
	 */
	public boolean checkForTerminator(String input){
		return (input == null) || input.equalsIgnoreCase("#") || input.equalsIgnoreCase("$");
	}
}