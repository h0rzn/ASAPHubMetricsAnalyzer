@import java.time.Instant
@import java.util.List
@import de.htwberlin.model.ConnectionRequestInfo
@import de.htwberlin.model.DataSessionInfo
@import de.htwberlin.model.EventLogEntry
@import de.htwberlin.model.PeerInfo
@import de.htwberlin.visualizer.PeerTimelineRow
@import de.htwberlin.visualizer.TimelineBar
@import static de.htwberlin.visualizer.DashboardDemo.full
@import static de.htwberlin.visualizer.DashboardDemo.time

@param List<PeerInfo> registeredPeers
@param List<ConnectionRequestInfo> connectionRequests
@param List<DataSessionInfo> dataSessions
@param List<EventLogEntry> events
@param List<PeerTimelineRow> peerTimelines
@param Instant measurementStart
@param Instant measurementEnd

<!DOCTYPE html>
<html lang="de">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Analyse</title>
  <script src="https://cdn.tailwindcss.com"></script>
</head>
<body class="bg-white text-gray-800 min-h-screen p-8">

  <div class="max-w-2xl mx-auto">

    <h1 class="text-2xl font-semibold mb-4">Bekannte Peers</h1>
    @if(registeredPeers.isEmpty())
      <p class="text-gray-400 mb-10">Keine Peers registriert.</p>
    @else
      <div class="flex flex-wrap gap-2 mb-10">
        @for(PeerInfo peer : registeredPeers)
          <span class="px-3 py-1 text-sm border border-gray-200 rounded-full text-gray-700">
            ${peer.getPeerId()}
            <span class="ml-2 text-xs text-gray-400">TCP: ${peer.isCanCreateTCPConnections()}</span>
          </span>
        @endfor
      </div>
    @endif

    <h2 class="text-2xl font-semibold mb-4">Connection Requests</h2>
    @if(connectionRequests.isEmpty())
      <p class="text-gray-400 mb-10">Keine Connection Requests vorhanden.</p>
    @else
      <table class="w-full text-sm border-collapse mb-10">
        <thead>
          <tr class="border-b border-gray-200 text-left text-gray-500 text-xs uppercase tracking-wide">
            <th class="pb-2 pr-6 font-medium">Source</th>
            <th class="pb-2 pr-6 font-medium">Target</th>
            <th class="pb-2 font-medium">Timeout (ms)</th>
          </tr>
        </thead>
        <tbody class="divide-y divide-gray-100">
          @for(ConnectionRequestInfo req : connectionRequests)
            <tr>
              <td class="py-2 pr-6">${req.getSourcePeerId()}</td>
              <td class="py-2 pr-6">${req.getTargetPeerId()}</td>
              <td class="py-2 text-gray-500">${req.getTimeoutMs()}</td>
            </tr>
          @endfor
        </tbody>
      </table>
    @endif

    <h2 class="text-2xl font-semibold mb-4">Data Sessions</h2>
    @if(dataSessions.isEmpty())
      <p class="text-gray-400 mb-10">Keine Data Sessions vorhanden.</p>
    @else
      <table class="w-full text-sm border-collapse mb-10">
        <thead>
          <tr class="border-b border-gray-200 text-left text-gray-500 text-xs uppercase tracking-wide">
            <th class="pb-2 pr-6 font-medium">Source</th>
            <th class="pb-2 pr-6 font-medium">Target</th>
            <th class="pb-2 pr-6 font-medium">Start</th>
            <th class="pb-2 pr-6 font-medium">Ende</th>
            <th class="pb-2 font-medium">Timeout (ms)</th>
          </tr>
        </thead>
        <tbody class="divide-y divide-gray-100">
          @for(DataSessionInfo session : dataSessions)
            <tr>
              <td class="py-2 pr-6">${session.getSourcePeerId()}</td>
              <td class="py-2 pr-6">${session.getTargetPeerId()}</td>
              <td class="py-2 pr-6 text-gray-500">${time(session.getStartedAt())}</td>
              <td class="py-2 pr-6 text-gray-500">
                @if(session.getEndedAt() != null)${time(session.getEndedAt())}@else—@endif
              </td>
              <td class="py-2 text-gray-500">${session.getTimeoutMs()}</td>
            </tr>
          @endfor
        </tbody>
      </table>
    @endif

    <h2 class="text-2xl font-semibold mb-4">Event-Log</h2>
    @if(events.isEmpty())
      <p class="text-gray-400 mb-10">Noch keine Events aufgezeichnet.</p>
    @else
      <table class="w-full text-sm border-collapse mb-10">
        <thead>
          <tr class="border-b border-gray-200 text-left text-gray-500 text-xs uppercase tracking-wide">
            <th class="pb-2 pr-6 font-medium">Typ</th>
            <th class="pb-2 pr-6 font-medium">Peer</th>
            <th class="pb-2 pr-6 font-medium">Related Peer</th>
            <th class="pb-2 font-medium">TCP</th>
          </tr>
        </thead>
        <tbody class="divide-y divide-gray-100">
          @for(EventLogEntry entry : events)
            <tr>
              <td class="py-2 pr-6">${entry.type().name()}</td>
              <td class="py-2 pr-6">${entry.peerId()}</td>
              <td class="py-2 pr-6 text-gray-500">
                @if(entry.relatedPeerId() != null)${entry.relatedPeerId()}@else—@endif
              </td>
              <td class="py-2 text-gray-500">
                @if(entry.canCreateTCPConnections() != null)${entry.canCreateTCPConnections()}@else—@endif
              </td>
            </tr>
          @endfor
        </tbody>
      </table>
    @endif

    <h2 class="text-2xl font-semibold mb-4">Peer-Lifetime</h2>
    @if(peerTimelines.isEmpty())
      <p class="text-gray-400">Keine Peer-Lifetime-Daten vorhanden.</p>
    @else
      <div class="flex justify-between text-xs text-gray-400 mb-1 pl-28">
        <span>${full(measurementStart)}</span>
        <span>${full(measurementEnd)}</span>
      </div>
      @for(PeerTimelineRow row : peerTimelines)
        <div class="flex items-center gap-3 py-1">
          <span class="w-24 text-sm text-gray-600 truncate flex-shrink-0">${row.peerId()}</span>
          <div class="relative flex-1 h-5 bg-gray-100 rounded overflow-visible">
            @for(TimelineBar bar : row.bars())
            <!--
              <div class="absolute top-1 bottom-1 rounded @if(bar.ongoing())bg-blue-400@else bg-gray-300@endif"
                   style="left: ${bar.leftPercent()}%; width: ${bar.widthPercent()}%;">
              </div>
            -->
            <div class="absolute top-1 bottom-1 rounded bg-blue-400"
               style="left: ${bar.leftPercent()}%; width: ${bar.widthPercent()}%;">
            </div>
            @endfor
          </div>
        </div>
      @endfor
    @endif

  </div>

</body>
</html>
