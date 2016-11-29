package kelgon.rosalite.console.util;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;

public class ContextManager {
	private static final Logger log = Logger.getLogger(ContextManager.class);
	
	private static ApplicationContext context;

	public static void setContext(ApplicationContext context) {
		ContextManager.context = context;
	}
	
	public static Object getBean(String beanName) {
		if(context == null) {
			log.fatal("spring context hasn't been initialized");
			return null;
		}
		return context.getBean(beanName);
	}
}
