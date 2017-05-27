package com.chibde.webserver.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;

import com.chibde.webserver.R;
import com.chibde.webserver.model.Link;
import com.chibde.webserver.model.ResourceLink;
import com.chibde.webserver.server.ServerSingleton;
import com.chibde.webserver.server.WebServer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gautam chibde on 27-May-17.
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private WebServer webServer;
    public List<Link> links = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addServerResources();
        setServer();
        WebView webView = (WebView) findViewById(R.id.web_view);
        webView.loadUrl("http://127.0.0.1:8080/portfolio/index.html");
    }

    private void setServer() {
        try {
            webServer = ServerSingleton.getServerInstance(8080);
            webServer.start();
            webServer.addRoutes(this, new ResourceLink(links));
        } catch (IOException e) {
            Log.i("MainActivity", "error " + e);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (webServer != null && webServer.isAlive()) {
            webServer.stop();
            ServerSingleton.resetServerInstance();
        }
        Log.d(TAG, "Server has been stopped");
    }

    private void addServerResources() {
        //html
        links.add(new Link("portfolio/index.html", "text/html"));

        //css
        links.add(new Link("portfolio/css/grid.css", "text/css"));
        links.add(new Link("portfolio/css/main.css", "text/css"));

        //resource elements
        links.add(new Link("portfolio/images/large-kitty.jpeg", "image/jpg"));
        links.add(new Link("portfolio/images/profile.jpg", "image/jpg"));
        links.add(new Link("portfolio/images/work-two.jpeg", "image/jpg"));
        links.add(new Link("portfolio/images/mini-kitty.jpeg", "image/jpg"));
        links.add(new Link("portfolio/images/small-kitty.jpeg", "image/jpg"));
        links.add(new Link("portfolio/images/udacity-logo.jpg", "image/jpg"));
        links.add(new Link("portfolio/images/work-five.jpeg", "image/jpg"));
        links.add(new Link("portfolio/images/work-four.jpeg", "image/jpg"));
        links.add(new Link("portfolio/images/work-one.jpeg", "image/jpg"));
        links.add(new Link("portfolio/images/work-six.jpeg", "image/jpg"));
        links.add(new Link("portfolio/images/work-three.jpeg", "image/jpg"));
    }
}
