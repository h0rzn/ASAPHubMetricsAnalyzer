import time

PEERS = [
    {"peerId": "peer-alpha", "canCreateTCPConnections": "true"},
    {"peerId": "peer-beta",  "canCreateTCPConnections": "true"},
    {"peerId": "peer-gamma", "canCreateTCPConnections": "false"},
]

CONNECTIONS = [
    {"source": "peer-alpha", "target": "peer-beta",  "timeoutMs": 100, "durationMs": 100},
    {"source": "peer-beta",  "target": "peer-gamma", "timeoutMs": 200, "durationMs": 200},
    {"source": "peer-alpha", "target": "peer-gamma", "timeoutMs": 300, "durationMs": 300},
]

def emit(tag, fields):
    body = "; ".join(f"{k}={v}" for k, v in fields.items())
    print(f"[{tag}] {body} [/{tag}]", flush=True)


def now():
    return int(time.time() * 1000)

for peer in PEERS:
    emit("REGISTER", {
        "peerId": peer["peerId"],
        "canCreateTCPConnections": peer["canCreateTCPConnections"]
    })

for conn in CONNECTIONS:
    emit("CONNECTION_REQUEST", {
        "sourcePeerId": conn["source"],
        "targetPeerId": conn["target"],
        "timeoutMs":    conn["timeoutMs"],
    })

    started = now()
    time.sleep(conn["durationMs"] / 1000)
    ended = now()

    emit("START_DATA_SESSION", {
        "sourcePeerId": conn["source"],
        "targetPeerId": conn["target"],
        "timeoutMs":    conn["timeoutMs"],
        "startedAt":    started,
        "endedAt":      ended,
    })

for peer in PEERS:
    emit("UNREGISTER", {"peerId": peer["peerId"]})
