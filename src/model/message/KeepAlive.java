package model.message;

import model.Message;
import model.Peer;

import java.io.DataOutputStream;
import java.io.IOException;

public class KeepAlive extends Message {
    public KeepAlive(int length, byte id, Peer peer){super(length,id, peer);}

    @Override
    protected void addPayload(DataOutputStream out) throws IOException {}
}
