package action;

public interface PeerConnection {

	/**
	 * Generate handshake with a peer, record response
	 * @param peer
	 * @param infohash
	 * @return the byte[]
	 */
	byte[] handshake(byte[] peer, byte[] infohash);

	/**
	 * Confirm the result of handshake()
	 * @param infoHash
	 * @param response
	 * @return
	 */
	boolean confirmHandshake(byte[] infoHash, byte[] response);

	/**
	 * Establish a connection with a peer
	 * Launches new thread if true
	 * @return
	 */
	boolean connect();
}
