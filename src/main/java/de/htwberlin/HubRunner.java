package de.htwberlin;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.*;

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
        this.standardOutputStreamWriter.newLine(); // brauchen wir das wirklich?
        this.standardOutputStreamWriter.flush();
    }

    private void handleErrorOutputLine(String errorLine) throws IOException {
        this.errorOutputStreamWriter.write("stderr: " + errorLine);
        this.errorOutputStreamWriter.newLine(); // brauchen wir das wirklich?
        this.errorOutputStreamWriter.flush();
    }

    public void run() throws Exception {
        //List.of("java", "-jar", "<path>", "<arg1>");
        Process hubProcess = this.runProcess(
                List.of("python3", "test_process.py")
        );
        this.watchProcess(hubProcess);
    }

    @Override
    public void close() throws Exception {
        System.out.println("runner: close");
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
