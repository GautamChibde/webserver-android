package com.chibde.webserver.server;

import com.chibde.webserver.activity.MainActivity;
import com.chibde.webserver.model.ResourceLink;
import com.chibde.webserver.server.handler.ResourceHandler;

import fi.iki.elonen.router.RouterNanoHTTPD;

/**
 * Created by gautam chibde on 27-May-17.
 */

public class WebServer extends RouterNanoHTTPD {
    private static final String RESOURCE_HANDLE = "/(.*)";

    public WebServer(int portNo) {
        super(portNo);
    }

    public void addRoutes(MainActivity context, ResourceLink links) {
        addRoute(RESOURCE_HANDLE, ResourceHandler.class, context, links);
    }
}