package hello;


import org.eclipse.jetty.http.MimeTypes;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.*;
import org.joda.time.LocalTime;

import drivers.HelloDriver;
import servlets.HelloServlet;
import servlets.IndexServlet;

import javax.servlet.Servlet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Main {


    public static void main(String[] args) throws Exception {
        LocalTime currentTime = new LocalTime();
        System.out.println("The current local time is: " + currentTime);

        Greeter greeter = new Greeter();
        System.out.println(greeter.sayHello());

        Server server = new Server(8080);
        HelloServlet servlet = new HelloServlet();
        IndexServlet index = new IndexServlet();
        ServletContextHandler context =
                new ServletContextHandler(ServletContextHandler.SESSIONS);


        context.addServlet(new ServletHolder(servlet), "/testpage");
        context.addServlet(new ServletHolder(index), "/index");
        context.addServlet(new ServletHolder(index), "/");


        try ( HelloDriver hello = new HelloDriver( "bolt://deadlyshine.ml:7687", "neo4j", "h1tlerTRACE" ) )
        {
            hello.printGreeting( "hello, world" );
        }



        HashMap<String, String> resources = new HashMap<>();

        resources.put("./static/css", "/css");
        resources.put("./static/html", "/html");
        resources.put("./static/js", "/js");

        HandlerList handlers = new HandlerList();

        for (String i : resources.keySet()) {

            ContextHandler resContext = new ContextHandler(resources.get(i));
            ResourceHandler resHandler = new ResourceHandler();
            resHandler.setDirectoriesListed(false);
            resHandler.setResourceBase(i);
            resContext.setHandler(resHandler);
            handlers.addHandler(resContext);
        }

        handlers.addHandler(context);

        //handlers.setHandlers(new Handler[] { handlers, context, new DefaultHandler() });

        server.setHandler(handlers);


        server.start();
        server.join();

    }
}