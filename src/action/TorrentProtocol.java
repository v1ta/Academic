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
     * Handles TorrentManager initialization which can't be done in the constructor
     * @throws IOException
     */
    void configure() throws IOException;

    /**
     * Addes to the message queue
     * @param message
     */
    void addToQueue(model.Message message);

    /**
     * Designed to handle all message logic routing, in this implementation only some of it is used.
     * @throws IOException
     * @throws InterruptedException
     */
    void leech() throws IOException, InterruptedException;

    /**
     * Updates the output file
     * @param piece
     * @param SHA1Hash
     * @param data
     * @return
     * @throws Exception
     */
    boolean UpdateFile(Piece piece, ByteBuffer SHA1Hash,	byte[] data) throws Exception;

    /**
     * Exactly what the method sig says
     * @param download
     * @param upload
     */
    void setDownloadUpload(int download, int upload);

    /**
     * See method sig
     * @return
     */
    boolean isFileComplete();

    /**
     * Logic for getting upload amount
     * @throws IOException
     */
    void getUpload() throws IOException;
}
