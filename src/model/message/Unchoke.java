package model.message;

import model.Message;
import model.Peer;

import java.io.DataOutputStream;
import java.io.IOException;

public class Unchoke extends Message {
    public Unchoke(int length, byte id, Peer peer){super(1, Message.unchoke, peer);}

    @Override
    protected void addPayload(DataOutputStream out) throws IOException {}
}
