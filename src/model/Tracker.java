package model;

import java.net.ServerSocket;
import java.net.URL;

/**
 * Stores persistent data associated with the tracker
 * @author Joseph
 *
 */
public class Tracker {
	protected static URL trackerGETURL;
	protected static ServerSocket listenSocket;
	protected static byte[] trackerData;

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
