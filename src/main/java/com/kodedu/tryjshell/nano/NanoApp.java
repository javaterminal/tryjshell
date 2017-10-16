package com.kodedu.tryjshell.nano;

import com.kodedu.tryjshell.websocket.TerminalSocket;
import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoWSD;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

public class NanoApp extends NanoWSD {

    public static void main(String[] args) throws IOException {
        new NanoApp("0.0.0.0", 8080).start(5 * 60 * 1000, false);
        System.out.println("\nRunning! Point your browsers to http://localhost:8080/ \n");
    }

    public NanoApp(String hostname, int port) {
        super(hostname, port);
    }

    @Override
    protected WebSocket openWebSocket(IHTTPSession handshake) {
        return new TerminalSocket(this, handshake);
    }

    @Override
    protected Response serveHttp(IHTTPSession session) {
        BufferedInputStream fis = null;
        String contentType = null;
        try {
//            Path root = Paths.get("C:\\Users\\usta\\tryjshell\\src\\main\\resources\\public");

            String uri = session.getUri();

            if (uri.length() == 1) {
                uri = "/index.html";
            }

            InputStream inputStream = NanoApp.class.getResourceAsStream("/public" + uri);

//            Path resolve = root.resolve(uri);
//            contentType = Files.probeContentType(resolve);
            fis = new BufferedInputStream(inputStream);
        } catch (Exception e) {
//            e.printStackTrace();
            return NanoHTTPD.newFixedLengthResponse("No Response");
        }

        return NanoHTTPD.newChunkedResponse(Response.Status.OK, contentType, fis);
    }

}
