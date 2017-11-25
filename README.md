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

Der Cluster dient aktuell als **verteiltes Dateisystem** ([HDFS](http://hadoop.apache.org/docs/current/hadoop-project-dist/hadoop-hdfs/HdfsDesign.html) - Hadoop Distributed File System) und **verteilter Ressourcen Manager** ([Hadoop YARN](http://hadoop.apache.org/docs/current/hadoop-yarn/hadoop-yarn-site/YARN.html) - Yet Another Resource Negotiator) für **verteilte Anwendungen** (auf Basis von [Hadoop Map Reduce](http://hadoop.apache.org/docs/current/hadoop-mapreduce-client/hadoop-mapreduce-client-core/MapReduceTutorial.html) und [Cascalog](http://cascalog.org)).

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

In Hadoop gibt es einige Web-basierte Werkzeuge. Die folgenden Links funktionieren nur im Intranet der Hochschule (auch über eine VPN Verbindung von außen).

- Die **NameNode Web UI** zeigt den Status und Inhalt des HDFS.
    - [LKIT NameNode Web UI](http://iwi-lkit-ux-06.hs-karlsruhe.de:50070/)

- Die **YARN Web UI** dient zur Beobachtung von MapReduce und anderen YARN Anwendungen.
    - [LKIT YARN Web UI](http://iwi-lkit-ux-06.hs-karlsruhe.de:8088)

### Anmeldung am Cluster von Remote per VPN und SSH

Falls Sie die Laboraufgaben nicht im LKIT bearbeiten möchten, können Sie dies per VPN und SSH auch von außerhalb durchführen.

1. Öffnen Sie eine Konsole und starten Sie eine SSH Verbindung zum Login Server der Hochschule.

    ```
    ssh <IZ-Name>@login.hs-karlsruhe.de
    ```

2. Vom Login Server aus können Sie sich nun wiederum mittels SSH auf einem beliebigen LKIT Knoten einwählen.

    ```
    ssh <IZ-Name>@iwi-lkit3-<Range-Number>.hs-karlsruhe.de
    ```

    Die `Range-Number` ist eine Zahl zwischen 01-20.

    Sie können auch eine VPN Verbindung zum Hochschulnetz aufbauen (Server: vpn.hs-karlsruhe.de) und sich dann von außerhalb direkt auf den LKIT Rechnern per SSH anmelden, ohne den Login Server zu benutzen.

#### Alternative: Einbindung eines Remote Windows 10 Rechners in den Cluster

Folgender Gist beschreibt, wie die Hadoop Anwendungen der Laborübungen auf einem entfernten Windows 10 Rechner gestartet werden können und von dort auf den LKIT-Cluster zugreifen: 

[https://gist.github.com/SheepSays/b96397ea6d93738e47304e7a129f15a0](https://gist.github.com/SheepSays/b96397ea6d93738e47304e7a129f15a0)

Dies funktioniert z.B. vom Windows Rechner zuhause mit VPN Verbindung zur Hochschule.
