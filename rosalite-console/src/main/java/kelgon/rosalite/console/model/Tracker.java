package kelgon.rosalite.console.model;

import java.util.Date;

public class Tracker {
	private String id;
	private String agentId;
	private String logpath;
	private String logfile;
	private String parser;
	private String status;
	private long pos;
	private String no;
	private Date lastModified;
	private Date lastHeartbeat;
	private long lastModifiedDiff;
	private long lastHeartbeatDiff;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getAgentId() {
		return agentId;
	}
	public void setAgentId(String agentId) {
		this.agentId = agentId;
	}
	public String getLogpath() {
		return logpath;
	}
	public void setLogpath(String logpath) {
		this.logpath = logpath;
	}
	public String getLogfile() {
		return logfile;
	}
	public void setLogfile(String logfile) {
		this.logfile = logfile;
	}
	public String getParser() {
		return parser;
	}
	public void setParser(String parser) {
		this.parser = parser;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public long getPos() {
		return pos;
	}
	public void setPos(long pos) {
		this.pos = pos;
	}
	public String getNo() {
		return no;
	}
	public void setNo(String no) {
		this.no = no;
	}
	public Date getLastModified() {
		return lastModified;
	}
	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}
	public Date getLastHeartbeat() {
		return lastHeartbeat;
	}
	public void setLastHeartbeat(Date lastHeartbeat) {
		this.lastHeartbeat = lastHeartbeat;
	}
	public long getLastModifiedDiff() {
		return lastModifiedDiff;
	}
	public void setLastModifiedDiff(long lastModifiedDiff) {
		this.lastModifiedDiff = lastModifiedDiff;
	}
	public long getLastHeartbeatDiff() {
		return lastHeartbeatDiff;
	}
	public void setLastHeartbeatDiff(long lastHeartbeatDiff) {
		this.lastHeartbeatDiff = lastHeartbeatDiff;
	}
}
