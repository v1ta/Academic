package action;

import java.io.IOException;
import java.net.URL;

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
}
