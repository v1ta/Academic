package view;

import model.Tracker;
import action.TrackerControl;

/**
 * Logic visible to the user
 * @author Joseph
 *
 */
public class Term {
	
	public static void main(String args[]){
		TrackerControl tracker = new TrackerControl();
		Tracker.setTrackerData(tracker.trackerGET());
	}
}
