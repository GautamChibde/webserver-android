package com.chibde.webserver.model;

import java.io.Serializable;

/**
 * Created by gautam chibde on 27-May-17.
 */

public class Link implements Serializable {
    private static final long serialVersionUID = 7612342295622776147L;

    private String href;
    private String type;

    public Link(String href, String type) {
        this.href = href;
        this.type = type;
    }

    @Override
    public String toString() {
        return "Link{" +
                ", href='" + href + '\'' +
                ", type='" + type + '\'' +
                '}';
    }

    public String getHref() {
        return href;
    }

    public String getType() {
        return type;
    }
}