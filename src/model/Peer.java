package model;

import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingQueue;

import action.PeerConnection;
import model.message.*;
import util.HashConstants;

public class Peer extends Thread implements PeerConnection{

	Tracker tracker;
	TorrentManager torrentManager;
	Socket socket;
	int port;
	public byte[] peerId;
	public boolean[] bitfield = null;
	String host;
	boolean[] choke =  {true, true}; // {we are choking, peer are choking}
	boolean[] interest = {false, false}; //{we are interested, they are interested}
	InputStream in;
	OutputStream out;
	Connection connection;
	public int previousIndex = -1;
	private int currentByteOffset = 0;
	public long totalDownload =0L;
	public long totalUpload = 0L;

	ByteArrayOutputStream piece = null;
	int currentPieceIndex = -1;
	int totalBytesWritten = 0;
	LinkedBlockingQueue<Request> requestQueue = new LinkedBlockingQueue<>();

	public Peer(byte[] peerId, int port, String host, Tracker tracker, TorrentManager torrentManager) {
		//super("Peer@" + ip + ":" + port);
		this.peerId = peerId;
		this.port = port;
		this.host = host;
		this.tracker = tracker;
		this.torrentManager = torrentManager;
		this.connection = new Connection(this);
	}

	public boolean connect() {

		//check to see if peer id doesn't start with RU11
		byte[] id = new byte[4];
		System.arraycopy(this.peerId, 0, id, 0, 4);

		try {

			createSocket();

			DataOutputStream os = new DataOutputStream(this.out);
			DataInputStream is = new DataInputStream(this.in);

			os.write(handshake(peerId, tracker.torrentInfo.info_hash.array()));
			os.flush();

			byte[] response = new byte[68];

			this.socket.setSoTimeout(10000);
			is.readFully(response);
			this.socket.setSoTimeout(130000);
			if (!confirmHandshake(tracker.torrentInfo.info_hash.array(), response)) {
				return false;
			} else {
				this.start();
				return true;
			}

		} catch (Exception e) {
			System.err.println("Failed to connect to peer @ " + this.host +"\nEXCEPTION: " + e.getMessage());
			return false;
		}
	}

	public synchronized void createSocket() throws IOException {
		this.socket = new Socket(this.host, this.port);
		this.in = this.socket.getInputStream();
		this.out = this.socket.getOutputStream();
	}

	public byte[] handshake(byte[] peer, byte[] infohash) {
		int index = 0;
		byte[] handshake = new byte[68];

		handshake[index] = 0x13;
		index++;

		System.arraycopy(HashConstants.KEY_BT, 0, handshake, index, HashConstants.KEY_BT.length);
		index += HashConstants.KEY_BT.length;

		/* message id */
		byte[] zero = new byte[8];
		for(int i = 0; i < zero.length; i++){
			zero[i] = 0;
		}
		System.arraycopy(zero, 0, handshake, index, zero.length);
		index += zero.length;


		System.arraycopy(infohash, 0, handshake, index, infohash.length);
		index += infohash.length;

		System.arraycopy(peer, 0, handshake, index, peer.length);


		System.out.println("handshake : " + handshake);
		return handshake;
	}

	public boolean confirmHandshake(byte[] infoHash, byte[] response) {
		byte[] peerHash = new byte[20];
		System.arraycopy(response, 28, peerHash, 0, 20);

		if(!Arrays.equals(peerHash, infoHash))
		{
			System.err.println("handshake failed");
			return false;
		}
		return true;
	}

	public void run() {
		while(this.socket != null){
			Message msg;
			try {
				msg = Message.MessageFactory(this.in, this);
			} catch (IOException e) {
				System.err.println("Invalid stream for peer " + this.toString()+ "\nEXCEPTION: "+ e.getMessage());
				e.printStackTrace();
				break;
			}

			if (msg != null)
				if (msg.id == Message.request) requestQueue.add((Request) msg);
				else torrentManager.addToQueue(msg);
		}
	}

