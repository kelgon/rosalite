package kelgon.rosalite.console.model;

import java.util.Date;
import java.util.List;

public class Agent {
	private String id;
	private String name;
	private String host;
	private String pid;
	private String status;
	private boolean removed;
	private Date lastHeartbeat;
	private long lastHeartbeatDiff;
	private List<Tracker> trackers;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public String getPid() {
		return pid;
	}
	public void setPid(String pid) {
		this.pid = pid;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public boolean isRemoved() {
		return removed;
	}
	public void setRemoved(boolean removed) {
		this.removed = removed;
	}
	public Date getLastHeartbeat() {
		return lastHeartbeat;
	}
	public void setLastHeartbeat(Date lastHeartbeat) {
		this.lastHeartbeat = lastHeartbeat;
	}
	public long getLastHeartbeatDiff() {
		return lastHeartbeatDiff;
	}
	public void setLastHeartbeatDiff(long lastHeartbeatDiff) {
		this.lastHeartbeatDiff = lastHeartbeatDiff;
	}
	public List<Tracker> getTrackers() {
		return trackers;
	}
	public void setTrackers(List<Tracker> trackers) {
		this.trackers = trackers;
	}
}
