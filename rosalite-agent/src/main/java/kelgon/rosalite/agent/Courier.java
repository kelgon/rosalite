package kelgon.rosalite.agent;

import java.lang.Thread.State;
import java.util.Date;
import java.util.Iterator;
import java.util.Map.Entry;

import kelgon.rosalite.base.Mongo;

import org.apache.log4j.Logger;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.mongodb.client.MongoCursor;

public class Courier implements Job {
	private static final Logger log = Logger.getLogger(Courier.class);
	
	public void execute(JobExecutionContext context) throws JobExecutionException {
		ObjectId agentId = Agent.settings.getObjectId("_id");
		log.info("fetching central command...");
		MongoCursor<Document> cursor = Mongo.db().getCollection("commands").find(
				new Document("agent", agentId).append("status", "new")).iterator();
		while(cursor.hasNext()) {
			Document command = cursor.next();
			log.info("command received: "+command.getString("command")+" "+command.getString("target"));
			Mongo.db().getCollection("commands").updateOne(new Document("_id", command.getObjectId("_id")), 
					new Document("$set", new Document("status", "acquired").append("acqDate", new Date())));
			log.info("assigning command execution thread...");
			carryOutCommand(command);
		}

		log.info("updating trackers status...");
		Iterator<Entry<String, LogTrackerThread>> it = Agent.trackers.entrySet().iterator();
		Date date = new Date();
		while(it.hasNext()) {
			LogTrackerThread lt = it.next().getValue();
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
				new Document("lastHeartbeat", date)));
	}

	
	private boolean carryOutCommand(Document command) {
		CommandExecutor ce = new CommandExecutor(command);
		ce.start();
		return true;
	}
}
