package com.kodedu.tryjshell.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kodedu.tryjshell.helper.IOHelper;
import com.kodedu.tryjshell.helper.ThreadHelper;
import com.kodedu.tryjshell.nano.NanoApp;
import com.kodedu.tryjshell.process.ProcessWrapper;
import com.kodedu.tryjshell.websocket.TerminalSocket;
import com.pty4j.PtyProcess;
import com.pty4j.WinSize;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.LinkedBlockingQueue;

//@Component
//@Scope("prototype")
public class TerminalService {

    private static String shellStarter;
    private static LinkedBlockingQueue<ProcessWrapper> processQueue = new LinkedBlockingQueue<>();
    private final NanoApp nanoApp;
    private final TerminalSocket terminalSocket;
    private ProcessWrapper processWrapper;
    private BufferedWriter outputWriter;
    private boolean firstSent = false;

    private String cols = "80";
    private String rows = "20";

    public TerminalService(NanoApp nanoApp, TerminalSocket terminalSocket) {
        this.nanoApp = nanoApp;
        this.terminalSocket = terminalSocket;
        init();
    }

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

        onTerminalResize(this.cols, this.rows);

        this.outputWriter = processWrapper.getOutputWriter();
        BufferedReader inputReader = processWrapper.getInputReader();
        BufferedReader errorReader = processWrapper.getErrorReader();

        ThreadHelper.start(() -> {
            printReader(inputReader);
        });

        ThreadHelper.start(() -> {
            printReader(errorReader);
        });

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

        if (terminalSocket.isOpen()) {
            terminalSocket.send(message);

            if (!firstSent) {
                firstSent = true;
                this.onCommand(outputWriter, "/set editor /usr/bin/vim\n");
            }
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

    public void onCommand(BufferedWriter outputWriter, String command) {

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

    public void onCommand(String command) {
        onCommand(this.outputWriter, command);
    }

    public void onTerminalResize(String cols, String rows) {
        if (Objects.nonNull(cols) && Objects.nonNull(rows)) {

            this.cols = cols;
            this.rows = rows;

            if (Objects.nonNull(processWrapper)) {

                processWrapper.getProcess().setWinSize(new WinSize(Integer.parseInt(cols), Integer.parseInt(rows)));
            }

        }
    }

}
