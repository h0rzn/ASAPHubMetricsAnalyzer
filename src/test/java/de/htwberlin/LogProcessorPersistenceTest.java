package de.htwberlin;

import de.htwberlin.model.ConnectionRequestInfo;
import de.htwberlin.model.DataSessionInfo;
import de.htwberlin.model.PeerInfo;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

/**
 * Verifies that LogProcessor calls repository methods correctly.
 */
class LogProcessorPersistenceTest {
    /**
     * Test-Double that stores in a list rather than a database.
     */
    private static class CapturingRepository implements MetricsRepository {
        final List<Object> persisted = new ArrayList<>();

        @Override
        public void persist(Object entity) {
            persisted.add(entity);
        }
    }

    /**
     * Prepares a LogProcessor which only contains the given line.
     * @param line line to be fed into the LogProcessor instance
     * @return LogProcessor
     */
    private LogProcessor processorFor(String line) {
        InputStream std = new ByteArrayInputStream(line.getBytes(StandardCharsets.UTF_8));
        InputStream err = InputStream.nullInputStream();
        return new LogProcessor(std, err, new CapturingRepository());
    }

    /**
     * Runner method for checking if repository was called correctly. Creates a
     * repository and runs the LogProcessor with the line as input.
     */
    private CapturingRepository runWith(String line) throws ExecutionException, InterruptedException {
        CapturingRepository repository = new CapturingRepository();
        InputStream std = new ByteArrayInputStream(line.getBytes(StandardCharsets.UTF_8));
        InputStream err = InputStream.nullInputStream();
        new LogProcessor(std, err, repository).run();
        return repository;
    }

    @Test
    void register_persistsPeerInfo() throws Exception {
        var repo = runWith("[REGISTER] peerId=abc; canCreateTCPConnections=true [/REGISTER]");
        assertInstanceOf(PeerInfo.class, repo.persisted.getFirst());
    }

    @Test
    void connectionRequest_persistsConnectionRequestInfo() throws Exception {
        var repo = runWith("[CONNECTION_REQUEST] sourcePeerId=alice; targetPeerId=bob; timeoutMs=5000 [/CONNECTION_REQUEST]");
        assertInstanceOf(ConnectionRequestInfo.class, repo.persisted.getFirst());
    }

    @Test
    void startDataSession_persistsDataSessionInfo() throws Exception {
        var repo = runWith("[START_DATA_SESSION] sourcePeerId=alice; targetPeerId=bob; timeoutMs=3000; startedAt=1000; endedAt=2000 [/START_DATA_SESSION]");
        assertInstanceOf(DataSessionInfo.class, repo.persisted.getFirst());
    }
}
