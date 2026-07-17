package de.htwberlin;

import de.htwberlin.persistence.EntityManagerFactory;

import java.io.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) throws Exception {
        PipedOutputStream standardPipedOutputStream = new PipedOutputStream();
        PipedInputStream standardPipedInputStream = new PipedInputStream(standardPipedOutputStream);

        PipedOutputStream errorPipedOutputStream = new PipedOutputStream();
        PipedInputStream errorPipedInputStream = new PipedInputStream(errorPipedOutputStream);

        FileOutputStream standardFileOut = new FileOutputStream("standard-out.log");
        FileOutputStream errorFileOut = new FileOutputStream("error-out.log");

        ExecutorService executorService = Executors.newFixedThreadPool(2);
        EntityManagerFactory emf = new EntityManagerFactory();
        LogProcessor logProcessor = new LogProcessor(
                standardPipedInputStream,
                errorPipedInputStream,
                emf
        );

        System.out.println("init runner");
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
            logProcessorFuture.get();
        }
    }
}