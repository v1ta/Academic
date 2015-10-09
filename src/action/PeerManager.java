package action;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import model.Tracker;
import model.Peer;
import util.Bencoder2;
import util.BencodingException;
import util.ToolKit;

/**
 * Class to handle peer logic
 * @author Joseph
 *
 */
public class PeerManager {
	
	
	public static byte[] peerId = genPeerId();
	
	/**
	 * Generates somewhat random peer Id
	 * @return
	 */
	public static byte[] genPeerId(){
		Random rand = new Random(System.currentTimeMillis());
		byte[] peerId = new byte[20];

		peerId[0] = 'K';
		peerId[1] = 'K';
		peerId[2] = '0';
		peerId[3] = '1';

		for (int i = 4; i < 20; i++) {
			peerId[i] = (byte) ('A' + rand.nextInt(26));
		}
		return peerId;
	}
	
	/**
	 * takes tracker data and peel off peer id, port and ip
	 *  
	 * @param trackerData
	 * @return list of Peer
	 */
	public static ArrayList<Peer> peelPeer(byte[] trackerData){
		
		ArrayList<Peer> peers = new ArrayList<Peer>();
		HashMap<ByteBuffer, Object> ableToRead = null;
		int peerPort;
		byte[] peerId;
		String peerIP;
		Peer peer;
		
		try {
			ableToRead = (HashMap<ByteBuffer, Object>) Bencoder2.decode(trackerData);
		} catch (BencodingException e) {
		}
		
		//ToolKit.printMap(ableToRead,1);
		
		AbstractList<Map<ByteBuffer, Object>> peersList = (AbstractList<Map<ByteBuffer, Object>>) ableToRead.get(Peer.KEY_PEERS);
		
		//ToolKit.printList(peersList, 1);
		
		for (Map<ByteBuffer, Object> rawPeer : peersList) {
			peerPort = ((Integer) rawPeer.get(Peer.KEY_PORT)).intValue();
			peerId = ((ByteBuffer) rawPeer.get(Peer.KEY_PEERID)).array();
			peerIP = null;
			try {
				peerIP = new String(((ByteBuffer) rawPeer.get(Peer.KEY_IP)).array(),"ASCII");
				System.out.println(peerIP);
				peer = new Peer(peerId, peerPort, peerIP);
				peers.add(peer);
				
			} catch (UnsupportedEncodingException e) {

			}

		}
		
		return peers;
	}
	
	/**
	 * Generate handshake.
	 * 
	 * Generate a handshake on our end to send to the peer
	 *
	 * @param peer the peer
	 * @param infohash the infohash
	 * @return the byte[]
	 */
	public static byte[] generateHandshake(byte[] peer, byte[] infohash) {
		int index = 0;
		byte[] handshake = new byte[68];
		
		handshake[index] = 0x13;
		index++;
		
		byte[] BTChars = { 'B', 'i', 't', 'T', 'o', 'r', 'r', 'e', 'n', 't', ' ',
				'p', 'r', 'o', 't', 'o', 'c', 'o', 'l' };
		System.arraycopy(BTChars, 0, handshake, index, BTChars.length);
		index += BTChars.length;
		
		byte[] zero = new byte[8];
		for(int i = 0; i < zero.length; i++){
			zero[i] = 0;
		}
		System.arraycopy(zero, 0, handshake, index, zero.length);
		index += zero.length;
		
		//log.fine("Info hash length: " + infohash.length);
		System.arraycopy(infohash, 0, handshake, index, infohash.length);
		index += infohash.length;
		
		System.arraycopy(peer, 0, handshake, index, peer.length);
		//log.fine("Peer ID Length: " + peer.length);
		
		System.out.println("handshake : " + handshake);
		return handshake;
	}
	
	public static boolean checkHandshake(byte[] infoHash, byte[] response) {
		byte[] peerHash = new byte[20];
		System.arraycopy(response, 28, peerHash, 0, 20);

		if(!Arrays.equals(peerHash, infoHash))
		{
			//log.info("Handshake verification failed with Peer: " + peerId);
			System.out.println("handshake failed");
			return false;
		}
			//log.info("Verified Handshake.");
		return true;
	}
}
