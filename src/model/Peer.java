package model;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import action.PeerManager;
import action.TrackerControl;
import util.Bencoder2;
import util.BencodingException;

import model.Tracker;



public class Peer {
	
	public static final ByteBuffer KEY_PEERS = ByteBuffer.wrap(new byte[] {'p', 'e', 'e', 'r', 's' });
	public static final ByteBuffer KEY_PEERID = ByteBuffer.wrap(new byte[] {'p', 'e', 'e', 'r', ' ', 'i', 'd' });
	public static final ByteBuffer KEY_IP = ByteBuffer.wrap(new byte[] { 'i','p' });
	public static final ByteBuffer KEY_PORT = ByteBuffer.wrap(new byte[] { 'p','o', 'r', 't' });
	public static final byte[] MUST_NOT_PEER_ID = new byte[] { 'R', 'U', 'B', 'T' };
	
	
	
	protected URL trackerGETURL;
	protected ServerSocket listenSocket;
	protected byte[] trackerData;
	protected int peerPort;
	protected byte[] peerId;
	protected String peerIP;
	protected static ArrayList<Peer> peers;
	/** The in. */
	protected InputStream in;
	
	/** The out. */
	protected OutputStream out;
	
	protected static HashMap<ByteBuffer, Object> dataFromTracker = null;

	public Peer(byte[] peerId, int port, String ip) {
		//super("Peer@" + ip + ":" + port);
		this.peerId = peerId;
		this.peerPort = port;
		this.peerIP = ip;
	}
	
	/**
	 * Inits the peer
	 *
	 * @return true, if successful
	 */
	public boolean init() {
		
		//check to see if peer id doesn't start with RUBT
		byte[] id = new byte[4];
		System.arraycopy(this.peerId, 0, id, 0, 4);
		
		if ( !Arrays.equals(id, MUST_NOT_PEER_ID) ){ 
			return false;
		}
		
		
		try {
			//isRunning = true;
			//currentTime= System.currentTimeMillis();
			//this.connect();

			DataOutputStream os = new DataOutputStream(this.out);
			DataInputStream is = new DataInputStream(this.in);

			if (is == null || os == null) {
				//log.severe("Unable to create stream to peer");
				//this.disconnect();
				return false;
			}
			
			System.out.println("In try of init");
			os.write(PeerManager.generateHandshake(PeerManager.peerId, TrackerControl.torrentInfo.info_hash.array()));
			os.flush();

			byte[] response = new byte[68];
			
			
			//this.socket.setSoTimeout(10000);
			is.readFully(response);		
			//this.socket.setSoTimeout(130000);
			
			if(!PeerManager.checkHandshake(TrackerControl.torrentInfo.info_hash.array(), response)){
				return false;
			}
			System.out.println("ended init");
		/*	log.info("Handshake Response: " + Arrays.toString(response));

			if(this.manager.curUnchoked < this.manager.maxUnchoked){
				this.weAreChokingPeer = false;
				this.manager.curUnchoked++;
			} else {
				this.weAreChokingPeer = true;
			}
			
			this.start();
			*/
			//RUBTClient.log("Connected to " + this);
			
			return true;
		} catch (Exception e) {
			//log.severe("Error connecting with peer");
			return false;
		}
	}

	/**
	 * connect to each peer on the list (DIDNT GET TO THE ACTUAL CONNECTING PART...)
	 * 
	 * @param trackerData
	 */
	public static void connectToPeers(byte[] trackerData) {
		peers = new ArrayList<Peer>();
		peers = PeerManager.peelPeer(trackerData);
		
		for (Peer peer : peers){
			peer.init();
			
			
		}
	
	}
	
}
