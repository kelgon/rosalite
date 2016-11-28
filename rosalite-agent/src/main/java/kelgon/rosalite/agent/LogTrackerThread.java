package kelgon.rosalite.agent;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import kelgon.rosalite.base.Mongo;

import org.apache.log4j.Logger;
import org.bson.Document;

public class LogTrackerThread extends Thread {
	private static final Logger log = Logger.getLogger(LogTrackerThread.class);
	private Document setting;
	private boolean running = true;
	private long pos = 0L;
	private long lastModified = 0L;
	public LogTrackerThread(Document setting) {
		super();
		this.setting = setting;
		this.setName("Tracker-"+setting.getString("no"));
	}

	public void run() {
		log.info(getName() + " started");
		try {
			RandomAccessFile logfile = new RandomAccessFile(getFilename(), "r");
			//to detect log rotation
			File file = new File(getFilename());
			//last modified time
			lastModified = file.lastModified();
			pos = setting.getLong("pos");
			if(pos > 0 && pos <= logfile.length())
				logfile.seek(pos);
			Mongo.db().getCollection("trackers").updateOne(new Document("_id", setting.getObjectId("_id")), 
					new Document("$set", new Document("status", "running")));
			try {
				int eofCount = 0;
				while(running) {
					try {
						String line = logfile.readLine();
						if(line == null) {
							if(eofCount >= 10) {
								log.trace("no change in logfile for 20s, try to detect log rotation...");
								File newfile = new File(getFilename());
								if(newfile.lastModified() > lastModified + 20*1000) {
									log.info("log rotation detected, switching to new file...");
									logfile.close();
									logfile = new RandomAccessFile(getFilename(), "r");
									pos = 0L;
								} else {
									sleep(2000);
								}
							} else {
								eofCount++;
								log.trace("eof reached, sleeping 2s");
								sleep(2000);
							}
						} else {
							eofCount = 0;
							line = new String(line.getBytes("ISO-8859-1"), "UTF-8");
							pos = logfile.getFilePointer();
							lastModified = file.lastModified();
							log.trace("已读取：" + line);
						}
					} catch (IOException e) {
						log.error("", e);
					} catch (InterruptedException e) {
						log.error("", e);
					}
				}
			} finally {
				logfile.close();
			}
		} catch (FileNotFoundException e) {
			log.error("filenotfound", e);
		} catch (IOException e) {
			log.error("ioexception", e);
		} finally {
			log.info("reading stopped, sync status to central...");
			Mongo.db().getCollection("trackers").updateOne(new Document("_id", setting.getObjectId("_id")), 
					new Document("$set", new Document("status", "stopped").append("pos", pos)));
		}
	}
	
	private String getFilename() {
		String path = setting.getString("logpath");
		if(!path.endsWith("/"))
			path += "/";
		return path+setting.getString("logfile");
	}
	
	public void sigStop() {
		running = false;
	}

	public long getPos() {
		return pos;
	}

	public void setPos(long pos) {
		this.pos = pos;
	}

	public long getLastModified() {
		return lastModified;
	}

	public void setLastModified(long lastModified) {
		this.lastModified = lastModified;
	}

	public Document getSetting() {
		return setting;
	}

	public void setSetting(Document setting) {
		this.setting = setting;
	}
	
}
