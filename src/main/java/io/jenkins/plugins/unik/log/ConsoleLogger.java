package io.jenkins.plugins.unik.log;

import hudson.model.BuildListener;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.Charset;

/**
 * A helper class which offers an annotated  log via {@link UnikConsoleAnnotator}.
 * *
 */
public class ConsoleLogger {

    private final BuildListener listener;
    private final UnikConsoleAnnotator annotator;

    public ConsoleLogger(BuildListener listener) {
        this.listener = listener;
        this.annotator = new UnikConsoleAnnotator(this.listener.getLogger());
    }

    public BuildListener getListener() {
        return listener;
    }

    public PrintStream getLogger() {
        return listener.getLogger();
    }

    /**
     * Logs annotated messages with prifx "[Unik] INFO:"
     *
     * @param message message to be annotated
     */
    public void logInfo(String message) {
        logAnnot("[Unik] INFO: ", message);
    }

    /**
     * Logs annotated messages with prifx "[Unik] WARN:"
     *
     * @param message message to be annotated
     */
    public void logWarn(String message) {
        logAnnot("[Unik] WARN: ", message);
    }

    /**
     * Logs annotated messages with prifx "[Unik] ERROR:"
     *
     * @param message message to be annotated
     */
    public void logError(String message) {
        logAnnot("[Unik] ERROR: ", message);
    }

    private void logAnnot(String prefix, String message) {
        byte[] msg = (prefix + message + "\n").getBytes(Charset.defaultCharset());
        try {
            annotator.eol(msg, msg.length);
        } catch (IOException e) {
            log("Problem with writing into console log: " + e.getMessage());
        }
    }

    private void log(String message) {
        listener.getLogger().println(message);
    }
}
