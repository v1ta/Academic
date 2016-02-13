package model;

import action.P2PFileLogic;
import model.message.Piece;
import util.ToolKit;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/**
 * Created by Joseph on 10/28/15.
 */
public class P2PFile implements P2PFileLogic{
    File outputFile;
    Tracker tracker;
    TorrentClient torrentClient;
    int[] SHAhash;
    boolean[] currReqBits;
    boolean[] bitfield;

    public P2PFile(File outputFile, Tracker tracker, TorrentClient torrentClient) throws IOException {
        this.outputFile = outputFile;
        this.tracker = tracker;
        this.torrentClient = torrentClient;
        try {
            outputFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        bitfield = ToolKit.checkPieces(tracker.torrentInfo, outputFile);
        currReqBits = new boolean[tracker.torrentInfo.piece_length];
        SHAhash = new int[tracker.torrentInfo.piece_hashes.length];
        Arrays.fill(currReqBits, false);
        Arrays.fill(SHAhash, 0);
    }

    public int rarePieceBitField(Peer peer){
        refreshPiecesBitField();
        int min = Integer.MAX_VALUE;

        for(int i = 0; i < this.SHAhash.length; i++){
            if(this.bitfield[i] == false && peer.bitfield[i] == true){
                if(min > this.SHAhash[i]){
                    min = SHAhash[i];
                }
            }
        }

        if(min == Integer.MAX_VALUE){
            return -1;
        }

        ArrayList<Integer> indices = new ArrayList<>();

        for(int i = 0; i < this.SHAhash.length; i++){
            if(this.bitfield[i] == false && peer.bitfield[i] == true){
                if(min == this.SHAhash[i]){
                    indices.add(i);
                }
            }
        }

        Random random = new Random();
        int n = random.nextInt(indices.size());

        return indices.get(n);
    }

    public void refreshPiecesBitField(){
        Arrays.fill(this.SHAhash, 0);
        for(Peer peer : torrentClient.peers){
            for(int i = 0; i < this.SHAhash.length; i++){
                if(peer.bitfield[i] == true){
                    this.SHAhash[i] += 1;
                    if(this.currReqBits[i])
                        this.SHAhash[i] += 10;
                }
            }
        }
    }

    public boolean isFileComplete() {
        for(int i = 0; i < torrentClient.bits.length; i++){
            if(torrentClient.bits[i] == false){
                return false;
            }
        }
        try {
            torrentClient.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    public boolean updateFile(Piece piece, ByteBuffer SHA1Hash, byte[] data) throws Exception {
        if (TorrentClient.verifySHA1(data, SHA1Hash, piece.index)) {
            RandomAccessFile raf = new RandomAccessFile(this.outputFile, "rws");

            raf.seek((tracker.torrentInfo.piece_length * piece.index));
            raf.write(data);
            raf.close();

            tracker.downloaded += data.length;

            System.out.println("Wrote to output file piece: " + piece.index);
            return true;
        } else{
            System.out.println("Failed to verified piece: " + piece.index);
            return false;
        }
    }
}
