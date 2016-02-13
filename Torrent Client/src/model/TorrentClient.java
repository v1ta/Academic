package model;

import action.TorrentProtocol;
import model.message.*;
import util.*;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Takes a .p2PFile as a command line argument. Sets up the environment to communicate between a tracker and multiple peer(s)
 */
public class TorrentClient extends Thread implements TorrentProtocol{



    ArrayList<Peer> peers;
    public Tracker tracker;
    public LinkedBlockingQueue<Message> queue;
    public P2PFile p2PFile;
    File outputFile;
    File torrentFile;
    public boolean[] bits;
    public boolean isRunning = false;
    boolean downloadingStatus = true;

    /**
     * TorrentClient handles protocol logic and relevant object creation tasks
     * @param torrentFile
     */
    public TorrentClient(File torrentFile) throws IOException {
        super();
        this.torrentFile = torrentFile;
        peers = new ArrayList<>();
    }

    public void initialize() throws IOException {
        try {
            tracker = new Tracker(new TorrentInfo(Files.readAllBytes(torrentFile.toPath())), genPeerId(), this);
            bits  = new boolean[tracker.torrentInfo.piece_hashes.length];
        } catch (BencodingException | IOException e){
            System.err.println("ERROR: Failed to instantiate torrentInfo\nException: "
                    + e.getMessage());
        }

        p2PFile = new P2PFile(new File("video.mov"), tracker, this);

        isRunning = true;
        ArrayList<Peer> allPeers  = tracker.update("started", this);

        this.tracker.timer = new Timer();
        this.tracker.swarm = new Swarm(this);
        this.tracker.timer.schedule(tracker.swarm,  tracker.interval * 1000, tracker.interval * 1000);

        queue = new LinkedBlockingQueue<>();
        if (allPeers != null) {
            for (Peer p : allPeers) {
                System.out.println(p.toString());
                    if (!p.connect())
                        System.err.println("Wrong Peer IP or unable to contact peer" + p);
                    else
                       peers.add(p);

            }
        }
    }

    public byte[] genPeerId(){
        Random rand = new Random(System.currentTimeMillis());
        byte[] peerId = new byte[20];

        peerId[0] = 'K';
        peerId[1] = 'K';
        peerId[2] = '0';
        peerId[3] = '8';

        for (int i = 4; i < 20; i++) {
            peerId[i] = (byte) ('A' + rand.nextInt(26));
        }
        return peerId;
    }

    public synchronized void addToQueue(Message message){
        if (message == null) return;
        queue.add(message);
    }

    public void readMessage() throws IOException, InterruptedException {
        Message msg;
        if ((msg = queue.take()) == null) return;
        System.out.println("Receiving message: " + msg);
        switch (msg.id) {
            case Message.choke:
                msg.peer.choke[1] = true;
                break;
            case Message.unchoke:
                msg.peer.choke[1] = false;
                if(msg.peer.interest[0] == true){
                    msg.peer.send(msg.peer.getRequest());
                }
                break;
            case Message.interested:
                msg.peer.interest[1] = true;
                msg.peer.send(new Interested(1, Message.unchoke, msg.peer));
                break;
            case Message.notinterested:
                msg.peer.interest[1] = false;
                break;
            case Message.have:
                if (msg.peer.bitfield != null)
                    msg.peer.bitfield[(msg).index] = true;

                if(msg.peer.bitfield!= null)
                {
                    for(int j = 0; j < msg.peer.bitfield.length; j++){
                        if(msg.peer.bitfield[j] == true && this.bits[j] == false){
                            msg.peer.send(new Interested(1, Message.interested, msg.peer));
                            msg.peer.interest[0] = true;
                            break;
                        }
                    }
                }
                break;
            case Message.bitfield:
                boolean[] bitfield = HashSequenceHelper.bitfieldToBoolArray(((Bitfield)msg).bitfield, tracker.torrentInfo.piece_hashes.length);
                for (int i = 0; i < bitfield.length; ++i) {
                    msg.peer.bitfield[i] = bitfield[i];
                }
                for(int j = 0; j < msg.peer.bitfield.length; j++){
                    if(msg.peer.bitfield[j] == true && this.bits[j] == false){
                        msg.peer.send(new Interested(1, Message.interested, msg.peer));
                        msg.peer.interest[0] = true;
                        break;
                    }
                }
                break;
            case Message.piece: //!!TODO Not sure if this working properly, find way to debug
                Have haveMsg = new Have(((Piece)msg).index, msg.peer);
                if (!this.bits[((Piece)msg).index]) {
                    if (msg.peer.verifyPiece(this, tracker.torrentInfo.piece_hashes, (Piece) msg) == true) {
                        this.bits[(msg).index] = true;

                        for (Peer p : this.peers) {
                            if(p.isRunning)
                            try {
                                p.send(haveMsg);
                            } catch (Exception e) {
                                System.err.println("Exception sending have message to peer " + p + ": " + e);
                                continue;
                            }
                        }
                    }
                }
                if (p2PFile.isFileComplete()) {
                    this.downloadingStatus = false;
                    this.tracker.update("Completed", this);
                    System.out.println("File completed");
                    return;
                }
                if(!msg.peer.choke[1])
                    msg.peer.send(msg.peer.getRequest());
                break;
            case Message.cancel:
                System.err.println("Not responsible for cancel....");
                break;
            default:
                break;
        }
    }

    public void setDownloadUpload(int download, int upload) {
        String up = Integer.toString(upload);

        try {
            File tFile = new File(outputFile.getName() + ".stats");
            BufferedWriter out = new BufferedWriter(new FileWriter(tFile));
            out.write(up);
            out.close();
        } catch (IOException e) {
            System.err.println("Error: Unable to write tracker stats to a file.");
        }
    }

    public static boolean verifySHA1(byte[] piece, ByteBuffer SHA1Hash, int index) {
        MessageDigest SHA1;

        try {
            SHA1 = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            System.err.println("Unable to find SHA1 Algorithm");
            return false;
        }

        SHA1.update(piece);
        byte[] pieceHash = SHA1.digest();

        if (Arrays.equals(pieceHash, SHA1Hash.array())) {
            System.out.println("Verified - " + SHA1.digest() + " - " + SHA1Hash.array() + " for index " + index);
            return true;
        } else {
            return false;
        }
    }

    public void getUpload() throws IOException {
        String input;
        File tFile = new File(p2PFile.outputFile.getName() + ".stats");
        BufferedReader in = null;

        if (tFile.exists()) {
            in = new BufferedReader(new FileReader(tFile));
            input = in.readLine();
            tracker.uploaded = Integer.parseInt(input);
        } else {
            tracker.downloaded = 0;
            tracker.uploaded = 0;
        }
        if(in != null) {
            in.close();
        }
    }

    public void run() {
        System.out.print("Started DLing File");
        while (this.isRunning == true) {
            try {
                readMessage();
            } catch (Exception e) {
                System.err.println("Error: " + e);
            }
        }
        System.out.print("File Finished");
    }

    public void close() throws IOException {
        this.isRunning = false;
        if (this.peers != null) {
            for (Peer p : this.peers) {
                try {
                    p.disconnect();
                } catch (Exception e) {
                    System.err.println("Error while shutting down peer  " + p + " : " + e);
                    continue;
                }
            }
        }
    }


}
