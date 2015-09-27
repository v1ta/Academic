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
}
