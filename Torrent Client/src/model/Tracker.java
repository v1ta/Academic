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

import action.TorrentManager;
import action.TrackerConnection;
import util.*;


public class Tracker implements TrackerConnection{

	public  URL trackerGETURL;
	public ServerSocket listenSocket;
	public TorrentInfo torrentInfo;
	TorrentClient torrentClient;
	int downloaded;
	int uploaded;
	public Swarm swarm;
	int interval;
	String event;
	int port;
	public Timer timer;
	public byte[] peerId;
	public final int requestSize = 16000;

	/**
	 * Constructs Tracker URL, sets listening port for this peer.
	 * @param torrentInfo
	 * @param peerId
	 */
	public Tracker(TorrentInfo torrentInfo, byte[] peerId, TorrentClient torrentClient){
		this.torrentInfo = torrentInfo;
		this.torrentClient = torrentClient;
		this.peerId = peerId;
		this.port = setListeningPort(6887, 6889);
		//trackerGETURL = constructURL(this.torrentInfo,  peerId , this.port );
	}

	/**
	 * Specify port range
	 * @param torrentInfo
	 * @param peerId
	 * @param portLowerBound
	 * @param portUpperBound
	 */
	public Tracker(TorrentInfo torrentInfo, byte[] peerId, TorrentClient torrentClient, int portLowerBound, int portUpperBound){
		this.torrentInfo = torrentInfo;
		this.torrentClient = torrentClient;
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

	public byte[] sendHTTPGET(URL trackerGETURL) throws IOException {
		HttpURLConnection httpConnection = (HttpURLConnection)trackerGETURL.openConnection();
		DataInputStream dataInputStream = new DataInputStream(httpConnection.getInputStream());
		byte[]  trackerResponse = new byte[httpConnection.getContentLength()];

		dataInputStream.readFully(trackerResponse);
		dataInputStream.close();

		return trackerResponse;
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

	public ArrayList<Peer> update(String event, TorrentClient torrentClient) throws IOException {
		this.event = event;

		if(this.event.equals("started")){
			try {
				torrentClient.getUpload();

			} catch (IOException e) {
				this.downloaded = 0;
				this.uploaded = 0;
			}
		} else {
			torrentClient.setDownloadUpload(this.downloaded, this.uploaded);
		}

		trackerGETURL = constructURL(torrentInfo, peerId , port);

		HashMap<ByteBuffer, Object> response = null;
		byte[] resData;
		try {
			resData = sendHTTPGET(trackerGETURL);

			if (resData == null) {
				System.err.println("Error communicating with tracker");
				return null;
			}
			response = (HashMap<ByteBuffer, Object>) Bencoder2.decode(resData);
		} catch (BencodingException e1) {
			System.err.println("Error decoding tracker response");
			return null;
		}

		if (response.containsKey(HashConstants.KEY_FAILURE)) {
			System.err.println("Failure from the tracker.");
			return null;
		}

		ArrayList<Peer> peers = new ArrayList<>();

		this.interval = (Integer)response.get(HashConstants.KEY_INTERVAL);

		List<Map<ByteBuffer, Object>> peersList = (List<Map<ByteBuffer, Object>>) response.get(HashConstants.KEY_PEERS);

		if (peersList == null) {
			System.err.println("List of peers given by tracker is null");
			return null;
		}

		for (Map<ByteBuffer, Object> rawPeer : peersList) {
			int peerPort = ((Integer) rawPeer.get(HashConstants.KEY_PORT)).intValue();
			byte[] peerId = ((ByteBuffer) rawPeer.get(HashConstants.KEY_PEERID)).array();
			String ip;

			try {
				ip = new String(((ByteBuffer) rawPeer.get(HashConstants.KEY_IP)).array(),
						"ASCII");
			} catch (UnsupportedEncodingException e) {
				System.err.println("Unable to parse encoding");
				continue;
			}

			peers.add(new Peer(peerId, peerPort, ip, this, torrentClient));
		}

		if(this.interval < 0){
			this.interval = 120000;
		}

		System.out.println("Converted: " + peers);
		return peers;
	}

	@Override
	public ArrayList<Peer> update(String event, TorrentManager torrentManager) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public byte[] getResponse() {
		// TODO Auto-generated method stub
		return null;
	}

}
