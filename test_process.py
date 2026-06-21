import sys

for i in range (3):
    print(f"stdout_{i}")

print(f"stderr_{i}", file=sys.stderr)

print("stdout_last")
