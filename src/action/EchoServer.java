package action;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Multi-threaded echo server
 * @author Joseph
 *
 */
public class EchoServer implements Runnable{
	
	public static boolean server = true;
	public static ServerSocket serverSocket;
	public static int port;
	protected static List<Thread> clients; // TODO use ScheduledThreadPoolExecutor instead 
	protected Scanner in = new Scanner(System.in);
	protected Socket socket;
	
	/**
	 * Accepts an initialized socket(client) object
	 * @param client
	 * @throws Exception
	 */
	public EchoServer(Socket client) throws Exception{
		socket = client;
	}
	
	public static void main(String[] args) {
    	Scanner in = new Scanner(System.in);
    	/*
    	 * In case the user was dropped as a baby
    	 */
		if (args.length != 1) {
			System.err.println("PROGRAM USAGE: java EchoServer <port>");
			System.exit(1);
		}else{
			port = Integer.parseInt(args[0]);
		}
		
		/*
		 * The server will listen on [port] for client(s) requesting a connection
		 */
		try {
			serverSocket = new ServerSocket(port);
		} catch (NumberFormatException | IOException e) {
			System.err.println("ERROR: Could not bind port: " + port + "\nException: " + e.getMessage());
		}
			clients = new ArrayList<Thread>();
			System.out.println("Enter \"quit\" to termiante the server");
			Runnable check = new serverCheck(in);
			new Thread(check).start();
			
	        while (server) {
	        	try{
		        	Socket client = serverSocket.accept(); 
		        	Runnable echo = new EchoServer(client);
		        	clients.add(new Thread(echo));
		        	clients.get(clients.size()-1).start();
	        	}catch(IOException e){
	        		System.err.println("ERROR: Failed to bind incoming connection\nException: " + e.getMessage());
	        	} catch (Exception e) {
					System.err.println("ERROR: Failed to launch client thread\nException: " + e.getMessage());
				}
	        }
	        try {
				serverSocket.close();
			} catch (IOException e) {
				System.err.println("ERROR: Failed to close server socket\nException: " + e.getMessage());
			}
	        System.exit(1); // TODO the blocking input stream call in a client thread is the only reason this is needed, should find a way around
		}
    

	/**
	 * launch a new Thread for each client session
	 */
	@Override
	public void run() {
		PrintWriter out = null; //data to client
		BufferedReader in = null; //data from client
		
		try{
			out = new PrintWriter(socket.getOutputStream(), true); 
			in = new BufferedReader(new InputStreamReader(socket.getInputStream())); 
		} catch (IOException e) {
			System.err.println("ERROR: Failed to open client IO streams\nException: " + e.getMessage());
			try {
				socket.close();
			} catch (IOException e1) {
				System.err.println("ERROR: Failed to close client socket\nException: " + e.getMessage());
			}
			return;
		}
		
		String input; 
		while(server) {
			try {
				input = in.readLine();
				if (!checkForTerminator(input)){ 
					out.println(new StringBuilder(input).reverse().toString()); 
					out.flush(); 
				} else {
					socket.close();
					return;
				}
			} catch (IOException e) {
				try {
					socket.close();
				} catch (IOException e1) {
					System.err.println("ERROR: Failed to close client socket\nException: " + e.getMessage());
				} 		
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

	static class serverCheck implements Runnable{
		private Scanner in;
		private String input;
		
		serverCheck(Scanner in){
			this.in = in;
		}

		@Override
		public void run() {
			Socket closeServer = null;
			while(server){
				input = in.nextLine();
				if (input.equalsIgnoreCase("quit")) {
					in.close();
					server = false;
					try {
						closeServer = new Socket(serverSocket.getInetAddress(), port);
						closeServer.close();
					} catch (IOException e) {
						System.err.println("ERROR: Failed to signal server termination\nException: " + e.getMessage());
					}
				}
			}
		}
	}
}

