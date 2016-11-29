package kelgon.rosalite.console.listener;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import kelgon.rosalite.base.Mongo;
import kelgon.rosalite.console.util.ContextManager;

public class InitListener implements ServletContextListener {
	private static final Logger log = Logger.getLogger(InitListener.class);
	
	public void contextInitialized(ServletContextEvent ev) {
		try {
			PropertyConfigurator.configure(ev.getServletContext().getRealPath("/WEB-INF/classes/log4j.properties"));
			log.info("loading rosalite-mongo.properties...");
			InputStream in = new BufferedInputStream(new FileInputStream(ev.getServletContext()
					.getRealPath("/WEB-INF/classes/rosalite-mongo.properties")));
			Properties props = new Properties();
			props.load(in);
			if(Mongo.initMongo(props)) {
		        log.info("loading spring context...");
		        ContextManager.setContext(new ClassPathXmlApplicationContext("spring.xml"));
				log.info("rosalite-console successfully initialized");
			} else {
				log.error("initialize mongodb client failed");
			}
		} catch(Exception e) {
			log.error("error initializing rosalite-console", e);
		}
	}

	public void contextDestroyed(ServletContextEvent arg0) {
		
	}

}
