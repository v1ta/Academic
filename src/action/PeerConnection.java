package action;

public interface PeerConnection {

	/**
	 * Generate handshake with a peer
	 * @param peer
	 * @param infohash
	 * @return the byte[]
	 */
	byte[] handshake(byte[] peer, byte[] infohash);

	/**
	 * Confirm the result of a handshake
	 * @param infoHash
	 * @param response
	 * @return
	 */
	boolean confirmHandshake(byte[] infoHash, byte[] response);

	/**
	 * Establish a connection with a peer
	 * @return
	 */
	boolean connect();
}
