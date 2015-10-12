package action;


import model.Peer;


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

    
}
