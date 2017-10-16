package com.kodedu.tryjshell.websocket;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kodedu.tryjshell.nano.NanoApp;
import com.kodedu.tryjshell.service.TerminalService;
import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoWSD;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TerminalSocket extends NanoWSD.WebSocket {

    private final NanoApp nanoApp;
    private final NanoHTTPD.IHTTPSession handshake;
    private TerminalService terminalService;

    public TerminalSocket(NanoApp nanoApp, NanoHTTPD.IHTTPSession handshake) {
        super(handshake);
        this.nanoApp = nanoApp;
        this.handshake = handshake;
        this.terminalService = new TerminalService(nanoApp, this);
    }

    private Map<String, String> getMessageMap(NanoWSD.WebSocketFrame message) {
        try {
            Map<String, String> map = new ObjectMapper().readValue(message.getTextPayload(), new TypeReference<Map<String, String>>() {
            });

            return map;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new HashMap<>();
    }

    protected void onOpen() {

        System.out.println();

    }

    protected void onClose(NanoWSD.WebSocketFrame.CloseCode code, String reason, boolean initiatedByRemote) {

        terminalService.destroyProcess();

    }

    protected void onMessage(NanoWSD.WebSocketFrame message) {

        Map<String, String> messageMap = getMessageMap(message);

        if (messageMap.containsKey("type")) {
            String type = messageMap.get("type");

            switch (type) {
                case "TERMINAL_INIT":
                    terminalService.onTerminalInit();
                    break;
                case "TERMINAL_READY":
                    terminalService.onTerminalReady();
                    break;
                case "TERMINAL_COMMAND":
                    terminalService.onCommand(messageMap.get("command"));
                    break;
                case "TERMINAL_RESIZE":
                    terminalService.onTerminalResize(messageMap.get("columns"), messageMap.get("rows"));
                    break;
                default:
                    throw new RuntimeException("Unrecodnized action");
            }
        }

    }

    protected void onPong(NanoWSD.WebSocketFrame pong) {

    }

    protected void onException(IOException exception) {

        exception.printStackTrace();
        terminalService.destroyProcess();

    }
}
