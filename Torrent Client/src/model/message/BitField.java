package model.message;

import model.Message;
import model.Peer;

import java.io.DataOutputStream;
import java.io.IOException;

public class BitField extends Message {

    public final byte[] bitfield;

    public BitField(final byte[] bitfield, Peer peer) {
        super(bitfield.length + 1, Message.bitfield, peer);
        this.bitfield = bitfield;
    }

    public void addPayload(DataOutputStream out) throws IOException {
        out.write(this.bitfield);
        return;
    }
}
