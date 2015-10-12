package action;

import model.Peer;


import java.util.ArrayList;

/**
 * A set of method to facilitate the implementation of a bit torrent client
 */
public interface TorrentProtocol {

    /**
     * Generates a peerID for the local client
     * @return
     */
    byte[] genPeerId();

}
