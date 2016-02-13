package action;

import model.Peer;
import model.message.Piece;

import java.nio.ByteBuffer;

/**
 * Created by Joseph on 10/28/15.
 */
public interface P2PFileLogic {
    /**
     * Update the piece bits associated with a torrent file
     */
    void refreshPiecesBitField();

    /**
     * Checks if the file is complete
     * @return true if the file associated with the tracker has finished downloading, false otherwise.
     */
    boolean isFileComplete();

    /**
     * Algorithm to find the rarest piece !TODO MAKE SURE THIS WORKS
     * @param peer
     * @return index of rarest piece
     */
    int rarePieceBitField(Peer peer);

    /**
     * Updates the output file
     * @param piece
     * @param SHA1Hash
     * @param data
     * @return
     * @throws Exception
     */
    boolean updateFile(Piece piece, ByteBuffer SHA1Hash, byte[] data) throws Exception;
}
