package kelgon.rosalite.console.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;

import kelgon.rosalite.console.model.RetMsg;
import kelgon.rosalite.console.util.ContextManager;

public class MainServlet extends HttpServlet {
	private static final long serialVersionUID = 4091109290174870814L;
	private static final Logger log = Logger.getLogger(MainServlet.class);
	
	public void service(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setContentType("text/json;charset=UTF-8");
		PrintWriter out = response.getWriter();
		String beanName = request.getParameter("bn");
		String method = request.getParameter("mn");
		String retJson;
		try {
			Object bean = ContextManager.getBean(beanName);
			Class<?> serviceClass = bean.getClass();
			Method m = serviceClass.getMethod(method, HttpServletRequest.class);
			retJson = (String)m.invoke(bean, request);
		} catch(Exception e) {
			retJson = JSON.toJSONString(new RetMsg("-1", e.toString()));
			log.error("", e);
		}
		out.print(retJson);
	}

}
