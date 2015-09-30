package action;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.URL;
import java.nio.file.Files;

import util.BencodingException;
import util.TorrentInfo;
import util.Util;

import model.Tracker;

/**
 * Communicates with the trackers via references from the decoded torrent file
 * @author Joseph
 *
 */
public class TrackerControl {
	
	public TorrentInfo torrentInfo = null;
	
	/**
	 * Writes a torrent file to byte[] and initializes the Tracker URL
	 * TODO, the file needs to be passed via commandline
	 */
	public TrackerControl(){
		try {
			torrentInfo = new TorrentInfo(Files.readAllBytes(new File(System.getProperty("user.dir") 
					+ File.separator 
					+ "data" 
					+ File.separator 
					+ "Phase1.torrent").toPath()));
		} catch (BencodingException | IOException e) {
			System.err.println("ERROR: Failed to instantiate torrentInfo\nException: "
					+ e.getMessage());
		}
		Tracker.setTrackerGETURL(initURL(torrentInfo));
	}
	
	/**
	 * Sets a listening ports between the std. torrent ports [6881 - 6889]
	 * @return
	 */
	public static int pickPort() {
		for (int i = 6881; i <= 6889; i++) {
			try {
				Tracker.setListenSocket(new ServerSocket(i));
				return i;
			} catch (IOException e) {
				System.err.println("ERROR: Failed to bind port: " + i +" \nException: "
						+ e.getMessage());
			}
		}
		System.err.println("ERROR: No ports available");
		return -1;
	}
	
	/**
	 * Sends a HTTP GET code to the tracker with the info hash keys decoded from the torrent file
	 * @return
	 */
	public byte[] trackerGET() {
		try {
			HttpURLConnection httpConnection = (HttpURLConnection)Tracker.getTrackerGETURL().openConnection();
			DataInputStream dataInputStream = new DataInputStream(httpConnection.getInputStream());
			
			byte[] retArr = new byte[httpConnection.getContentLength()];

			dataInputStream.readFully(retArr);
			dataInputStream.close();

			return retArr;
		} catch (IOException e) {
			System.err.println("ERROR: Failed to communicate with tracker\nException: "
					+ e.getMessage());
			return null;
		}
	}
	
	/**
	 * Formats a URL with decoded key values from a torrent file
	 * @param torrentInfo
	 * @return
	 */
	public static URL initURL(TorrentInfo torrentInfo){
		StringBuilder URL = new StringBuilder();
		URL.append(torrentInfo.announce_url.toString());
		URL.append("?info_hash=");
		URL.append(Util.toHexString(torrentInfo.info_hash.array()));
		URL.append("&peer_id=");
		URL.append(Util.toHexString(PeerManager.genPeerId()));
		URL.append("&port=");
		URL.append(Integer.toString(pickPort()));
		URL.append("&uploaded=");
		URL.append("0");
		URL.append("&downloaded=");
		URL.append("0");
		URL.append("&left=");
		URL.append(torrentInfo.file_length);

		try {
			return new URL(URL.toString());
		} catch (MalformedURLException e) {
			System.err.println("ERROR: Failed to generated Tracker GET code\nException: "
					+ e.getMessage());
			return null;
		}
		
	}
}
