# ASAPHubMetricsAnalyzer

A metrics collection and reporting tool for [ASAPHub](https://github.com/SharedKnowledge/ASAPHub). The analyzer captures event logs provided by the hub through stdout/stderr, persists them to a database and generates an HTML dashboard visualizing peer activity.

---

## Overview

When an ASAP Hub instance runs, it emits structured log lines describing network events (peer registrations, connection requests, ...). This tool:
1) Spawns the hub as a subprocess
2) Parses the stdout
3) stores parsed events via JPA/Hibernate into a database
4) Generates an HTML timeline report 

---

## Architecture & Technologies

```
ASAP Hub Process (subprocess through HubRunner)
        ↧ stdout
   LogProcessor  ↦  ModelBuilder  ↦  MetricsRepository  ↦  [ configured database ]
     (thread)        (parsing)         (persistence)
```
When running the report generation:
```  
ReportBuilder ↦  JTE Template  ↦  report.html
```

**Two execution phases:**
1. **Capture phase** — run alongside the hub to collect live metrics
2. **Report phase** — run standalone to generate HTML from stored data

**Data persistence**
This project uses [Hibernate](https://hibernate.org/orm/) to persist data. Tables are modelled as JPA-Entities. This means that manually writing queries is not required and the actual DBMS can be changed
to any other compatible one.

**Report generation**
Reports are generated using [JTE](https://jte.gg/) (Java Template Engine). The template file `src/main/jte/report.jte` defines the HTML structure and references the data passed in by `ReportBuilder`. 
- build time: JTE compiles template to Java bytecode.
- runtime: `ReportBuilder` collects all metrics from the database, passes them to the template engine, and writes the rendered HTML to `rep2.html`.

---

## Prerequisites

- Java 21+
- Maven 3.3+
- `ASAPHub.jar` and `ASAPJava.jar` placed in `src/main/resources/`

---

## Setup & Build

```bash
mvn compile
```

The JTE templates are compiled automatically during the build.

---

## Configuration

### Database
Different DBMS are supported. The active database configuration is read from `src/main/resources/META-INF/persistence.xml`. Rename the persistence-xml file accordingly. 
- **SQLite**: default, no setup required
- **PostgreSQL**: use `persistence-postgres.xml`; start the included Docker service with `docker-compose up -d`
- *any Hibernate-compatible database*: provide the matching JDBC driver and dialect in `persistence.xml` and add the corresponding JDBC driver dependency to `pom.xml`

SQLite creates a database file `data/hub_metrics.db`. A docker-compose-file for PostgreSQL is included.

The **Schema** is managed by [Hibernate](https://hibernate.org/orm/). Tables are created and migrated automatically on startup.

---

## Usage
**1. Start capture (run hub and record logs):**
```bash
mvn exec:java -Dexec.mainClass="de.htwberlin.Main"
```
or run `src/main/java/de/htwberlin/Main.java` directly in your IDE

**2. Generate HTML report:**
```bash
mvn exec:java -Dexec.mainClass="de.htwberlin.visualizer.ReportBuilder"
```
or run `src/main/java/de/htwberlin/visualizer/ReportBuilder.java` directly in your IDE

If you provide a schema compatible database (file) with existing data, the report generation (2.) can be run directly.

---

## Project Structure

```
src/main/java/de/htwberlin/
├── Main.java                      
├── HubRunner.java                 # Spawns ASAP Hub subprocess
├── model/                         # JPA entities (Register, ConnectionRequest, …)
├── processor/
│   ├── LogProcessor.java          # Reads hub stdout, dispatches events
│   ├── ModelBuilder.java          # Parses log lines into domain objects
│   └── MetricsRepository.java     # Persistence interface
├── persistence/
│   └── EntityManagerFactory.java  # Hibernate/JPA implementation
└── visualizer/
    ├── ReportBuilder.java          # Queries DB, builds timeline data
    └── *.java                      # Timeline helpers
src/main/jte/
└── report.jte                     # HTML template (Tailwind CSS)
src/main/resources/
├── ASAPHub.jar                    # ASAP Hub runtime (replace if outdated)
├── ASAPJava.jar                   # ASAP base library (replace if outdated)
└── META-INF/persistence.xml       # Hibernate/SQLite config
```

---

## Event Support
The following events are currently supported. Any unknown events are dropped.
Any event message is expected to have this format:
`[EVENT_TAG] field:value; field:value [/EVENT_TAG]`

| Event tag | Parsed fields | Entity |
|---|---|---|
| `REGISTER` | `peerId`, `canCreateTCPConnections` | `Register` |
| `CONNECTION_REQUEST` | `source`, `target`, `timeout` | `ConnectionRequest` |
| `START_DATA_SESSION` | `source`, `target`, `timeout`, `timeStart` | `StartDataSession` |
| `UNREGISTER` | `peerId` | `Unregister` |
| `DISCONNECT` | `source`, `target` | `Disconnect` |
| `NOTIFY_CONNECTION_ENDED` | `source`, `target`, `timeEnd` | `NotifyConnectionEnded` |