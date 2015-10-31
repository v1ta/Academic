package action;

<<<<<<< HEAD
=======
import model.*;
import model.message.Piece;
>>>>>>> a7d12b69c515aec397d91f9fb598ec5a0464f3dd

import model.Peer;

<<<<<<< HEAD
=======
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
//>>>>>>> a7d12b69c515aec397d91f9fb598ec5a0464f3dd

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
<<<<<<< HEAD
    byte[] genPeerId();
    
    /**
     * Checks to see if 
     * @throws IOException
     */
    void haveFile() throws IOException;
    
    /**
     * adds message to the queue
     * @param message
     */
    void addToQueue(Message message);

    /**
     * 
     * @throws IOException
     */
    void leech() throws IOException;

    /**
     * Updates file with more data
     * @param piece
     * @param SHA1Hash
     * @param data
     * @return true if successful in updating file
     * @throws Exception
     */
    boolean UpdateFile(Piece piece, ByteBuffer SHA1Hash, byte[] data) throws Exception;
    
    /**
     * Sets download and upload variables
     * @param download
     * @param upload
     */
    void setDownloadUpload(int download, int upload);
    
    /**
     * Checks the number of pieces
     * @return
     * @throws IOException
     */
    boolean[] checkPieces() throws IOException;
    
    /**
     * Verifies that SHA1 algorithm exists
     * @param piece
     * @param SHA1Hash
     * @param index
     * @return
     */
    boolean verifySHA1(byte[] piece, ByteBuffer SHA1Hash, int index) ;

    /**
     * Checks to see if the file is complete
     * @return true when file is complete
     */
    public boolean isFileComplete();

    /**
     * get upload
     * @throws IOException
     */
    void getUpload() throws IOException;

    /**
     * Reads the file from the given an offset
     * @param index
     * @param offset
     * @param length
     * @return
     * @throws IOException
     */
    byte[] readFile(int index, int offset, int length) throws IOException ;
    

    /**
     * runs leech 
     */
    void run() ;

    
=======
    boolean isFileComplete();

    /**
     * Logic for getting upload amount
     * @throws IOException
     */
    void getUpload() throws IOException;
>>>>>>> a7d12b69c515aec397d91f9fb598ec5a0464f3dd
}
