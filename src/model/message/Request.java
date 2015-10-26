package model.message;

import model.Message;
import model.Peer;

import java.io.DataOutputStream;
import java.io.IOException;

public class Request extends Message {
    public int mlength;

    public Request(int index,  int start,  int length, Peer peer) {
        super(13, Message.request, peer);
        this.index = index;
        this.start = start;
        this.mlength = length;
        System.out.println("test");
    }

    @Override
    public void addPayload(DataOutputStream out) throws IOException {
        out.writeInt(this.index);
        out.writeInt(this.start);
        out.writeInt(mlength);
    }

    public String toString() {
        return new String("Length: " + this.length + " ID: " + this.id
                + " Index: " + this.index + " Start: " + this.start
                + " Block: " + this.mlength);

    }
}
