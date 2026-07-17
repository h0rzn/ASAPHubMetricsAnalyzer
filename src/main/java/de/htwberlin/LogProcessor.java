package de.htwberlin;

import de.htwberlin.model.ConnectionRequestInfo;
import de.htwberlin.model.DataSessionInfo;
import de.htwberlin.model.PeerInfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Consumer;

public class LogProcessor {
    private final ExecutorService executorService;
    private final InputStream standardInputStream;
    private final InputStream errorInputStream;

    public void run() throws ExecutionException, InterruptedException {
        System.out.println("processor: run");

        Future<?> standardHandler = this.executorService.submit(
                () -> readLines(standardInputStream, this::handleStandardOut)
        );
        Future<?> errorHandler = this.executorService.submit(
                () -> readLines(errorInputStream, this::handleErrorOut)
        );

        standardHandler.get();
        errorHandler.get();
    }

    private void readLines(InputStream inputStream, Consumer<String> lineHandler) {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {

            String line;
            while ((line = reader.readLine()) != null) {
                lineHandler.accept(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleStandardOut(String line) {
        String eventName = this.extractEvent(line);
        if (eventName.isEmpty()) return;

        switch (eventName) {
            case "REGISTER" -> {
                PeerInfo peerInfo = ModelBuilder.buildPeerInfo(line);
                System.out.println(peerInfo);
            }
            case "CONNECTION_REQUEST" -> {
                ConnectionRequestInfo requestInfo = ModelBuilder.buildConnectionRequestInfo(line);
                System.out.println(requestInfo);
            }
            case "START_DATA_SESSION" -> {
                DataSessionInfo sessionInfo = ModelBuilder.buildDataSessionInfo(line);
                System.out.println(sessionInfo);
            }
            default -> System.out.println("Unkown Event: " + line);
        }
    }

    private void handleErrorOut(String line) {
        System.out.println("unhandled error: " + line);
    }

    private String extractEvent(String line) {
        // TODO: handle case where start and/or end tag exists
        int startOffset = line.indexOf("[") + 1;
        int endOffset = line.indexOf("]");

        if (endOffset < startOffset) return "";

        return line.substring(startOffset, endOffset);
    }

    public LogProcessor(InputStream standardInputStream, InputStream errorInputStream) {
        this.executorService = Executors.newFixedThreadPool(2);
        this.standardInputStream = standardInputStream;
        this.errorInputStream = errorInputStream;
    }
}
