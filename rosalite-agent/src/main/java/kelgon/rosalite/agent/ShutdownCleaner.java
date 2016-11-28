package kelgon.rosalite.agent;

import org.apache.log4j.Logger;
import org.bson.Document;

import kelgon.rosalite.base.Mongo;

public class ShutdownCleaner extends Thread {
	private static final Logger log = Logger.getLogger(ShutdownCleaner.class);
	
	public ShutdownCleaner() {
		super();
		this.setName("CleanerThread");
	}
	
	public void run() {
		try {
			log.info("shutting down agent...");
			log.info("stopping LogTrackerThreads...");
			for(LogTrackerThread lt : Agent.trackers) {
				lt.sigStop();
			}
			while(true) {
				try {
					sleep(500);
				} catch (InterruptedException e) {}
				boolean all = true;
				for(LogTrackerThread lt : Agent.trackers) {
					if(!State.TERMINATED.equals(lt.getState())) {
						all = false;
						break;
					}
				}
				if(all)
					break;
			}
		} finally {
			log.info("updating agent status to [stopped]...");
			Mongo.db().getCollection("agents").updateOne(new Document("_id", Agent.settings.getObjectId("_id")), 
					new Document("$set", new Document("status", "stopped")));
		}
		log.info("agent stopped");
	}
}
