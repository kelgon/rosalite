package kelgon.rosalite.agent;

import java.lang.Thread.State;
import java.util.Date;
import java.util.Iterator;

import kelgon.rosalite.base.Mongo;

import org.apache.log4j.Logger;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class Courier implements Job {
	private static final Logger log = Logger.getLogger(Courier.class);
	
	public void execute(JobExecutionContext context) throws JobExecutionException {
		ObjectId agentId = Agent.settings.getObjectId("_id");
		log.info("fetching central command...");
		Document command = Mongo.db().getCollection("commands").find(new Document("agent",agentId)
			.append("status", "sent")).first();
		if(command != null) {
			log.info("command received: "+command.getString("command")+command.getString("target"));
			Mongo.db().getCollection("commands").updateOne(new Document("_id", command.getObjectId("_id")), 
					new Document("$set", new Document("status", "acquired").append("acqDate", new Date())));
			log.info("assigning command execution thread...");
			carryOutCommand(command);
		} else {
			log.info("no new command");
		}

		log.info("updating trackers status...");
		Iterator<LogTrackerThread> it = Agent.trackers.iterator();
		Date date = new Date();
		while(it.hasNext()) {
			LogTrackerThread lt = it.next();
			if(lt.getState().equals(State.TERMINATED)) {
				log.warn("tracker ["+lt.getName()+"] is already terminated, remove thread");
				it.remove();
			} else {
				Mongo.db().getCollection("trackers").updateOne(
						new Document("_id", lt.getSetting().getObjectId("_id")),
						new Document("$set", new Document("pos", lt.getPos())
						.append("lastModified", new Date(lt.getLastModified()))
						.append("lastHeartbeat", date)));
			}
		}
		
		log.info("sending agent headbeat...");
		Mongo.db().getCollection("agents").updateOne(new Document("_id", agentId), new Document("$set",
				new Document("lastHeadbeat", date)));
	}

	
	private boolean carryOutCommand(Document command) {
		return true;
	}
}
