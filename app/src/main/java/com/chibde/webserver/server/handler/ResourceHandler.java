package com.chibde.webserver.server.handler;

import android.util.Log;

import com.chibde.webserver.activity.MainActivity;
import com.chibde.webserver.model.Link;
import com.chibde.webserver.model.ResourceLink;
import com.chibde.webserver.server.ResponseStatus;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;
import fi.iki.elonen.NanoHTTPD.Response.IStatus;
import fi.iki.elonen.NanoHTTPD.Response.Status;
import fi.iki.elonen.router.RouterNanoHTTPD.DefaultHandler;
import fi.iki.elonen.router.RouterNanoHTTPD.UriResource;

import static fi.iki.elonen.NanoHTTPD.MIME_PLAINTEXT;

/**
 * Created by gautam chibde on 27-May-17.
 */

public class ResourceHandler extends DefaultHandler {
    private static final String TAG = ResourceHandler.class.getSimpleName();

    public ResourceHandler() {
    }

    @Override
    public String getMimeType() {
        return "application/json";
    }

    @Override
    public String getText() {
        return ResponseStatus.FAILURE_RESPONSE;
    }

    @Override
    public IStatus getStatus() {
        return Status.OK;
    }

    @Override
    public Response get(UriResource uriResource, Map<String, String> urlParams, IHTTPSession session) {
        String uri = session.getUri();
        MainActivity context = uriResource.initParameter(0, MainActivity.class);
        ResourceLink resourceLink = uriResource.initParameter(1, ResourceLink.class);

        //extract path from url
        int offset = uri.indexOf("/", 0);
        int startIndex = uri.indexOf("/", offset + 1);
        String filePath = uri.substring(startIndex + 1);

        try {
            for (Link link : resourceLink.getLinks()) {
                if (link.getHref().contains(filePath)) {
                    InputStream inputStream = context.getAssets().open(link.getHref());
                    return serveResponse(session, inputStream, link.getType());
                }
            }
        } catch (IOException e) {
            Log.i(TAG, "error " + e);
        }

        return NanoHTTPD.newFixedLengthResponse(Status.NOT_FOUND, getMimeType(), ResponseStatus.FAILURE_RESPONSE);
    }

    private Response serveResponse(IHTTPSession session, InputStream inputStream, String mimeType) {
        Response response;
        String rangeRequest = session.getHeaders().get("range");

        try {
            // Calculate etag
            String etag = Integer.toHexString(inputStream.hashCode());

            // Support skipping:
            long startFrom = 0;
            long endAt = -1;
            if (rangeRequest != null) {
                if (rangeRequest.startsWith("bytes=")) {
                    rangeRequest = rangeRequest.substring("bytes=".length());
                    int minus = rangeRequest.indexOf('-');
                    try {
                        if (minus > 0) {
                            startFrom = Long.parseLong(rangeRequest.substring(0, minus));
                            endAt = Long.parseLong(rangeRequest.substring(minus + 1));
                        }
                    } catch (NumberFormatException ignored) {
                    }
                }
            }

            // Change return code and add Content-Range header when skipping is requested
            long streamLength = inputStream.available();
            if (rangeRequest != null && startFrom >= 0) {
                if (startFrom >= streamLength) {
                    response = createResponse(Status.RANGE_NOT_SATISFIABLE, MIME_PLAINTEXT, "");
                    response.addHeader("Content-Range", "bytes 0-0/" + streamLength);
                    response.addHeader("ETag", etag);
                } else {
                    if (endAt < 0) {
                        endAt = streamLength - 1;
                    }
                    long newLen = endAt - startFrom + 1;
                    if (newLen < 0) {
                        newLen = 0;
                    }

                    final long dataLen = newLen;
                    inputStream.skip(startFrom);

                    response = createResponse(Status.PARTIAL_CONTENT, mimeType, inputStream);
                    response.addHeader("Content-Length", "" + dataLen);
                    response.addHeader("Content-Range", "bytes " + startFrom + "-" + endAt + "/" + streamLength);
                    response.addHeader("ETag", etag);
                }
            } else {
                if (etag.equals(session.getHeaders().get("if-none-match")))
                    response = createResponse(Status.NOT_MODIFIED, mimeType, "");
                else {
                    response = createResponse(Status.OK, mimeType, inputStream);
                    response.addHeader("Content-Length", "" + streamLength);
                    response.addHeader("ETag", etag);
                }
            }
        } catch (IOException ioe) {
            response = getResponse("Forbidden: Reading file failed");
        }

        return (response == null) ? getResponse("Error 404: File not found") : response;
    }

    private Response createResponse(Status status, String mimeType, InputStream message) {
        Response response = NanoHTTPD.newChunkedResponse(status, mimeType, message);
        response.addHeader("Accept-Ranges", "bytes");
        return response;
    }

    private Response createResponse(Status status, String mimeType, String message) {
        Response response = NanoHTTPD.newFixedLengthResponse(status, mimeType, message);
        response.addHeader("Accept-Ranges", "bytes");
        return response;
    }

    private Response getResponse(String message) {
        return createResponse(Status.OK, "text/plain", message);
    }
}