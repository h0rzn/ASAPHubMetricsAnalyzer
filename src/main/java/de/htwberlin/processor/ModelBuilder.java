package de.htwberlin.processor;

import de.htwberlin.model.*;

import java.time.Instant;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class ModelBuilder {
    public static Register buildPeerInfo(String line) {
        Map<String, String> f = parseFields(line, "REGISTER");
        return new Register(
                f.get("peerId"),
                Boolean.parseBoolean(f.get("canCreateTCPConnections"))
        );
    }

    public static ConnectionRequest buildConnectionRequestInfo(String line) {
        Map<String, String> f = parseFields(line, "CONNECTION_REQUEST");
        return new ConnectionRequest(
                f.get("sourcePeerId"),
                f.get("targetPeerId"),
                Integer.parseInt(f.get("timeoutMs"))
        );
    }

    public static StartDataSession buildDataSessionInfo(String line) {
        Map<String, String> f = parseFields(line, "START_DATA_SESSION");
        return new StartDataSession(
                f.get("sourcePeerId"),
                f.get("targetPeerId"),
                Integer.parseInt(f.get("timeoutMs")),
                Instant.ofEpochMilli(Long.parseLong(f.get("startedAt"))),
                Instant.ofEpochMilli(Long.parseLong(f.get("endedAt")))
        );
    }

    private static Map<String, String> parseFields(String line, String tag) {
        int start = line.indexOf("[" + tag + "]") + tag.length() + 2;
        int end = line.indexOf("[/" + tag + "]");
        String inner = line.substring(start, end).trim();
        if (inner.isEmpty()) return Map.of();
        return Arrays.stream(inner.split(";"))
                .map(String::trim)
                .filter(s -> s.contains("="))
                .collect(Collectors.toMap(
                        s -> s.substring(0, s.indexOf('=')).trim(),
                        s -> s.substring(s.indexOf('=') + 1).trim()
                ));
    }
}
