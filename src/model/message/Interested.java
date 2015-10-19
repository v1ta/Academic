package model.message;

import model.Message;
import model.Peer;

import java.io.DataOutputStream;
import java.io.IOException;

public class Interested extends Message {
    public Interested(int length, byte id, Peer peer){
        super(1, Message.interested, peer);
        System.out.println("test22");
    }

    @Override
    public void addPayload(DataOutputStream out) throws IOException {
        return;
    }
}
