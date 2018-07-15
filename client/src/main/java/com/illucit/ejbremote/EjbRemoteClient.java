package com.illucit.ejbremote;

import static javax.naming.Context.INITIAL_CONTEXT_FACTORY;
import static javax.naming.Context.PROVIDER_URL;
import static javax.naming.Context.URL_PKG_PREFIXES;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.Security;
import java.util.Hashtable;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.jboss.sasl.JBossSaslProvider;

import com.illucit.ejbremote.server.ExampleService;

public class EjbRemoteClient {

	public static void main(String[] args) throws IOException {

		try {
			
			ExampleService service = (ExampleService) createRemoteEjbContext().lookup("ejb:/ejb-remote-server/ExampleServiceImpl!com.illucit.ejbremote.server.ExampleService");
			
			System.out.println( service.greet("Rodrigo") );
			
		} catch (NamingException e) {
			e.printStackTrace();
		}

	}
	
	public static Properties getProp() throws IOException {
		Properties props = new Properties();
		FileInputStream file = new FileInputStream("jboss-ejb-client.properties");
		props.load(file);
		return props;
	}

	private static Context createRemoteEjbContext() throws NamingException, IOException {

		Properties prop = getProp();
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

}
