package model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.TimerTask;

/**
 * Created by Joseph on 10/15/15.
 */
public class Swarm extends TimerTask  {
    TorrentManager torrentManager;

    public Swarm(TorrentManager torrentManager){
        this.torrentManager = torrentManager;
    }

    public void run(){
        ArrayList<Peer> peers = null;
        try {
            peers = torrentManager.tracker.update("",torrentManager);
        } catch (IOException e) {
            e.printStackTrace();
        }
        boolean isAlreadyAPeer = false;

        for(Peer p : peers){
            for(Peer q : torrentManager.peers){
                if(Arrays.equals(p.peerId, q.peerId)){
                    isAlreadyAPeer = true;
                    break;
                }
            }
            if(isAlreadyAPeer == false){
                this.torrentManager.peers.add(p);
            } else {
                isAlreadyAPeer = false;
            }
        }
    }

}
