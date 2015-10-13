package model;

import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Arrays;
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
	public boolean[] bitfield;
	String host;
	boolean[] choke =  {true, true}; // {we are choking, peer are choking}
	boolean[] interest = {false, false}; //{we are interested, they are interested}
	protected InputStream in;
	protected OutputStream out;
	Connection connection;
	public int previousIndex = -1;
	private int currentByteOffset = 0;
	public long totalDownload =0L;
	boolean isRunning = true;

	ByteArrayOutputStream piece = null;
	int currentPieceIndex = -1;
	int totalBytesWritten = 0;
	LinkedBlockingQueue<Request> requestQueue = new LinkedBlockingQueue<>();

	public Peer(byte[] peerId, int port, String host, Tracker tracker, TorrentManager torrentManager) {
		super("Peer@" + host + ":" + port);
		this.peerId = peerId;
		this.port = port;
		this.host = host;
		this.tracker = tracker;
		this.torrentManager = torrentManager;
		this.connection = new Connection(this);
		this.bitfield = new boolean[tracker.torrentInfo.piece_hashes.length];
		Arrays.fill(this.bitfield, false);
	}

	public boolean connect() {

		byte[] id = new byte[4];
		System.arraycopy(this.peerId, 0, id, 0, 4);

		if (!Arrays.equals(id, HashConstants.PEER_ID_PHASE_ONE)){
			System.err.println("wrong id");
			return false;
		}

		try {

			createSocket();

			DataOutputStream os = new DataOutputStream(this.out);
			DataInputStream is = new DataInputStream(this.in);

			if (is == null || os == null) {
				System.err.println("Unable to create stream to peer");
				this.disconnect();
				return false;
			}

			os.write(handshake(peerId, tracker.torrentInfo.info_hash.array()));
			os.flush();

			byte[] response = new byte[68];

			this.socket.setSoTimeout(10000);
			is.readFully(response);
			this.socket.setSoTimeout(130000);
			if (!confirmHandshake(tracker.torrentInfo.info_hash.array(), response)) {
				System.err.println("handshake failed");
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

	/**
	 * Thread safe socket creation
	 * @throws IOException
	 */
	public synchronized void createSocket() throws IOException {
		this.socket = new Socket(this.host, this.port);
		this.in = this.socket.getInputStream();
		this.out = this.socket.getOutputStream();
	}

	/**
	 * Handshake with peer
	 * @param peer
	 * @param infohash
	 * @return
	 */
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
	public synchronized void disconnect() {
		if(this.choke[0] == false){
			this.torrentManager.curUnchoked -= 1;
		}
		if (this.socket != null) {
			this.connection.isRunning = false;
			this.connection.interrupt();
		}

		try {
			if(this.socket!= null)
				this.socket.close();

		} catch (IOException e) {
			System.err.println("Error closing socket peer");

		} finally {
			this.socket = null;
			this.in = null;
			this.out = null;
			this.torrentManager.peers.remove(this);
			isRunning = false;

		}

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

	public synchronized void send(Message msg) throws IOException {
		if (this.out == null) {
			throw new IOException(this
					+ " cannot send a message on an empty socket.");
		}
		System.err.println("Sending " + msg + " to " + this);
		Message.encode(msg, out);
	}

	public Request getRequest() {
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

	public boolean appendAndVerify(Piece pieceMsg, ByteBuffer[] hashes, TorrentManager manager) {

		int currentPieceLength = (pieceMsg.index == (this.tracker.torrentInfo.piece_hashes.length - 1)) ?
				this.tracker.torrentInfo.file_length % this.tracker.torrentInfo.piece_length : this.tracker.torrentInfo.piece_length;

		if (this.piece == null) {
			this.piece = new ByteArrayOutputStream();
		}

		try {
			piece.write(pieceMsg.block, 0, pieceMsg.block.length);
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


	public String toString() {
		return new String(peerId) + " " + port + " " + host;
	}



}
