package de.htwberlin.processor;

import de.htwberlin.model.*;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ModelBuilderTest {

    @Test
    void testBuildPeerInfo() {
        String log = "[REGISTER] peerId:abc; canCreateTCPConnections:true [/REGISTER]";
        Register result = ModelBuilder.buildPeerInfo(log);
        assertEquals("abc", result.getPeerId());
        assertEquals(true, result.isCanCreateTCPConnections());
    }

    @Test
    void testBuildConnectionRequestInfo() {
        String log = "[CONNECTION_REQUEST] source:alice; target:bob; timeout:5000 [/CONNECTION_REQUEST]";
        ConnectionRequest result = ModelBuilder.buildConnectionRequestInfo(log);
        assertEquals("alice", result.getSourcePeerId());
        assertEquals("bob", result.getTargetPeerId());
        assertEquals(5000L, result.getTimeoutMs());
    }

    @Test
    void testBuildDataSessionInfo() {
        String log = "[START_DATA_SESSION] source:alice; target:bob; timeout:3000; timeStart:1000 [/START_DATA_SESSION]";
        StartDataSession result = ModelBuilder.buildDataSessionInfo(log);
        assertEquals("alice", result.getSourcePeerId());
        assertEquals("bob", result.getTargetPeerId());
        assertEquals(3000, result.getTimeoutMs());
        assertEquals(Instant.ofEpochMilli(1000), result.getStartedAt());
    }
}