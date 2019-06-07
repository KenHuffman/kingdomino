package com.huffmancoding.kingdomino;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.util.resource.Resource;

public class KingdominoApplication
{
    public static void main(String[] args) throws Exception
    {
        Server server = new Server();
        // HTTP connector
        ServerConnector http = new ServerConnector(server);
        http.setHost("localhost");
        http.setPort(8080);
        http.setIdleTimeout(30000);

        // Set the connector
        server.addConnector(http);

        HandlerList handlers = new HandlerList();

        ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setBaseResource(Resource.newClassPathResource("static"));

        Game game = new Game();
        KingdominoHandler jsonHandler = new KingdominoHandler(game);

        handlers.setHandlers(new Handler[] { resourceHandler, jsonHandler, new DefaultHandler() });
        server.setHandler(handlers);

        server.start();

        System.out.println("Point your browser to: http://" + http.getHost() + ":" + http.getPort());
        server.join();
    }
}