	public void send(Message msg) throws IOException {
		if (this.out == null) {
			throw new IOException(this
					+ " cannot send a message on an empty socket.");
		}
		System.err.println("Sending " + msg + " to " + this);
		Message.encode(msg, out);
	}

	public void choke(){
		try {
			send(new Choke(1, Message.choke, this));
		} catch (IOException e) {
			System.err.println("Unable to send choke to peer");
		}
		choke[0] = true;
		//RUBTClient.updatePeerChokeStatus(this, true);
	}

	public void unchoke(){
		try {
			send(new Unchoke(1, Message.unchoke, this));
		} catch (IOException e) {
			System.err.println("Unable to send unchoke to peer");
		}
		choke[0] = false;
		//RUBTClient.updatePeerChokeStatus(this, false);
	}

	public Request getNextRequest() {
		int piece_length = this.tracker.torrentInfo.piece_length;
		int file_length = this.tracker.torrentInfo.file_length;
		int requestSize = tracker.requestSize;
		int numPieces = this.tracker.torrentInfo.piece_hashes.length;

		if(this.currentPieceIndex == (numPieces - 1)){
			piece_length = file_length % this.tracker.torrentInfo.piece_length;
		}

		if((this.currentByteOffset + requestSize) > piece_length){
			requestSize = piece_length % requestSize;
		}

		Request request = new Request(this.currentPieceIndex, this.currentByteOffset, requestSize, this);

		if((this.currentByteOffset + requestSize) >= piece_length){
			this.currentPieceIndex = -1;
			this.currentByteOffset = 0;
		} else {
			this.currentByteOffset += requestSize;
		}

		return request;
	}

	public boolean appendToPieceAndVerifyIfComplete(Piece pieceMsg, ByteBuffer[] hashes, TorrentManager manager) {

		int currentPieceLength = (pieceMsg.index == (this.tracker.torrentInfo.piece_hashes.length - 1)) ?
				this.tracker.torrentInfo.file_length % this.tracker.torrentInfo.piece_length : this.tracker.torrentInfo.piece_length;

		if (this.piece == null) {
			this.piece = new ByteArrayOutputStream();
		}

		try {
			piece.write(pieceMsg.block, 0, pieceMsg.block.length);
			//RUBTClient.log(Integer.toString(this.piece.toByteArray().length));
		} catch (Exception e) {
			System.err.println("Unable to write to file at " + pieceMsg.index + " with offset " + pieceMsg.start);
		}

		if(this.currentPieceIndex == -1){
			this.totalBytesWritten = 0;
			this.previousIndex = pieceMsg.index;
			this.torrentManager.currReqBits[this.previousIndex] = false;

			try {
				if (manager.UpdateFile(pieceMsg, hashes[pieceMsg.index], piece.toByteArray())) {
					totalDownload+= currentPieceLength;
					this.torrentManager.bits[pieceMsg.index] = true;
					piece = null;
					return true;
				} else {
					piece = null;
				}
			} catch (Exception e) {
				System.err.println("Error writing to file");
				piece = null;
			}
		}

		return false;
	}

	class Connection extends Thread {
		public Peer peer;
		public int interval = 120000;
		public boolean isRunning = false;

		Connection(Peer peer){
			this.peer = peer;
		}

		public void run(){
			while(this.isRunning){
				try {
					Thread.sleep(interval);
				} catch (InterruptedException e) {
					continue;
				}
				try {
					peer.send(new KeepAlive(0,(byte)255,peer));
				} catch (IOException e) {
					System.err.println("Error sending keepalive to peer: " + this.peer);
				}
			}
		}
	}

	@Override
	public String toString() {
		try {
			return "Peer{" +
                    "peerId=" + new String(peerId, "UTF-8") +
                    '}';
		} catch (UnsupportedEncodingException e) {
			System.err.println("Failed to print peerId\nEXCEPTION: " + e.getMessage());
			return e.getMessage();
		}
	}


}
