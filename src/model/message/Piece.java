package model.message;

import model.Message;
import model.Peer;

import java.io.DataOutputStream;
import java.io.IOException;

public class Piece extends Message {
    public byte[] block;

    public Piece(int index,  int start,  byte[] block, Peer peer) {
        super(9 + block.length, Message.piece, peer);
        this.index = index;
        this.start = start;
        this.block = block;
    }

    @Override
    public void addPayload(DataOutputStream out) throws IOException {
        out.writeInt(this.index);
        out.writeInt(this.start);
        out.write(block);
    }

    public String toString() {
        return "ID: " + this.id + " Length: " + this.length
                + " index: " + this.index;
    }
}
