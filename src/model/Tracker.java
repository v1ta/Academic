package model;

import java.net.ServerSocket;
import java.net.URL;
import java.nio.ByteBuffer;

import util.TorrentInfo;

/**
 * Stores persistent data associated with the tracker
 * @author Joseph
 *
 */
public class Tracker {
	protected static URL trackerGETURL;
	protected static ServerSocket listenSocket;
	protected static byte[] trackerData;
	
	/*
	 * I moved these from Term.java cause I thought it made more sense, 
	 * but you can move it to any place if you find a better one
	 */
	public static final ByteBuffer KEY_PEERS = ByteBuffer.wrap(new byte[] {'p', 'e', 'e', 'r', 's' });
	public static final ByteBuffer KEY_PEERID = ByteBuffer.wrap(new byte[] {'p', 'e', 'e', 'r', ' ', 'i', 'd' });
	public static final ByteBuffer KEY_IP = ByteBuffer.wrap(new byte[] { 'i','p' });
	public static final ByteBuffer KEY_PORT = ByteBuffer.wrap(new byte[] { 'p','o', 'r', 't' });
	

	public static byte[] getTrackerData() {
		return trackerData;
	}

	public static void setTrackerData(byte[] trackerData) {
		Tracker.trackerData = trackerData;
	}

	public static ServerSocket getListenSocket() {
		return listenSocket;
	}

	public static void setListenSocket(ServerSocket listenSocket) {
		Tracker.listenSocket = listenSocket;
	}

	public static URL getTrackerGETURL() {
		return trackerGETURL;
	}

	public static void setTrackerGETURL(URL trackerGETURL) {
		Tracker.trackerGETURL = trackerGETURL;
	}
}
