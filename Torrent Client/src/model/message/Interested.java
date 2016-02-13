package model.message;

import model.Message;
import model.Peer;

import java.io.DataOutputStream;
import java.io.IOException;

public class Interested extends Message {
    public Interested(int length, byte id, Peer peer){
        super(length, id, peer);
    }

    @Override
    public void addPayload(DataOutputStream out) throws IOException {
        return;
    }
}
