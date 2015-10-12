package view;

import model.Peer;
import model.TorrentManager;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import util.HashConstants;

/**
 * Logic visible to the user
 * @author Joseph
 *
 */
public class RUBTClient implements Runnable{

	protected  boolean running = false;


	public static void main(String args[]){

		/* Parse torrent metadata, setup comms w/tracker */
		TorrentManager client = new TorrentManager(new File(System.getProperty("user.dir")
				+ File.separator
				+ "data"
				+ File.separator
				+ "Phase1.torrent"));



		File outputFile = new File("352FA2015FinalAnswers.mov");

		try {
			outputFile.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}

		client.bits = new boolean[client.tracker.torrentInfo.piece_hashes.length];
		Arrays.fill(client.bits, false);


		Peer peerPhaseOne = null;
		/* Extract the peer the assignment calls for  */
		for(Peer peer : client.listPeers(client.tracker.getResponse())){
			byte[] id = new byte[6];
			System.arraycopy(peer.peerId, 0, id, 0, 6);
			if (Arrays.equals(id, HashConstants.PEER_ID_PHASE_ONE))
				peerPhaseOne = peer;
		}

		/* connect to the peer */
		if (!peerPhaseOne.connect()){
			System.err.println("failed to connet");
			return;
		}


		/* Run client until file is complete*/
		while(!client.isFileComplete()){}

		System.out.println("file complete?");

	}

	@Override
	public void run() {
		while(running){
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println("Downloading File");
		}
	}
}
