package de.htwberlin.visualizer;

import de.htwberlin.model.ConnectionRequestInfo;
import de.htwberlin.model.DataSessionInfo;
import de.htwberlin.model.EventLogEntry;
import de.htwberlin.model.PeerInfo;
import de.htwberlin.persistence.EntityManagerFactory;
import de.htwberlin.processor.MetricsRepository;
import gg.jte.CodeResolver;
import gg.jte.ContentType;
import gg.jte.TemplateEngine;
import gg.jte.output.StringOutput;
import gg.jte.resolve.DirectoryCodeResolver;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class ReportBuilder {
    private MetricsRepository metricsRepository;

    private Map<String, Object> collectReportParams() {
        String hubName = "hub-123";
        Instant generatedAt = Instant.now();
        List<PeerInfo> registeredPeers = this.metricsRepository.findAllPeerInfos();
        List<ConnectionRequestInfo> connectionRequests = this.metricsRepository.findAllConnectionRequests();
        List<DataSessionInfo> dataSessions = this.metricsRepository.findAllDataSessions();
        List<EventLogEntry> eventLogs = new ArrayList<>();

        Instant measureStart = dataSessions.stream()
                .map(DataSessionInfo::getStartedAt)
                .min(Comparator.naturalOrder())
                .orElse(generatedAt);
        Instant measureEnd = dataSessions.stream()
                .map(s -> s.getEndedAt() != null ? s.getEndedAt() : generatedAt)
                .max(Comparator.naturalOrder())
                .orElse(generatedAt);

        List<PeerTimelineRow> peerTimelines = buildTimelines(registeredPeers, dataSessions, measureStart, measureEnd);

        return Map.of(
                "hubName", hubName,
                "generatedAt", generatedAt,
                "registeredPeers", registeredPeers,
                "connectionRequests", connectionRequests,
                "dataSessions", dataSessions,
                "events", eventLogs,
                "measurementStart", measureStart,
                "measurementEnd", measureEnd,
                "peerTimelines", peerTimelines
        );
    }

    private List<PeerTimelineRow> buildTimelines(
            List<PeerInfo> peers,
            List<DataSessionInfo> dataSessions,
            Instant measureStart,
            Instant measureEnd
    ) {
        long totalMs = measureEnd.toEpochMilli() - measureStart.toEpochMilli();
        if (totalMs <= 0) return List.of();

        List<PeerTimelineRow> peerRows = new ArrayList<>();
        for (PeerInfo peer : peers) {
            List<TimelineBar> bars = new ArrayList<>();
            for (DataSessionInfo session : dataSessions) {
                if (!session.getSourcePeerId().equals(peer.getPeerId())
                        && !session.getTargetPeerId().equals(peer.getPeerId())) {
                    continue;
                }

                Instant end = session.getEndedAt();

                double leftPercent  = (session.getStartedAt().toEpochMilli() - measureStart.toEpochMilli()) * 100.0 / totalMs;
                double widthPercent = (end.toEpochMilli() - session.getStartedAt().toEpochMilli()) * 100.0 / totalMs;

                bars.add(new TimelineBar(leftPercent, widthPercent));
            }
            peerRows.add(new PeerTimelineRow(peer.getPeerId(), bars));
        }
        return peerRows;
    }

    public String generate(Map<String, Object> reportParams, String path) {
        CodeResolver codeResolver = new DirectoryCodeResolver(Path.of("src/main/jte"));
        TemplateEngine templateEngine = TemplateEngine.create(codeResolver, ContentType.Html);

        StringOutput output = new StringOutput();
        templateEngine.render(path, reportParams, output);
        return output.toString();
    }

    public void write(String content, Path outputPath) throws IOException {
        Files.writeString(outputPath, content);
    }

    public ReportBuilder(MetricsRepository metricsRepository) {
        this.metricsRepository = metricsRepository;
    }

    public static void main(String[] args) throws IOException {
        MetricsRepository repository = new EntityManagerFactory();
        ReportBuilder builder = new ReportBuilder(repository);
        Map<String, Object> reportParams = builder.collectReportParams();
        String reportContents = builder.generate(reportParams, "report.jte");
        builder.write(reportContents, Path.of("rep2.html"));

    }
}
