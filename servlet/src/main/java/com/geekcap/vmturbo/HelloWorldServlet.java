package com.geekcap.vmturbo;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.*;
import javax.servlet.http.*;

import com.illucit.ejbremote.server.ExampleService;

import static javax.naming.Context.INITIAL_CONTEXT_FACTORY;
import static javax.naming.Context.PROVIDER_URL;
import static javax.naming.Context.URL_PKG_PREFIXES;

import java.io.*;
import java.util.Hashtable;
import java.util.Properties;

public class HelloWorldServlet extends HttpServlet 
{

  private static final long serialVersionUID = -5261451817942565406L;

  
  public static Properties getProp(ServletContext servletContext) throws IOException {
		Properties props = new Properties();
		props.load(servletContext.getResourceAsStream("/resources/jboss-ejb-client.properties"));
		return props;
  }
  
  private static Context createRemoteEjbContext(ServletContext servletContext) throws NamingException, IOException {

		Properties prop = getProp(servletContext);
		Hashtable<Object, Object> props = new Hashtable<>();

		props.put(INITIAL_CONTEXT_FACTORY, "org.jboss.naming.remote.client.InitialContextFactory");
		props.put(URL_PKG_PREFIXES, "org.jboss.ejb.client.naming");

		props.put("jboss.naming.client.ejb.context", false);
		props.put("org.jboss.ejb.client.scoped.context", true);

		props.put("endpoint.name", "client-endpoint");
		props.put("remote.connectionprovider.create.options.org.xnio.Options.SSL_ENABLED", false);
		props.put("remote.connections", "default");
		props.put("remote.connection.default.connect.options.org.xnio.Options.SASL_POLICY_NOANONYMOUS", false);

		props.put(PROVIDER_URL, "http-remoting://"+prop.getProperty("host")+":"+prop.getProperty("port")+"");
		props.put("remote.connection.default.host", prop.getProperty("host"));
		props.put("remote.connection.default.port", prop.getProperty("port"));
		props.put("remote.connection.default.username", "cyborg");
		props.put("remote.connection.default.password", "c6b94gmg");

		return new InitialContext(props);
  }
	
  public String getEjbHello(String name, ServletContext servletContext) throws IOException{

	  try {
			
		  ExampleService service = (ExampleService) createRemoteEjbContext(servletContext).lookup("ejb:/ejb-remote-server/ExampleServiceImpl!com.illucit.ejbremote.server.ExampleService");
		  
		  return service.greet(name) ;
			
		} catch (NamingException e) {
			e.printStackTrace();
			return "FALHA!!";
		}
	  
  }
	
  public void service( HttpServletRequest req, HttpServletResponse res ) throws IOException {
	  res.setContentType("text/html;charset=UTF-8");
      PrintWriter out = res.getWriter();
      String nome = req.getParameter("nome");

      
      
      try {
          /* TODO output your page here. You may use following sample code. */
          out.println("<!DOCTYPE html>");
          out.println("<html>");
          out.println("<head>");
          out.println("<title>Servlet HelloWorldServlet</title>");            
          out.println("</head>");
          out.println("<body>");
          out.println("<h1>" + getEjbHello(nome, getServletContext()) + "</h1>");
          out.println("</body>");
          out.println("</html>");
      } finally {            
          out.close();
      }
  }
}
