package de.htwberlin;

import de.htwberlin.persistence.EntityManagerFactory;
import de.htwberlin.processor.LogProcessor;

import java.io.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Connects {@link HubRunner} and {@link LogProcessor}
 * via piped streams so that the hub's stdout/stderr are forwarded to the
 * processor. Both components run concurrently in separate
 * threads. The application terminates once the hub process exits and all output has been
 * processed.
 */
public class Main {
    public static void main(String[] args) throws Exception {
        PipedOutputStream standardPipedOutputStream = new PipedOutputStream();
        PipedInputStream standardPipedInputStream = new PipedInputStream(standardPipedOutputStream);

        PipedOutputStream errorPipedOutputStream = new PipedOutputStream();
        PipedInputStream errorPipedInputStream = new PipedInputStream(errorPipedOutputStream);

        ExecutorService executorService = Executors.newFixedThreadPool(2);
        EntityManagerFactory emf = new EntityManagerFactory();
        LogProcessor logProcessor = new LogProcessor(
                standardPipedInputStream,
                errorPipedInputStream,
                emf
        );

        try (HubRunner hubRunner = new HubRunner(
                standardPipedOutputStream,
                errorPipedOutputStream
        )) {
            Future<?> runnerFuture = executorService.submit(() -> {
                try {
                    hubRunner.run();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            Future<?> logProcessorFuture = executorService.submit(() -> {
                try {
                    logProcessor.run();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            runnerFuture.get();
            standardPipedOutputStream.close();
            errorPipedOutputStream.close();
            logProcessorFuture.get();
        }
        executorService.shutdown();
    }
}