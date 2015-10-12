package model.message;

import model.Message;
import model.Peer;

import java.io.DataOutputStream;
import java.io.IOException;

public class Have extends Message {
    public Have(int index, Peer peer) {
        super(5, Message.have, peer);
        this.index = index;
    }

    @Override
    protected void addPayload(DataOutputStream out) throws IOException {
        out.writeInt(index);
        return;
    }
}
