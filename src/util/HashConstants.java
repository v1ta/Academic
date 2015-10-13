package util;

import java.nio.ByteBuffer;

/**
 * Created by Joseph on 10/10/15.
 */
public class HashConstants {

    public static final ByteBuffer KEY_PEERS = ByteBuffer.wrap(new byte[] {'p', 'e', 'e', 'r', 's' });

    public static final ByteBuffer KEY_PEERID = ByteBuffer.wrap(new byte[] {'p', 'e', 'e', 'r', ' ', 'i', 'd' });

    public static final ByteBuffer KEY_IP = ByteBuffer.wrap(new byte[] { 'i','p' });

    public static final ByteBuffer KEY_PORT = ByteBuffer.wrap(new byte[] { 'p','o', 'r', 't' });

    public static final ByteBuffer KEY_FAILURE = ByteBuffer.wrap(new byte[] {
            'f', 'a', 'i', 'l', 'u', 'r', 'e', ' ', 'r', 'e', 'a', 's', 'o',
            'n' });

    public static final ByteBuffer KEY_INTERVAL = ByteBuffer.wrap(new byte[] {
            'i', 'n', 't', 'e', 'r', 'v', 'a', 'l' });

    public static final byte[] KEY_BT = { 'B', 'i', 't', 'T', 'o', 'r', 'r', 'e', 'n', 't', ' ',
            'p', 'r', 'o', 't', 'o', 'c', 'o', 'l' };

    public static final byte[] PEER_ID_PHASE_ONE  = new byte[] { '-', 'R', 'U', '1','1','0' };
}
