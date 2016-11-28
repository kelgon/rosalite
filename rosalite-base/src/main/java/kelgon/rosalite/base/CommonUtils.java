package kelgon.rosalite.base;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class CommonUtils {
	public static String getHostName() {
		try {
			return InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			return "";
		}
	}
	
	public static String getPid() {
	    String name = ManagementFactory.getRuntimeMXBean().getName();
	    return name.split("@")[0];
	}
}
