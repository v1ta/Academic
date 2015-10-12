package model;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.*;

import action.TrackerConnection;
import util.*;


public class Tracker implements TrackerConnection{

	public  URL trackerGETURL;
	ServerSocket listenSocket;
	public TorrentInfo torrentInfo;
	TorrentManager torrentManager;
	int downloaded;
	int uploaded;
	int interval;
	String event;
	int port;
	public Timer timer;
	byte[] response;
	byte[] peerId;
	public final int requestSize = 16000;

	/**
	 * Constructs Tracker URL, sets listening port for this peer.
	 * @param torrentInfo
	 * @param peerId
	 */
	public Tracker(TorrentInfo torrentInfo, byte[] peerId, TorrentManager torrentManager){
		this.torrentInfo = torrentInfo;
		this.torrentManager = torrentManager;
		this.peerId = peerId;
		this.port = setListeningPort(6883, 6889);
		trackerGETURL = constructURL(this.torrentInfo,  peerId , this.port );
	}

	/**
	 * Specify port range
	 * @param torrentInfo
	 * @param peerId
	 * @param portLowerBound
	 * @param portUpperBound
	 */
	public Tracker(TorrentInfo torrentInfo, byte[] peerId, TorrentManager torrentManager, int portLowerBound, int portUpperBound){
		this.torrentInfo = torrentInfo;
		this.torrentManager = torrentManager;
		this.peerId = peerId;
		trackerGETURL = constructURL(this.torrentInfo, peerId , setListeningPort(portLowerBound, portUpperBound) );
	}

	public int setListeningPort(int lowerBound, int upperBound) {
		for (int i = lowerBound; i <= upperBound; i++) {
			try {
				this.listenSocket = new ServerSocket(i);
				return i;
			} catch (IOException e) {
				System.err.println("ERROR: Failed to bind port: " + i + " \nException: "
						+ e.getMessage());
			}
		}
		System.err.println("ERROR: No ports available");
		return -1;
	}

	public void sendHTTPGET(URL trackerGETURL) throws IOException {
		HttpURLConnection httpConnection = (HttpURLConnection)trackerGETURL.openConnection();
		DataInputStream dataInputStream = new DataInputStream(httpConnection.getInputStream());

		this.response = new byte[httpConnection.getContentLength()];

		dataInputStream.readFully(response);
		dataInputStream.close();
	}

	 public URL constructURL(TorrentInfo torrentInfo, byte[] peerId, int port){
		StringBuilder URL = new StringBuilder();
		URL.append(torrentInfo.announce_url.toString());
		URL.append("?info_hash=");
		URL.append(HashSequenceHelper.toHexString(torrentInfo.info_hash.array()));
		URL.append("&peer_id=");
		URL.append(HashSequenceHelper.toHexString(peerId));
		URL.append("&port=");
		URL.append(Integer.toString(port));
		URL.append("&uploaded=");
		URL.append(uploaded);
		URL.append("&downloaded=");
		URL.append(downloaded);
		URL.append("&left=");
		URL.append(torrentInfo.file_length);

		try {
			return new URL(URL.toString());
		} catch (MalformedURLException e) {
			System.err.println("ERROR: Failed to generated Tracker GET code\nException: "
					+ e.getMessage());
			return null;
		}

	}

	public ArrayList<Peer> update(String event, TorrentManager torrentManager) throws IOException {
		this.event = event;

		if(this.event.equals("started")){
			try {
				torrentManager.getUpload();

			} catch (IOException e) {
				this.downloaded = 0;
				this.uploaded = 0;
			}
		} else {
			torrentManager.setDownloadUpload(this.downloaded, this.uploaded);
		}

		URL request = constructURL(torrentInfo, peerId , setListeningPort(6881, 6889));

		HashMap<ByteBuffer, Object> response = null;

		try {
			sendHTTPGET(request);

			if (response == null) {
				System.err.println("Error communicating with tracker");
				return null;
			}
			response = (HashMap<ByteBuffer, Object>) Bencoder2.decode(this.response);
		} catch (BencodingException e1) {
			System.err.println("Error decoding tracker response");
			return null;
		}

		if (response.containsKey(HashConstants.KEY_FAILURE)) {
			System.err.println("Failure from the tracker.");
			return null;
		}

		ArrayList<Peer> peers = new ArrayList<Peer>();

		this.interval = (Integer)response.get(HashConstants.KEY_INTERVAL);

		List<Map<ByteBuffer, Object>> peersList = (List<Map<ByteBuffer, Object>>) response
				.get(HashConstants.KEY_PEERS);

		if (peersList == null) {
			System.err.println("List of peers given by tracker is null");
			return null;
		}

		for (Map<ByteBuffer, Object> rawPeer : peersList) {
			int peerPort = ((Integer) rawPeer.get(HashConstants.KEY_PORT)).intValue();
			byte[] peerId = ((ByteBuffer) rawPeer.get(HashConstants.KEY_PEERID)).array();
			String ip = null;
			try {
				ip = new String(((ByteBuffer) rawPeer.get(HashConstants.KEY_IP)).array(),
						"ASCII");
			} catch (UnsupportedEncodingException e) {
				System.err.println("Unable to parse encoding");
				continue;
			}

			peers.add(new Peer(peerId, peerPort, ip, this, torrentManager));
		}

		if(this.interval < 0){
			this.interval = 120000;
		}

		System.out.println("Converted: " + peers);
		return peers;
	}

	public byte[] getResponse() {
		return response;
	}
}
