package model;

import action.TorrentProtocol;
import model.message.Bitfield;
import model.message.Have;
import model.message.Interested;
import model.message.Piece;
import util.*;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class TorrentManager extends Thread implements TorrentProtocol{

    ArrayList<Peer> peers;
    public Tracker tracker;
    LinkedList<Message> queue = new LinkedList<>();
    boolean[] currReqBits;
    File outputFile;
    public boolean[] bits;
    boolean isRunning = false;
    public static boolean haveFullFile = false;
    boolean downloadingStatus = true;

    /**
     * TorrentManager handles protocol logic and relevant object creation tasks
     * @param torrentFile
     */
    public TorrentManager(File torrentFile){
        try {
            tracker = new Tracker(new TorrentInfo(Files.readAllBytes(torrentFile.toPath())), genPeerId(), this);
        } catch (BencodingException | IOException e){
            System.err.println("ERROR: Failed to instantiate torrentInfo\nException: "
                    + e.getMessage());
        }

        try {
            tracker.sendHTTPGET(tracker.trackerGETURL);
        } catch (IOException e) {
           System.err.println("No response from tracker\n"+ e.getMessage());
        }

        currReqBits = new boolean[tracker.torrentInfo.piece_length];
        Arrays.fill(currReqBits, false);
        isRunning = true;
    }

    public void haveFile() throws IOException {
        if(haveFullFile) {
            downloadingStatus = false;
            tracker.update("completed", this);
            // RUBTClient.log("Finished downloading. Will now seed.");
            tracker.downloaded = tracker.torrentInfo.file_length;

            byte[] bitfield = HashSequenceHelper.boolToBitfieldArray(this.bits);
            //Bitfield bitMsg = new Bitfield(bitfield, peer);

            // Send the Peers our completed Bitfield!
            for (Peer peer : this.peers) {
                try {
                    peer.send(new Bitfield(bitfield, peer));
                } catch (Exception e) {
                    System.err.println("Exception sending have message to peer " + peer + ": " + e);
                    continue;
                }
            }
        }
    }

    public byte[] genPeerId(){
        Random rand = new Random(System.currentTimeMillis());
        byte[] peerId = new byte[20];

        peerId[0] = 'K';
        peerId[1] = 'K';
        peerId[2] = '0';
        peerId[3] = '1';

        for (int i = 4; i < 20; i++) {
            peerId[i] = (byte) ('A' + rand.nextInt(26));
        }
        return peerId;
    }

    public ArrayList<Peer> listPeers(byte[] trackerData){

        ArrayList<Peer> peers = new ArrayList<>();
        HashMap<ByteBuffer, Object> ableToRead = null;
        int peerPort;
        byte[] peerId;
        String peerIP;
        Peer peer;

        try {
            ableToRead = (HashMap<ByteBuffer, Object>) Bencoder2.decode(trackerData);
        } catch (BencodingException e) {
            System.err.println("EXCEPTION: " + e.getMessage());
        }

        //ToolKit.printMap(ableToRead, 3);

        List<Map<ByteBuffer, Object>> peersList = (List<Map<ByteBuffer, Object>>) ableToRead.get(HashConstants.KEY_PEERS);


        for (Map<ByteBuffer, Object> rawPeer : peersList) {
            peerPort = ((Integer) rawPeer.get(HashConstants.KEY_PORT)).intValue();
            peerId = ((ByteBuffer) rawPeer.get(HashConstants.KEY_PEERID)).array();
            peerIP = null;
            try {
                peerIP = new String(((ByteBuffer) rawPeer.get(HashConstants.KEY_IP)).array(),"ASCII");
                peer = new Peer(peerId, peerPort, peerIP, tracker, this);
                peers.add(peer);

            } catch (UnsupportedEncodingException e) {
                System.err.println("Failed to extract peers\nEXCEPTION: " + e.getMessage());
            }

        }

        return peers;
    }

    public void addToQueue(Message message){
        queue.add(message);
    }

    public void leech() throws IOException {
        Message msg;
        if ((msg = queue.poll()) == null) return;

        switch (msg.id) {
            case Message.choke:
                msg.peer.choke[1] = true;
                break;
            case Message.unchoke:
                msg.peer.choke[1] = false;
                if(msg.peer.interest[0] == true){
                    msg.peer.send(msg.peer.getNextRequest());
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
                boolean isSeed = true;
                for (int i = 0; i < bitfield.length; ++i) {
                    msg.peer.bitfield[i] = bitfield[i];
                    if(!bitfield[i])
                        isSeed = false;
                }

                for(int j = 0; j < msg.peer.bitfield.length; j++){
                    if(msg.peer.bitfield[j] == true && this.bits[j] == false){
                        msg.peer.send(new Interested(1, Message.interested, msg.peer));
                        msg.peer.interest[0] = true;
                        break;
                    }
                }
                /*
                if(isSeed)
                {
                    RUBTClient.addSeed();
                }
                */
                break;
            case Message.piece:
                Have haveMsg = new Have(((Piece)msg).index, msg.peer);
                //peerMsg.peer.downloadRate += ((Message.Piece)peerMsg.message).block.length;

                if (!this.bits[((Piece)msg).index]) {
                    if (msg.peer.appendToPieceAndVerifyIfComplete((Piece)msg, tracker.torrentInfo.piece_hashes, this) == true) {
                        this.bits[(msg).index] = true;
                        //RUBTClient.addProgressBar(1);

                        for (Peer p : this.peers) {
                            try {
                                p.send(haveMsg);
                            } catch (Exception e) {
                                System.err.println("Exception sending have message to peer " + p + ": " + e);
                                continue;
                            }
                        }
                    }
                }
                if (this.isFileComplete()) {
                    //log.fine("Completed download: shutting down manager");
                    this.downloadingStatus = false;
                    this.tracker.update("completed", this);
                    //RUBTClient.log("Finished downloading. Will now seed.");
                    //RUBTClient.toggleProgressBarLoading();
                    return;
                }
                //	totalDownload += (((Message.Piece)peerMsg.message).block.length / (System.currentTimeMillis() - currentDownloadTime)/10.0);
                //	downloadCount++;
                //RUBTClient.setDownloadRate((totalDownload/downloadCount));
                //currentDownloadTime = System.currentTimeMillis();
                //RUBTClient.updatePeerDownRate(peerMsg.peer, peerMsg.peer.downloadRate/10.0);

                //Tracker.downloaded+=((Message.Piece)peerMsg.message).length;
                if(!msg.peer.choke[1])
                    msg.peer.send(msg.peer.getNextRequest());
                break;
            case Message.cancel:
                System.err.println("Not responsible for cancel....");
                break;
            default:
                break;
        }
    }

    public boolean UpdateFile(Piece piece, ByteBuffer SHA1Hash,	byte[] data) throws Exception {
        if (verifySHA1(data, SHA1Hash, piece.index)) {
            RandomAccessFile raf = new RandomAccessFile(this.outputFile, "rws");

            raf.seek((tracker.torrentInfo.piece_length * piece.index));
            raf.write(data);
            raf.close();

            tracker.downloaded += data.length;

            System.out.println("Wrote to output file piece: " + piece.index);
            return true;
        } else{
            System.out.println("Failed to verify piece: " + piece.index);
            return false;
        }
    }

    public void setDownloadUpload(int download, int upload) {
        String down = Integer.toString(download);
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

    public boolean[] checkPieces() throws IOException {

        int numPieces = tracker.torrentInfo.piece_hashes.length;
        int pieceLength = tracker.torrentInfo.piece_length;
        int fileLength = tracker.torrentInfo.file_length;
        ByteBuffer[] pieceHashes = tracker.torrentInfo.piece_hashes;
        int lastPieceLength = fileLength % pieceLength == 0 ? pieceLength : fileLength % pieceLength;

        byte[] piece = null;
        boolean[] verifiedPieces = new boolean[numPieces];

        for (int i = 0; i < numPieces; i++) {
            if (i != numPieces - 1) {
                piece = new byte[pieceLength];
                piece = readFile(i, 0, pieceLength);
            } else {
                piece = new byte[lastPieceLength];
                piece = readFile(i, 0, lastPieceLength);
            }

            if (verifySHA1(piece, pieceHashes[i], i)) {
                verifiedPieces[i] = true;
                //RUBTClient.log("Verified piece " + i);
            }
        }

        for(int i = 0; i < verifiedPieces.length; i++){
            if(verifiedPieces[i] != false){
                //RUBTClient.addProgress(torrentInfo.piece_length);
            }

            if(i == verifiedPieces.length - 1){
                this.downloadingStatus = false;
            }
        }

        return verifiedPieces;
    }

    public static boolean verifySHA1(byte[] piece, ByteBuffer SHA1Hash, int index) {
        MessageDigest SHA1;

        try {
            SHA1 = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            System.err.println("Unable to find SHA1 Algorithm");
            //RUBTClient.log("Unable to find SHA1 Algorithm");
            return false;
        }

        SHA1.update(piece);
        byte[] pieceHash = SHA1.digest();

        if (Arrays.equals(pieceHash, SHA1Hash.array())) {
            System.err.println("Verified - " + SHA1.digest() + " - " + SHA1Hash.array() + " for index " + index);
            return true;
        } else {
            return false;
        }
    }

    public boolean isFileComplete() {
        for(int i = 0; i < this.bits.length; i++){
            if(this.bits[i] == false){
                return false;
            }
        }
        return true;
    }

    public void getUpload() throws IOException {
        String input;
        File tFile = new File(outputFile.getName() + ".stats");
        BufferedReader in = null;

        if (tFile.exists()) {
            in = new BufferedReader(new FileReader(tFile));
            input = in.readLine();
            tracker.uploaded = Integer.parseInt(input);
            //log.info("Increased upload amount");
            //RUBTClient.addAmountUploaded(Tracker.uploaded);
        } else {
            tracker.downloaded = 0;
            tracker.uploaded = 0;
        }
        if(in != null) {
            in.close();
        }
    }

    public byte[] readFile(int index, int offset, int length) throws IOException {
        RandomAccessFile raf = new RandomAccessFile(this.outputFile, "r");
        byte[] data = new byte[length];

        raf.seek(tracker.torrentInfo.piece_length * index + offset);
        raf.readFully(data);
        raf.close();

        return data;
    }

    public void run() {
        System.out.print("Started DLing File");
        while (this.isRunning == true) {
            try {
                leech();
            } catch (Exception e) {
                System.err.println("Error: " + e);
            }
        }
        System.out.print("File Finished");
    }

}
