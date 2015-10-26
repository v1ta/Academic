package model;

import action.MessageCreation;
import model.message.*;

import java.io.*;

public abstract class Message implements MessageCreation{

    /* byte constants for message factory */
    protected static final byte keepAlive = -1;
    protected static final byte choke = 0;
    protected static final byte unchoke = 1;
    protected static final byte interested = 2;
    protected static final byte notinterested = 3;
    protected static final byte have = 4;
    protected static final byte bitfield = 5;
    protected static final byte request = 6;
    protected static final byte piece = 7;
    protected static final byte cancel = 8;

    protected final byte id;
    protected final int length;

    /* fields to extend */
    public int index;
    protected int start;
    protected Peer peer;

    protected Message(final int length, final byte id, Peer peer) {
        this.id = id;
        this.length = length;
        this.peer = peer;
    }

    /* constants for toString() */
    static final String[] TYPE_NAMES = new String[]{"Choke", "Unchoke", "Interested",
        "NotInterested", "Have", "Bitfield", "Request", "Piece", "Cancel", "Port"};

    public static Message MessageFactory(final InputStream in, Peer peer) throws IOException {
        DataInputStream dataIn = new DataInputStream(in);

        int length = dataIn.readInt();


        if (length == 0) {
            return new KeepAlive(0,(byte)255,peer);
        }

        int bit = dataIn.readByte();

        switch (bit) {
            case (choke):
                return new Choke(1, choke, peer);
            case (unchoke):
                return new Unchoke(1, unchoke, peer);
            case (interested):
                return new Interested(1, interested, peer);
            case (notinterested):
                return new NotInterested(1, notinterested, peer);
            case (have):
                return new Have(dataIn.readInt(), peer);
            case (bitfield):
                byte[] arr = new byte[length - 1];
                dataIn.readFully(arr);
                return new Bitfield(arr, peer);
            case (piece):
                int ind = dataIn.readInt();
                int start = dataIn.readInt();
                byte[] block = new byte[length - 9];
                dataIn.readFully(block);
                return new Piece(ind, start, block, peer);
            case (request):
                int indi = dataIn.readInt();
                int begin = dataIn.readInt();
                length = dataIn.readInt();
                return new Request(indi, begin, length, peer);
        }
        return null;
    }

    public static void encode(final Message message, final OutputStream out) throws IOException {
        System.out.println("Encoding message " + message);

        if (message != null) {
            {
                DataOutputStream dos = new DataOutputStream(out);
                dos.writeInt(message.length);
                if (message.length > 0) {
                    dos.write(message.id);
                    message.addPayload(dos);
                }
                dos.flush();
            }
        }
    }

    public abstract void addPayload(DataOutputStream out) throws IOException;

    @Override
    public String toString() {
        if(this.length == 0){
            return "Keep-Alive";
        }
        return TYPE_NAMES[this.id];
    }
}
