package view;

import util.Bencoder2;
import util.BencodingException;
import util.ToolKit;
import util.TorrentInfo;
import model.Tracker;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import action.TrackerControl;

/**
 * Logic visible to the user
 * @author Joseph
 *
 */
public class Term {
	public static final ByteBuffer KEY_PEERS = ByteBuffer.wrap(new byte[] {'p', 'e', 'e', 'r', 's' });
	public static final ByteBuffer KEY_PEERID = ByteBuffer.wrap(new byte[] {'p', 'e', 'e', 'r', ' ', 'i', 'd' });
	public static final ByteBuffer KEY_IP = ByteBuffer.wrap(new byte[] { 'i','p' });
	public static final ByteBuffer KEY_PORT = ByteBuffer.wrap(new byte[] { 'p','o', 'r', 't' });
	public static void main(String args[]){
		TrackerControl tracker = new TrackerControl();
		Tracker.setTrackerData(tracker.trackerGET());
		System.out.println(Tracker.getTrackerGETURL().toString());
		System.out.println();
		
		/*
		String[] readable = printString(Tracker.getTrackerData(), true, 0).split(":");
		
		for(String str : readable){
			System.out.println(str);
		}
		*/
		//hi
		
		HashMap<ByteBuffer, Object> ableToRead = null;

		try {
			ableToRead = (HashMap<ByteBuffer, Object>) Bencoder2.decode(Tracker.getTrackerData());
		} catch (BencodingException e) {
		}
		
		//ToolKit.printMap(ableToRead,1);
		
		AbstractList<Map<ByteBuffer, Object>> peersList = (AbstractList<Map<ByteBuffer, Object>>) ableToRead.get(KEY_PEERS);
		
		ToolKit.printList(peersList, 1);
		
		for (Map<ByteBuffer, Object> rawPeer : peersList) {
			int peerPort = ((Integer) rawPeer.get(KEY_PORT)).intValue();
			byte[] peerId = ((ByteBuffer) rawPeer.get(KEY_PEERID)).array();
			String ip = null;
			try {
				ip = new String(((ByteBuffer) rawPeer.get(KEY_IP)).array(),"ASCII");
				System.out.println(ip);
			} catch (UnsupportedEncodingException e) {

			}

		}

	}
	
    public static String printString(byte[] bytes, boolean as_text, int depth)
    {
    	StringBuilder sb = new StringBuilder();
        for (int k = 0; k < depth; k++)
            System.out.print("  ");
        System.out.print("String: ");
        for (int i = 0; i < bytes.length; i++)
        {
        	/*
            System.out.print(as_text ? (char) bytes[i] : (int) bytes[i] + " ");
                    */
        	sb.append(as_text ? (char) bytes[i] : (int) bytes[i] + " ");
        }
        return sb.toString();
    }
    
    
}
