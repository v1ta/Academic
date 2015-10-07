package model;

import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import util.Bencoder2;
import util.BencodingException;

public class Peer {
	protected static URL trackerGETURL;
	protected static ServerSocket listenSocket;
	protected static byte[] trackerData;
	protected static int peerPort;
	protected static byte[] peerId;
	protected static String peerIP;
	
	protected static HashMap<ByteBuffer, Object> dataFromTracker = null;

	public static void connectToPeers() {
		try {
			dataFromTracker = (HashMap<ByteBuffer, Object>) Bencoder2.decode(Tracker.getTrackerData());
		} catch (BencodingException e) {
		}
		
	
		AbstractList<Map<ByteBuffer, Object>> peersList = (AbstractList<Map<ByteBuffer, Object>>) dataFromTracker.get(KEY_PEERS);
	
		for (Map<ByteBuffer, Object> rawPeer : peersList) {
			peerPort = ((Integer) rawPeer.get(KEY_PORT)).intValue();
			peerId = ((ByteBuffer) rawPeer.get(KEY_PEERID)).array();
			peerIP = null;
			try {
				peerIP = new String(((ByteBuffer) rawPeer.get(KEY_IP)).array(),"ASCII");
				System.out.println(peerIP);
			} catch (UnsupportedEncodingException e) {

			}

		}
	
	}
	
}
