package kelgon.rosalite.agent;

import java.util.concurrent.ConcurrentHashMap;

import org.bson.Document;
import org.quartz.Scheduler;

public class Agent {
	public static Document settings;
	public static ConcurrentHashMap<String, LogTrackerThread> trackers;
	public static Scheduler sched;
}
