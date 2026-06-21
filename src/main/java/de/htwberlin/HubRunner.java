package de.htwberlin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

public class HubRunner {
    private Process runProcess(List<String> processParams) throws IOException {
        ProcessBuilder builder = new ProcessBuilder(processParams);
        return builder.start();
    }

    private void watchProcess(Process process) throws InterruptedException {
        InputStream inputStream = process.getInputStream();
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader inputStreamBufferedReader = new BufferedReader(inputStreamReader);

        InputStream errorInputStream = process.getErrorStream();
        InputStreamReader errorInputStreamReader = new InputStreamReader(errorInputStream);
        BufferedReader errorInputStreamBufferedReader = new BufferedReader(errorInputStreamReader);

        Thread threadStdout = new Thread(() -> {
            String outputLine;
            try {
                while ((outputLine = inputStreamBufferedReader.readLine()) != null) {
                    this.handleOutputLine(outputLine);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        Thread threadStderr = new Thread(() -> {
            try {
                String outputLine;
                while ((outputLine = errorInputStreamBufferedReader.readLine()) != null) {
                    this.handleErrorOutputLine(outputLine);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        threadStdout.start();
        threadStderr.start();
        threadStdout.join();
        threadStderr.join();

        process.waitFor();
        process.destroy();
    }

    private void handleOutputLine(String outputLine) {
        System.out.println("stdout: " + outputLine);
    }

    private void handleErrorOutputLine(String errorLine) {
        System.out.println("stderr: " + errorLine);
    }

    public void run() throws IOException, InterruptedException {
        //List.of("java", "-jar", "<path>", "<arg1>");
        Process hubProcess = this.runProcess(
                List.of("python3", "test_process.py")
        );
        this.watchProcess(hubProcess);
    }
}
