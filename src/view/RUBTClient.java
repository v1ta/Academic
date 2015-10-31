package view;

import model.TorrentClient;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;


/**
 * Logic visible to the user
 * @author Joseph
 *
 */
public class RUBTClient extends Thread{

	public static void main(String args[]){

		final Runnable runnable = new Runnable() {
			public TorrentClient client = null;

			public void run() {
				try {
					client = new TorrentClient(new File(System.getProperty("user.dir")
							+ File.separator
							+ "data"
							+ File.separator
							+ "Phase1.torrent"));
				} catch (IOException e) {
					e.printStackTrace();
				}
				try {
					client.initialize();
				} catch (IOException e) {
					e.printStackTrace();
				}
				client.bits = new boolean[client.tracker.torrentInfo.piece_hashes.length];
				Arrays.fill(client.bits, false);
				client.isRunning = true;
				client.start();
			}
		};
		Thread t = new Thread(runnable);

		t.start();
	}
}
