package action;

import java.util.Random;

/**
 * Class to handle peer logic
 * @author Joseph
 *
 */
public class PeerManager {
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
	/*
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
		System.arraycopy(zero, 0, handshake, index, zero.length);
		index += zero.length;
		
		log.fine("Info hash length: " + infohash.length);
		System.arraycopy(infohash, 0, handshake, index, infohash.length);
		index += infohash.length;
		
		System.arraycopy(peer, 0, handshake, index, peer.length);
		log.fine("Peer ID Length: " + peer.length);
		
		return handshake;
	}
	*/
}
