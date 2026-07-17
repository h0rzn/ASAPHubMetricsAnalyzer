package de.htwberlin.visualizer;

import java.util.List;

public record PeerTimelineRow(
        String peerId,
        List<TimelineBar> bars
) {}
