package action;

import model.*;
import model.message.Piece;


import java.io.IOException;
import java.nio.ByteBuffer;
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

    /**
     * Handles TorrentClient initialization which can't be done in the constructor
     * @throws IOException
     */
    void initialize() throws IOException;

    /**
     * Adds to the message queue
     * @param message
     */
    void addToQueue(model.Message message);

    /**
     * Designed to handle all message logic routing, in this implementation only some of it is used.
     * @throws IOException
     * @throws InterruptedException
     */
    void readMessage() throws IOException, InterruptedException;


    /**
     * Exactly what the method sig says
     * @param download
     * @param upload
     */
    void setDownloadUpload(int download, int upload);


    /**
     * Logic for getting upload amount
     * @throws IOException
     */
    void getUpload() throws IOException;

    /**
     * Close the ports and files associated with a torrent client on exit
     * @throws IOException
     */
    void close() throws IOException;


}
