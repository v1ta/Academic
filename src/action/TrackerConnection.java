package action;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.URL;
<<<<<<< HEAD
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.Peer;
<<<<<<< HEAD
import model.TorrentManager;
import util.Bencoder2;
import util.BencodingException;
import util.HashConstants;
import util.HashSequenceHelper;
=======
import java.util.ArrayList;

import model.Peer;
import model.TorrentManager;
>>>>>>> a7d12b69c515aec397d91f9fb598ec5a0464f3dd
=======
import model.TorrentClient;
>>>>>>> f31405678895e418237c459e0d625c4d549b59fe
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
	 byte[] sendHTTPGET(URL trackerGETURL) throws IOException;

	/**
	 * Construct a Tracker URL via decoded bencoded fields
	 * @param torrentInfo
	 * @param peerId
	 * @param port
	 * @return
	 */
	URL constructURL(TorrentInfo torrentInfo, byte[] peerId, int port);
<<<<<<< HEAD
	
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


=======

	/**
	 * Implements the logic to send a GET request, decode the response, store response in correct location(s)
	 * @param event
	 * @param torrentClient
	 * @return ArrayList of Peer objects
	 * @throws IOException
	 */
<<<<<<< HEAD
	public ArrayList<Peer> update(String event, TorrentManager torrentManager) throws IOException;
>>>>>>> a7d12b69c515aec397d91f9fb598ec5a0464f3dd
=======
	 ArrayList<Peer> update(String event, TorrentClient torrentClient) throws IOException;
>>>>>>> f31405678895e418237c459e0d625c4d549b59fe
}
