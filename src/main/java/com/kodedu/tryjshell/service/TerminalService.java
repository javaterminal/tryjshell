package com.kodedu.tryjshell.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kodedu.tryjshell.helper.IOHelper;
import com.kodedu.tryjshell.helper.ThreadHelper;
import com.kodedu.tryjshell.process.ProcessWrapper;
import com.pty4j.PtyProcess;
import com.pty4j.WinSize;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import javax.annotation.PostConstruct;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.IntStream;

@Component
@Scope("prototype")
public class TerminalService {

    private static String shellStarter;

    private WebSocketSession webSocketSession;

    //    private LinkedBlockingQueue<String> commandQueue = new LinkedBlockingQueue<>();
    private static LinkedBlockingQueue<ProcessWrapper> processQueue = new LinkedBlockingQueue<>();
    private ProcessWrapper processWrapper;
    private BufferedWriter outputWriter;

    public synchronized void addSingleProcess() {

        String tmpDir = System.getProperty("java.io.tmpdir");
        Path dataDir = Paths.get(tmpDir).resolve(".terminalfx");
        IOHelper.copyLibPty(dataDir);

//        Path systemRoot = Files.createTempDirectory(Paths.get(tmpDir), "systemRoot");
//        Files.createDirectories(systemRoot);
//        Path prefsFile = Files.createTempFile(systemRoot, ".userPrefs", null);
//        System.setProperty("java.util.prefs.systemRoot", systemRoot.normalize().toString());
//        System.setProperty("java.util.prefs.userRoot", prefsFile.normalize().toString());

        String[] termCommand = shellStarter.split("\\s+");

        Map<String, String> envs = new HashMap<>(System.getenv());
        envs.put("TERM", "xterm");

        System.setProperty("PTY_LIB_FOLDER", dataDir.resolve("libpty").toString());

        try {
            PtyProcess process = PtyProcess.exec(termCommand, envs, System.getProperty("user.home"));
            process.setWinSize(new WinSize(50, 20));

            BufferedWriter outputWriter = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
            BufferedReader inputReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));

            ProcessWrapper processWrapper = new ProcessWrapper(process);
            processWrapper.setOutputWriter(outputWriter);
            processWrapper.setInputReader(inputReader);
            processWrapper.setErrorReader(errorReader);



            processQueue.offer(processWrapper);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @PostConstruct
    public void init() {

        shellStarter = System.getenv("shell");

        if (Objects.isNull(shellStarter)) {
            shellStarter = "jshell.exe";
        }

    }

    public void onTerminalInit() {

    }

    public void onTerminalReady() {

        ThreadHelper.start(() -> {
            try {
                initializeProcess();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

    }

    private void initializeProcess() throws Exception {

        if (processQueue.size() < 5) {
            addSingleProcess();
            addSingleProcess();
        }

        this.processWrapper = processQueue.poll();

        this.outputWriter = processWrapper.getOutputWriter();
        BufferedReader inputReader = processWrapper.getInputReader();
        BufferedReader errorReader = processWrapper.getErrorReader();

        ThreadHelper.start(() -> {
            printReader(inputReader);
        });

        ThreadHelper.start(() -> {
            printReader(errorReader);
        });

//        this.onCommand(outputWriter, "/set editor /usr/bin/vim\n");

        processWrapper.getProcess().waitFor();

        destroyProcess();

//        addSingleProcess();

    }

    public void destroyProcess() {

        if (Objects.isNull(processWrapper)) {
            return;
        }

        PtyProcess process = processWrapper.getProcess();
        try {

            process.destroyForcibly();
        } catch (Exception e) {
            e.printStackTrace();
        }
        processQueue.remove(process);
        IOHelper.close(process.getInputStream(), process.getErrorStream(), process.getOutputStream());

//        addSingleProcess();
    }

    public void print(String text) throws IOException {

        Map<String, String> map = new HashMap<>();
        map.put("type", "TERMINAL_PRINT");
        map.put("text", text);

        String message = new ObjectMapper().writeValueAsString(map);

        if (webSocketSession.isOpen()) {
            webSocketSession.sendMessage(new TextMessage(message));
        }

    }

    private void printReader(BufferedReader bufferedReader) {
        try {
            int nRead;
            char[] data = new char[1 * 1024];

            while ((nRead = bufferedReader.read(data, 0, data.length)) != -1) {
                StringBuilder builder = new StringBuilder(nRead);
                builder.append(data, 0, nRead);
                print(builder.toString());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onCommand(BufferedWriter outputWriter, String command) throws InterruptedException {

        if (Objects.isNull(command)) {
            return;
        }

        try {
            outputWriter.write(command);
            outputWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onCommand(String command) throws InterruptedException {
        onCommand(this.outputWriter, command);
    }

    public void onTerminalResize(String columns, String rows) {
        if (Objects.nonNull(columns) && Objects.nonNull(rows)) {

            if (Objects.nonNull(processWrapper)) {
                processWrapper.getProcess().setWinSize(new WinSize(Integer.valueOf(columns), Integer.valueOf(rows)));
            }

        }
    }

    public void setWebSocketSession(WebSocketSession webSocketSession) {
        this.webSocketSession = webSocketSession;
    }

    public WebSocketSession getWebSocketSession() {
        return webSocketSession;
    }
}
