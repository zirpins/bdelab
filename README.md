# Big Data Engineering Lab - BDElab

In verschiedenen Übungsaufgaben werden Konzepte aus der Vorlesung Big Data Engineering an praktischen Beispielen nachvollzogen.

Die einzelnen Aufgaben befinden sich in Unterverzeichnissen:

- [**Lab 1**](lab1) (Fakten-/Graphmodell, Hadoop HDFS, Pail)
- [**Lab 2**](lab2) (Batch Processing mit Hadoop MapReduce)
- [**Lab 3**](lab3) (Komplexe Batch Layer Pipelines mit JCascalog)
- [**Lab 4**](lab4) (Stream Processing mit Apache Kafka und Storm)

## Allgemeine Vorbereitung
Die Aufgaben können auf allen Rechnern im Pool LI 137 (LKIT) bearbeitet werden. Die notwendige Umgebung variiert und wird in den Aufgaben erläutert. Grundsätzlich gilt:

- Die Basis aller Aufgaben bildet das vorliegende [Git Repository](https://github.com/zirpins/bdelab) auf github.
- Alle Aufgaben basieren auf Java. Hierzu wird eine Entwicklungsumgebung mit [**Java 1.7+ SDK**](http://www.oracle.com/technetwork/java/javase/downloads/index.html) (oder neuer), [**Eclipse JEE**](https://www.eclipse.org/downloads/eclipse-packages/) (oder eine andere Java IDE ihrer Wahl), [**Maven**](https://maven.apache.org/install.html), und [**Git**](https://help.github.com/articles/set-up-git/) benötigt. Diese Umgebung sollte lokal und nativ auf dem Entwicklungsrechner vorliegen (auf den LKIT-Rechnern ist dies gegeben).

## LKIT Hadoop Cluster

Ein Hadoop Cluster besteht aus Master- und Slave-Knoten. Im LKIT gibt es einen Master und 20 Slaves. Die Adressen folgen dem folgenden Schema:

![](cluster.jpg?raw=true)

Der Master Knoten dient im HDFS als sog. **NameNode** zur Verwaltung von Namen und Blöcken. Zudem ist er YARN **ResourceManager** zur Verwaltung verteilter Rechenaufträge. Die Slave Knoten dienen als HDFS **DataNodes** und YARN **NodeManager**.

### Nutzung des Clusters

Die Nutzung des Clusters erfolgt über die Kommandozeile (Shell). Hierzu dient das Skript `hadoop`. Im LKIT ist das Skript unter `/usr/local/opt/hadoop-2.7.3/bin/hadoop` installiert. Sie können es zur einfacheren Nutzung der `$PATH` Umgebungsvariable hinzufügen. Ergänzen sie dazu folgende Zeile in ihrer `~\.profile` Datei:

```
PATH="$PATH:/usr/local/opt/hadoop-2.7.3/bin"
```

### HDFS Bereiche im LKIT

Für das Labor wird je Teilnehmer ein Verzeichnis auf dem HDFS zur Verfügung gestellt. Dieses hat folgendes Muster:

```
/user/<IZ-Name>/
```

Das Verzeichnis muss für jeden Teilnehmer erst eingerichtet werden. Bitte wenden sie sich dazu an einen Betreuer.

### Hadoop Web Anwendungen 

In Hadoop gibt es einige Web-basierte Werkzeuge. Die folgenden Links funktionieren nur im Intranet der Hochschule.

- Die **NameNode Web UI** zeigt den Status und Inhalt des HDFS.
    - [LKIT NameNode Web UI](http://iwi-lkit-ux-06.hs-karlsruhe.de:50070/)

- Die **YARN Web UI** dient zur Beobachtung von MapReduce und anderen YARN Jobs.
    - [LKIT YARN Web UI](http://iwi-lkit-ux-06.hs-karlsruhe.de:8088)