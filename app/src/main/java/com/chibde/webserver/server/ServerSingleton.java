package com.chibde.webserver.server;

/**
 * Created by gautam chibde on 27-May-17.
 */

public class ServerSingleton {
    private static WebServer webServerInstance;

    public static WebServer getWebServerInstance() {
        if (webServerInstance == null) {
            webServerInstance = new WebServer(8080);
        }
        return webServerInstance;
    }

    public static WebServer getServerInstance(int portNumber) {
        if (webServerInstance == null) {
            webServerInstance = new WebServer(portNumber);
        }
        return webServerInstance;
    }

    public static void resetServerInstance() {
        webServerInstance = null;
    }
}
