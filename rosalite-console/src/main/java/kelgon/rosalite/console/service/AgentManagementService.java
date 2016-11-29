package kelgon.rosalite.console.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.bson.Document;

import com.alibaba.fastjson.JSON;
import com.mongodb.client.MongoCursor;

import kelgon.rosalite.base.Mongo;
import kelgon.rosalite.console.model.Agent;
import kelgon.rosalite.console.model.RetMsg;
import kelgon.rosalite.console.model.Tracker;

public class AgentManagementService {
	public String agentList(HttpServletRequest request) {
		MongoCursor<Document> cursor = Mongo.db().getCollection("agents")
				.find(new Document("removed", false)).iterator();
		List<Agent> agents = new ArrayList<Agent>();
		Date now = new Date();
		while(cursor.hasNext()) {
			Document ag = cursor.next();
			Agent agent = new Agent();
			agent.setId(ag.getObjectId("_id").toString());
			agent.setName(ag.getString("name"));
			agent.setHost(ag.getString("host"));
			agent.setPid(ag.getString("pid"));
			agent.setStatus(ag.getString("status"));
			agent.setRemoved(ag.getBoolean("removed"));
			agent.setLastHeartbeat(ag.getDate("lastHeartbeat"));
			agent.setLastHeartbeatDiff(now.getTime() - agent.getLastHeartbeat().getTime());
			MongoCursor<Document> tCursor = Mongo.db().getCollection("trackers")
					.find(new Document("agent", ag.getObjectId("_id"))).iterator();
			List<Tracker> trackers = new ArrayList<Tracker>();
			while(tCursor.hasNext()) {
				Document tk = tCursor.next();
				Tracker tracker = new Tracker();
				tracker.setId(tk.getObjectId("_id").toString());
				tracker.setAgentId(agent.getId());
				tracker.setLogpath(tk.getString("logpath"));
				tracker.setLogfile(tk.getString("logfile"));
				tracker.setParser(tk.getString("parser"));
				tracker.setNo(tk.getString("no"));
				tracker.setStatus(tk.getString("status"));
				tracker.setPos(tk.getLong("pos"));
				tracker.setLastModified(tk.getDate("lastModified"));
				tracker.setLastHeartbeat(tk.getDate("lastHeartbeat"));
				tracker.setLastModifiedDiff(now.getTime() - tracker.getLastModified().getTime());
				tracker.setLastHeartbeatDiff(now.getTime() - tracker.getLastHeartbeat().getTime());
				trackers.add(tracker);
			}
			agent.setTrackers(trackers);
			agents.add(agent);
		}
		return JSON.toJSONString(new RetMsg("0", "ok", agents));
	}
}