package de.htwberlin.processor;

import de.htwberlin.model.*;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ModelBuilderTest {

    @Test
    void testBuildPeerInfo() {
        String log = "[REGISTER] peerId=abc; canCreateTCPConnections=true [/REGISTER]";
        PeerInfo result = ModelBuilder.buildPeerInfo(log);
        assertEquals("abc", result.getPeerId());
        assertEquals(true, result.isCanCreateTCPConnections());
    }

    @Test
    void testBuildConnectionRequestInfo() {
        String log = "[CONNECTION_REQUEST] sourcePeerId=alice; targetPeerId=bob; timeoutMs=5000 [/CONNECTION_REQUEST]";
        ConnectionRequestInfo result = ModelBuilder.buildConnectionRequestInfo(log);
        assertEquals("alice", result.getSourcePeerId());
        assertEquals("bob", result.getTargetPeerId());
        assertEquals(5000, result.getTimeoutMs());
    }

    @Test
    void testBuildDataSessionInfo() {
        String log = "[START_DATA_SESSION] sourcePeerId=alice; targetPeerId=bob; timeoutMs=3000; startedAt=1000; endedAt=2000 [/START_DATA_SESSION]";
        DataSessionInfo result = ModelBuilder.buildDataSessionInfo(log);
        assertEquals("alice", result.getSourcePeerId());
        assertEquals("bob", result.getTargetPeerId());
        assertEquals(3000, result.getTimeoutMs());
        assertEquals(Instant.ofEpochMilli(1000), result.getStartedAt());
        assertEquals(Instant.ofEpochMilli(2000), result.getEndedAt());
    }
}