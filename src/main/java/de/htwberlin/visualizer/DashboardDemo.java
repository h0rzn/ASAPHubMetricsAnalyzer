package de.htwberlin.visualizer;

import de.htwberlin.model.*;
import gg.jte.CodeResolver;
import gg.jte.ContentType;
import gg.jte.TemplateEngine;
import gg.jte.output.StringOutput;
import gg.jte.resolve.DirectoryCodeResolver;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

public class DashboardDemo {
    private static final DateTimeFormatter FULL_FMT =
            DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss").withZone(ZoneId.systemDefault());
    private static final DateTimeFormatter TIME_FMT =
            DateTimeFormatter.ofPattern("HH:mm:ss").withZone(ZoneId.systemDefault());

    public static String full(Instant instant) {
        return FULL_FMT.format(instant);
    }

    public static String time(Instant instant) {
        return TIME_FMT.format(instant);
    }

    public static void main(String[] args) throws IOException {
        Instant now = Instant.now();

        List<Register> registeredPeers = List.of(
                new Register("peer-a3f2c9", true),
                new Register("peer-991cde", true),
                new Register("peer-7d0e11", true),
                new Register("peer-bb44aa", false)
        );

        List<ConnectionRequest> connectionRequests = List.of(
                new ConnectionRequest("peer-a3f2c9", "peer-991cde", 5000),
                new ConnectionRequest("peer-7d0e11", "peer-bb44aa", 3000)
        );

        List<StartDataSession> dataSessions = List.of(
                new StartDataSession("peer-a3f2c9", "peer-991cde", 30000,
                        now.minus(8, ChronoUnit.MINUTES), null),
                new StartDataSession("peer-7d0e11", "peer-bb44aa", 30000,
                        now.minus(20, ChronoUnit.MINUTES), now.minus(5, ChronoUnit.MINUTES)),
                new StartDataSession("peer-991cde", "peer-bb44aa", 15000,
                        now.minus(3, ChronoUnit.MINUTES), null)
        );

        List<EventLogEntry> events = List.of(
                new EventLogEntry(EventType.REGISTER, "peer-a3f2c9", null, true),
                new EventLogEntry(EventType.REGISTER, "peer-991cde", null, true),
                new EventLogEntry(EventType.REGISTER, "peer-cc1029", null, false),
                new EventLogEntry(EventType.REGISTER, "peer-7d0e11", null, true),
                new EventLogEntry(EventType.DISCONNECT, "peer-7d0e11", "peer-bb44aa", null),
                new EventLogEntry(EventType.REGISTER, "peer-bb44aa", null, true),
                new EventLogEntry(EventType.UNREGISTER, "peer-cc1029", null, null)
        );

        Instant measurementStart = now.minus(30, ChronoUnit.MINUTES);
        Instant measurementEnd = now;

        List<PeerTimelineRow> peerTimelines = List.of(
                new PeerTimelineRow("peer-a3f2c9", List.of(new TimelineBar(16.7, 83.3))),
                new PeerTimelineRow("peer-991cde", List.of(new TimelineBar(20.0, 80.0))),
                new PeerTimelineRow("peer-cc1029", List.of(new TimelineBar(26.7, 40.0))),
                new PeerTimelineRow("peer-7d0e11", List.of(new TimelineBar(33.3, 66.7))),
                new PeerTimelineRow("peer-bb44aa", List.of(new TimelineBar(40.0, 60.0)))
        );

        Map<String, Object> params = Map.of(
                "hubName", "RELAY-HUB-01",
                "generatedAt", now,
                "registeredPeers", registeredPeers,
                "connectionRequests", connectionRequests,
                "dataSessions", dataSessions,
                "events", events,
                "measurementStart", measurementStart,
                "measurementEnd", measurementEnd,
                "peerTimelines", peerTimelines
        );

        CodeResolver codeResolver = new DirectoryCodeResolver(Path.of("src/main/jte"));
        TemplateEngine templateEngine = TemplateEngine.create(codeResolver, ContentType.Html);

        StringOutput output = new StringOutput();
        templateEngine.render("report.jte", params, output);

        Path outFile = Path.of("report.html");
        Files.writeString(outFile, output.toString());
    }
}