package view;

import model.Peer;
import model.TorrentManager;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import util.HashConstants;

/**
 * Logic visible to the user
 * @author Joseph
 *
 */
public class RUBTClient extends Thread{

	public static void main(String args[]){

		RUBTClient mainApp = new RUBTClient();
		mainApp.start();
		EventQueue.invokeLater(new Runnable() {
			public TorrentManager client = null;
			public void run() {

				try {
					client = new TorrentManager(new File(System.getProperty("user.dir")
							+ File.separator
							+ "data"
							+ File.separator
							+ "Phase1.torrent"));
				} catch (IOException e) {
					e.printStackTrace();
				}
				try {
					client.configure();
				} catch (IOException e) {
					e.printStackTrace();
				}

				client.bits = new boolean[client.tracker.torrentInfo.piece_hashes.length];
				Arrays.fill(client.bits, false);

				client.start();



		}
		});


	}

}
