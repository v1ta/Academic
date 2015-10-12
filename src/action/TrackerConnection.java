package action;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.Peer;
import model.TorrentManager;
import util.Bencoder2;
import util.BencodingException;
import util.HashConstants;
import util.HashSequenceHelper;
import util.TorrentInfo;

/**
 * Methods which facilitate communication with a Tracker
 */
public interface TrackerConnection {

	/**
	 * Sets a listening ports between the std. default: [6881 - 6889]
	 * @param lowerBound
	 * @param upperBound
	 * @return
	 */
	int setListeningPort(int lowerBound, int upperBound);

	/**
	 * Send HTTP GET code to the tracker with the info hash keys from decoded torrent file
	 * @param trackerGETURL
	 * @return a port number corresponding to a listening port or this peer
	 */
	 void sendHTTPGET(URL trackerGETURL) throws IOException;

	/**
	 * Construct a Tracker URL via decoded bencoded fields
	 * @param torrentInfo
	 * @param peerId
	 * @param port
	 * @return
	 */
	URL constructURL(TorrentInfo torrentInfo, byte[] peerId, int port);
	
	/**
	 * updates new events
	 * @param event
	 * @param torrentManager
	 * @return
	 * @throws IOException
	 */
	ArrayList<Peer> update(String event, TorrentManager torrentManager) throws IOException;
	
	/**
	 * Gets the response
	 * @return response
	 */
	byte[] getResponse();


}
