package com.kodedu.tryjshell.process;

import com.pty4j.PtyProcess;

import java.io.BufferedReader;
import java.io.BufferedWriter;

public class ProcessWrapper {

    private final PtyProcess process;
    private BufferedWriter outputWriter;
    private BufferedReader inputReader;
    private BufferedReader errorReader;

    public ProcessWrapper(PtyProcess process) {
        this.process= process;
    }

    public void setOutputWriter(BufferedWriter outputWriter) {
        this.outputWriter = outputWriter;
    }

    public BufferedWriter getOutputWriter() {
        return outputWriter;
    }

    public void setInputReader(BufferedReader inputReader) {
        this.inputReader = inputReader;
    }

    public BufferedReader getInputReader() {
        return inputReader;
    }

    public void setErrorReader(BufferedReader errorReader) {
        this.errorReader = errorReader;
    }

    public BufferedReader getErrorReader() {
        return errorReader;
    }

    public PtyProcess getProcess() {
        return process;
    }
}
