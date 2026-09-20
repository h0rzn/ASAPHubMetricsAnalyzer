package de.htwberlin.processor;

import de.htwberlin.model.*;

import java.time.Instant;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Stateless utility class that parses structured hub log lines into domain objects.
 * Each {@code build*} method handles one event type and delegates field extraction
 * to {@link #parseFields(String, String)}.
 */
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
                f.get("source"),
                f.get("target"),
                Long.parseLong(f.get("timeout"))
        );
    }

    public static StartDataSession buildDataSessionInfo(String line) {
        Map<String, String> f = parseFields(line, "START_DATA_SESSION");
        return new StartDataSession(
                f.get("source"),
                f.get("target"),
                Integer.parseInt(f.get("timeout")),
                Instant.ofEpochMilli(Long.parseLong(f.get("timeStart"))),
                Instant.now()
        );
    }

    public static Unregister buildUnregister(String line) {
        Map<String, String> f = parseFields(line, "UNREGISTER");
        return new Unregister(
                f.get("peerId")
        );
    }

    public static Disconnect buildDisconnect(String line) {
        Map<String, String> f = parseFields(line, "DISCONNECT");
        return new Disconnect(
                f.get("source"),
                f.get("target")
        );
    }

    public static NotifyConnectionEnded buildNotifyConnectionEnded(String line) {
        Map<String, String> f = parseFields(line, "NOTIFY_CONNECTION_ENDED");
        return new NotifyConnectionEnded(
                f.get("source"),
                f.get("target"),
                Instant.ofEpochMilli(Long.parseLong(f.get("timeEnd")))
        );
    }

    /**
     * Extracts the content between '[TAG]' and '[/TAG]', splits it by ';',
     * and tokenizes each entry at ':' into a field-name-to-value map.
     */
    private static Map<String, String> parseFields(String line, String tag) {
        int start = line.indexOf("[" + tag + "]") + tag.length() + 2;
        int end = line.indexOf("[/" + tag + "]");
        String inner = line.substring(start, end).trim();
        if (inner.isEmpty()) return Map.of();
        return Arrays.stream(inner.split(";"))
                .map(String::trim)
                .filter(s -> s.contains(":"))
                .collect(Collectors.toMap(
                        s -> s.substring(0, s.indexOf(':')).trim(),
                        s -> s.substring(s.indexOf(':') + 1).trim()
                ));
    }
}
