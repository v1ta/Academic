package model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.TimerTask;

/**
 * Swarm handles peer updates, it is launched in a new thread to handle concurrency issues.
 */
public class Swarm extends TimerTask  {
    TorrentClient torrentClient;

    public Swarm(TorrentClient torrentClient){
        this.torrentClient = torrentClient;
    }

    public void run(){
        ArrayList<Peer> peers = null;
        try {
            peers = torrentClient.tracker.update("", torrentClient);
        } catch (IOException e) {
            e.printStackTrace();
        }
        boolean isAlreadyAPeer = false;

        for(Peer p : peers){
            for(Peer q : torrentClient.peers){
                if(Arrays.equals(p.peerId, q.peerId)){
                    isAlreadyAPeer = true;
                    break;
                }
            }
            if(isAlreadyAPeer == false){
                this.torrentClient.peers.add(p);
            } else {
                isAlreadyAPeer = false;
            }
        }
    }

}
