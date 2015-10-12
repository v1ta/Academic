package action;

import model.Peer;


import java.util.ArrayList;

/**
 * A set of method to facilitate the implementation of a bit torrent client
 */
public interface TorrentProtocol {

    //protected static HashMap<ByteBuffer, Object> dataFromTracker = null;

    /**
     * Gets a list of available peers from the tracker response dictionary
     * @param trackerData
     * @return
     */
    ArrayList<Peer> listPeers(byte[] trackerData);

    /**
     * Generates a peerID for the local client
     * @return
     */
    byte[] genPeerId();

}
