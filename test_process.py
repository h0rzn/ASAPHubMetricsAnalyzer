import sys

# for i in range (3):
    # print(f"stdout_{i}")

# print(f"stderr_{i}", file=sys.stderr)

# print("stdout_last")

print("[REGISTER] peerId=myPeerId; canCreateTCPConnections=true; [/REGISTER]")
print("[CONNECTION_REQUEST] sourcePeerId=mySourcePeer; targetPeerId=myTargetPeer; timeoutMs=100; [/CONNECTION_REQUEST]")
print("[START_DATA_SESSION] sourcePeerId=mySourcePeer; targetPeerId=myTargetPeer; timeoutMs=100; startedAt=1784284165; endedAt=1784284165; [/START_DATA_SESSION]")
