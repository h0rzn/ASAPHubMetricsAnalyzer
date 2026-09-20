package de.htwberlin;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.*;
import net.sharksystem.hub.hubside.*;

/**
 * Launches the ASAP Hub as a subprocess and forwards its stdout and stderr
 * to the provided output streams. The streams are written line-by-line and
 * prefixed with 'stdout:' and 'stderr:' respectively.
 */
public class HubRunner implements AutoCloseable {
    private final ExecutorService executorService;
    private final BufferedWriter standardOutputStreamWriter;
    private final BufferedWriter errorOutputStreamWriter;

    @FunctionalInterface
    private interface LineHandler {
        void handle(String line) throws IOException;
    }

    private Process runProcess(List<String> processParams) throws IOException {
        ProcessBuilder builder = new ProcessBuilder(processParams);
        return builder.start();
    }

    /**
     * Reads stdout and stderr of a process in two threads
     * and blocks until both streams are fully consumed.
     */
    private void watchProcess(Process process) throws InterruptedException, IOException, ExecutionException {
        InputStream inputStream = process.getInputStream();
        InputStream errorInputStream = process.getErrorStream();

        Future<Void> standardFuture = this.executorService.submit(() -> {
            this.readStream(inputStream, this::handleOutputLine);
            return null;
        });

        Future<Void> errorFuture = this.executorService.submit(() -> {
            this.readStream(errorInputStream, this::handleErrorOutputLine);
            return null;
        });

        standardFuture.get();
        errorFuture.get();

        process.waitFor();
    }

    private void readStream(InputStream inputStream, LineHandler logHandler) throws IOException {
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String outputLine;
        while ((outputLine = bufferedReader.readLine()) != null) {
            logHandler.handle(outputLine);
        }
    }

    private void handleOutputLine(String outputLine) throws IOException {
        this.standardOutputStreamWriter.write("stdout: " + outputLine);
        this.standardOutputStreamWriter.newLine();
        this.standardOutputStreamWriter.flush();
    }

    private void handleErrorOutputLine(String errorLine) throws IOException {
        this.errorOutputStreamWriter.write("stderr: " + errorLine);
        this.errorOutputStreamWriter.newLine();
        this.errorOutputStreamWriter.flush();
    }

    /**
     * Builds the classpath from the embedded JAR resources, starts the hub process
     * and blocks until it exits.
     */
    public void run() throws Exception {
        URL hub = getClass().getClassLoader().getResource("ASAPHub.jar");
        URL asap = getClass().getClassLoader().getResource("ASAPJava.jar");

        String cp = Paths.get(hub.toURI()).toString()
                + File.pathSeparator
                + Paths.get(asap.toURI()).toString();
        // For testing without a real hub, replace with: List.of("python", "test_process.py")
        Process hubProcess = this.runProcess(
                List.of("java", "-cp", cp, "net.sharksystem.hub.hubside.ASAPTCPHub")
        );
        this.watchProcess(hubProcess);
    }

    @Override
    public void close() throws Exception {
        this.executorService.shutdownNow();
        this.executorService.awaitTermination(2, TimeUnit.SECONDS);

        this.standardOutputStreamWriter.close();
        this.errorOutputStreamWriter.close();
    }

    public HubRunner(OutputStream standardOutputStream, OutputStream errorOutputStream) {
        this.executorService = Executors.newFixedThreadPool(2);

        this.standardOutputStreamWriter = new BufferedWriter(
                new OutputStreamWriter(standardOutputStream, StandardCharsets.UTF_8)
        );
        this.errorOutputStreamWriter = new BufferedWriter(
                new OutputStreamWriter(errorOutputStream, StandardCharsets.UTF_8)
        );
    }


}
