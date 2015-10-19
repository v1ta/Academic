package model.message;

import model.Message;
import model.Peer;

import java.io.DataOutputStream;
import java.io.IOException;

public class Cancel extends Message {
    int clength;

    public Cancel(int index, int start,	int length, Peer peer) {
        super(13, Message.cancel, peer);
        this.index = index;
        this.start = start;
        this.clength = length;
    }

    @Override
    public void addPayload(DataOutputStream out) throws IOException {
        out.writeInt(this.index);
        out.writeInt(this.start);
        out.writeInt(clength);
        return;
    }
}
