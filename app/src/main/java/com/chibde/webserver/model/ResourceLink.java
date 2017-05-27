package com.chibde.webserver.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by gautam chibde on 27-May-17.
 */

public class ResourceLink implements Serializable {
    private static final long serialVersionUID = -2741309398659001263L;

    private List<Link> links;

    public ResourceLink(List<Link> links) {
        this.links = links;
    }

    @Override
    public String toString() {
        return "ResourceLink{" +
                "links=" + links +
                '}';
    }

    public List<Link> getLinks() {
        return links;
    }
}
