package model.message;

import model.Message;
import model.Peer;

import java.io.DataOutputStream;
import java.io.IOException;

public class Choke extends Message {
    public Choke(int length, byte id, Peer peer){super(length,id,peer);}

    @Override
    public void addPayload(DataOutputStream out) throws IOException {return;}
}
