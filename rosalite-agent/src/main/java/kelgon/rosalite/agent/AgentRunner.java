package kelgon.rosalite.agent;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import kelgon.rosalite.base.CommonUtils;
import kelgon.rosalite.base.Mongo;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.bson.Document;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

import com.mongodb.client.MongoCursor;

public class AgentRunner {
	private static final Logger log = Logger.getLogger(AgentRunner.class);

	private static boolean initMongo() {
		log.info("loading rosalite-mongo.properties...");
		InputStream is = AgentRunner.class.getClassLoader().getResourceAsStream("rosalite-mongo.properties");
		Properties props = new Properties();
		try {
			props.load(is);
		} catch(IOException e) {
			log.error("error reading rosalite-mongo.properties", e);
			return false;
		}
		return Mongo.initMongo(props);
	}
	
	private static boolean initAgent() {
		try {
			log.info("loading rosalite-agent.properties...");
			InputStream is = AgentRunner.class.getClassLoader().getResourceAsStream("rosalite-agent.properties");
			Properties props = new Properties();
			props.load(is);
			String agentName = props.getProperty("agentName");
			log.info("agentName: "+agentName);
			log.info("verifying agent setting and status...");
			//find and verify agent setting from MongoDB
			Document agent = Mongo.db().getCollection("agents").find(new Document("name", agentName)).first();
			if("running".equals(agent.getString("status"))) {
				log.error("agent ["+agentName+"] is already running on host ["+agent.getString("host")+"], pid "
						+agent.getString("pid"));
				return false;
			}
			String hostname = CommonUtils.getHostName();
			if(!hostname.equals(agent.getString("host"))) {
				log.error("hostname doesn't match, current: "+hostname+", setting: "+agent.getString("host"));
				return false;
			}
			//find and verify tracker setting of this agent from MongoDB
			MongoCursor<Document> cursor = Mongo.db().getCollection("trackers").find(new Document("agent",
					agent.getObjectId("_id")).append("autorun", true)).iterator();
			if(!cursor.hasNext()) {
				log.error("cannot find any tracker config under agent ["+agentName+"]");
				return false;
			}
			Agent.settings = agent;
			List<Document> trackers = new ArrayList<Document>();
			while(cursor.hasNext()) {
				Document tracker = cursor.next();
				if(verifyTrackerSetting(tracker))
					trackers.add(tracker);
				else
					return false;
			}
			Agent.settings.append("trackers", trackers);
			
			log.info("init courier job...");
		    SchedulerFactory sf = new StdSchedulerFactory();
		    Agent.sched = sf.getScheduler();
		    JobDetail courierJob = JobBuilder.newJob(Courier.class).withIdentity("CourierJob", "group1").build();
		    CronTrigger courierTrigger = (CronTrigger)TriggerBuilder.newTrigger()
		    		.withIdentity("CourierTrigger", "group1")
		    		.withSchedule(CronScheduleBuilder.cronSchedule("0/10 * * * * ?")).build();
		    Agent.sched.scheduleJob(courierJob, courierTrigger);
		    
		    
			Mongo.db().getCollection("agents").updateOne(new Document("_id", Agent.settings.getObjectId("_id")), 
					new Document("$set", new Document("status", "running").append("pid", CommonUtils.getPid())));
			log.info("agent "+agentName+" successfully initialized");
			return true;
		} catch(Exception e) {
			log.error("error while initializing agent", e);
			return false;
		}
	}
	
	public static boolean verifyTrackerSetting(Document setting) {
		if(StringUtils.isBlank(setting.getString("logpath"))) {
			log.error("logpath of tracker["+setting.getObjectId("_id").toString()+"] is null or blank");
			return false;
		}
		if(StringUtils.isBlank(setting.getString("logfile"))) {
			log.error("logfile of tracker["+setting.getObjectId("_id").toString()+"] is null or blank");
			return false;
		}
		if(StringUtils.isBlank(setting.getString("parser"))) {
			log.error("parser of tracker["+setting.getObjectId("_id").toString()+"] is null or blank");
			return false;
		}
		try {
			Class.forName(setting.getString("parser"));
		} catch (ClassNotFoundException e) {
			log.error("parser class not found: " + setting.getString("parser"));
			return false;
		}
		return true;
	}
	
	@SuppressWarnings("unchecked")
	private static boolean startAgents() {
		List<Document> trackers = (List<Document>)Agent.settings.get("trackers");
		log.info("starting "+trackers.size()+" tracker threads...");
		Agent.trackers = new ConcurrentHashMap<String, LogTrackerThread>();
		for(Document setting : trackers) {
			LogTrackerThread lt = new LogTrackerThread(setting);
			lt.start();
			Agent.trackers.put(setting.getString("no"), lt);
		}
		try {
			Agent.sched.start();
		} catch (SchedulerException e) {
			log.error("error starting courier", e);
			return false;
		}
		return true;
	}
	
	public static void main(String[] args) {
		PropertyConfigurator.configure(AgentRunner.class.getClassLoader().getResource("log4j.properties"));
		log.info("initializing mongo client...");
		if(initMongo()) {
			log.info("initializing agent settings...");
			if(initAgent()) {
				log.info("starting agent threads...");
				if(startAgents()) {
					log.info("addShutdownHook...");
					Runtime.getRuntime().addShutdownHook(new ShutdownCleaner());
					log.info("rosalite-agent started");
					return;
				}
			}
		}
		log.error("failed to start rosalite-agent");
		System.exit(0);
	}
}
