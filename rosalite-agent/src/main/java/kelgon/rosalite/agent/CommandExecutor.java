package kelgon.rosalite.agent;

import kelgon.rosalite.base.Mongo;

import org.apache.log4j.Logger;
import org.bson.Document;

public class CommandExecutor extends Thread {
	private static final Logger log = Logger.getLogger(CommandExecutor.class);
	
	public CommandExecutor(Document command) {
		this.command = command;
		setName(command.getObjectId("_id").toString());
	}
	
	private Document command;

	public void run() {
		if("stopTracker".equals(command.getString("cType"))) {
			String trackerNo = command.getString("target");
			log.info("stopping tracker "+trackerNo+"...");
			LogTrackerThread lt = Agent.trackers.get(trackerNo);
			String msg;
			String result;
			if(lt == null) {
				msg = "tracker no "+trackerNo+" doesn't exist";
				result = "failed";
				log.info(msg);
			}
			else {
				lt.sigStop();
				while(true) {
					if(State.TERMINATED.equals(lt.getState()))
						break;
					else {
						try {
							sleep(100);
						} catch (InterruptedException e) {}
					}
				}
				msg = "tracker no "+trackerNo+" stopped";
				result = "complete";
			}
			Mongo.db().getCollection("commands").updateOne(new Document("_id", command.getObjectId("_id")),
					new Document("$set", new Document("status", result).append("msg", msg)));
		}
		if("stopTracker".equals(command.getString("cType"))) {
			String trackerNo = command.getString("target");
			log.info("starting tracker "+trackerNo+"...");
			LogTrackerThread lt = Agent.trackers.get(trackerNo);
			String msg;
			String result;
			if(lt == null || State.TERMINATED.equals(lt.getState())) {
				Document tracker = Mongo.db().getCollection("trackers").find(new Document("agent", 
						Agent.settings.getObjectId("_id")).append("no", trackerNo)).first();
				if(tracker == null) {
					msg = "cannot find setting of tracker "+trackerNo;
					result = "failed";
					log.info(msg);
				} else {
					if(AgentRunner.verifyTrackerSetting(tracker)) {
						lt = new LogTrackerThread(tracker);
						lt.start();
						Agent.trackers.put(trackerNo, lt);
						msg = "tracker no "+trackerNo+" started";
						result = "complete";
						log.info(msg);
					} else {
						msg = "tracker no "+trackerNo+" setting is not valid";
						result = "failed";
						log.info(msg);
					}
				}
			} else {
				msg = "tracker no "+trackerNo+" is already running";
				result = "failed";
			}
			Mongo.db().getCollection("commands").updateOne(new Document("_id", command.getObjectId("_id")),
					new Document("$set", new Document("status", result).append("msg", msg)));
		}
 	}
}
