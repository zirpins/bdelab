# Big Data Engineering Lab - BDElab
In verschiedenen Übungsaufgaben werden Konzepte aus der Vorlesung Big Data Engineering an praktischen Beispielen nachvollzogen.

## Allgemeine Vorbereitung
Die Aufgaben können auf Rechnern im Pool LI 137 oder auf dem eigenen Laptop bearbeitet werden. Die notwendige Umgebung variiert und wird in den Aufgaben erläutert. Grundsätzlich gilt:

- Die Basis aller Aufgaben bildet das vorliegende [Git Repository](https://github.com/zirpins/bdelab) auf github.

- Alle Aufgaben basieren auf Java. Hierzu wird eine Entwicklungsumgebung mit [**Java 1.7+ SDK**](http://www.oracle.com/technetwork/java/javase/downloads/index.html), [**Eclipse JEE**](https://www.eclipse.org/downloads/eclipse-packages/) , [**Maven**](https://maven.apache.org/install.html), und [**Git**](https://help.github.com/articles/set-up-git/) benötigt. Diese Umgebung sollte lokal und nativ auf dem Entwicklungsrechner vorliegen.

- In den meisten Aufgaben wird eine **virtualisierte Umgebung** mit speziellen Tools und Plattformen bereitgestellt. Aktuell werden zur Virtualisierung [**VirtualBox**](https://www.virtualbox.org) und z.T. [**Vagrant**](https://www.vagrantup.com) verwendet. Diese Virtualisierungswerkzeuge sollten auf dem Entwicklungsrechner zur Verfügung stehen.

## Lab1: Batch Storage
In der Aufgabe wird...
1. ...die Faktenbasis des Batch Layers als *Graph Schema* modelliert und mithilfe von *Apache Thrift* implementiert und
2. ...die physikalische Speicherung von Fakten auf *Hadoop HDFS* mit dem *dfs-datastore* Framework implementiert.

### Lab1 Vorbereitung
In der Aufgabe werden folgende zusätzliche Komponenten benötigt:
- Zur Generierung von Klassen für das Datenmodell wird [Apache Thrift](https://thrift.apache.org) benötigt. Konkret muss der **Thrift Compiler** lokal installiert sein.
- Als verteiltes Dateisystem wird Hadoop HDFS verwendet. Hierzu sollte eine einfache Hadoop Installation vorliegen. Wir verwenden zur Zeit [**Hadoop 2.7.0**](http://hadoop.apache.org/releases.html#Download).

Richten Sie diese Komponenten entweder auf Ihrem Entwicklungsrechner ein, oder installieren Sie die bereitgestellte **VirtualBox Appliance** `VISLABv80`.  Im Pool liegt die Virtual Appliance lokal vor und muss für Ihren Account installiert werden.

### Lab1 Durchführung
Melden Sie sich an der `VISLABv80` VM an und starten Sie dort Eclipse. Aktualisieren Sie das `bdelab` Projekt.

```
cd ~vislab/git/bdelab
git pull
cd lab1
```

Die Aufgabe basiert auf dem Maven Projekt `bdelab1` im Ordner `bdelab/lab1`. Importieren Sie das Projekt zunächst in Ihren Eclipse Workspace (falls nicht schon geschehen).

#### Datenmodellierung
Das Projekt `bdelab1` realisiert einen unveränderbaren Speicher für die Fakten einer einfachen **Social Media App**. Die Struktur der Fakten ist durch das Schema `src/main/thrift/schema.thrift` gegeben. Die Klassen für das Schema werden mit folgendem Skript generiert:

```
cd ~vislab/git/bdelab/lab1
./genthrift.sh
```

Die Klassen befinden sich dann im Paket `de.hska.iwi.bdelab.schema`. Die Klasse `de.hska.iwi.bdelab.batchstore.FriendFacts` in `src/test/java` instanziiert einige Objekte des Schemas.

##### Aufgabe 1.1 (Graph Schema)
- Skizzieren Sie das Graph Schema der gegebenen Social Media Anwendung.

##### Aufgabe 1.2 (Thrift API)
- Erweitern Sie das Schema derart, dass für alle Benutzer die Besuchten Webseiten gespeichert werden können. Generieren Sie dann die Klassen mit dem Thrift Compiler.

#### Datenspeicherung
Die Faktenbasis der Social Media App wird in Dateien eines *verteilten Dateisystems* (DFS) abgelegt. Als DFS wird Hadoop HDFS verwendet. Auf der `VISLABv80` VM ist Hadoop in der Version 2.7.0 als *Single Host Cluster* für den User `hadoop` installiert. Das HDFS Subsystem wird wie folgt gestartet:

```
su - hadoop
start-dfs.sh
```

Als User `hadoop` können die [Hadoop Skripte](http://hadoop.apache.org/docs/stable/hadoop-project-dist/hadoop-common/CommandsManual.html) verwendet werden, um z.B. das Dateisystem zu untersuchen:

```
hadoop fs -ls /user/hadoop
```

Eine einfache Web GUI ist unter [http://localhost:50070/](http://localhost:50070/) verfügbar.

Um nicht direkt mit der generischen FileSystem API arbeiten zu müssen, wird das Framework [dfs-datastores](https://github.com/nathanmarz/dfs-datastores) verwendet, das statt einzelner Dateien sogenannte *Pails* als Speichereinheit bereitstellt.

Als Dokumentation der Pail API dienen die Tests des Projekts (eine gute Einführung findet sich in [1]). Es ist daher (optional) empfehlenswert, das dfs-datastores Projekt als Referenz herunterzuladen (auf der VM schon geschehen). Allerdings basiert es auf dem [Leiningen Build Tool](http://leiningen.org) von Clojure und kann nur mit dem [Counterclockwise Plugin](http://doc.ccw-ide.org) in Eclipse importiert werden.  

Das `bdelab1` Projekt enthält zwei JUnit Testfälle `FactsIOTest.java` zur Speicherung von Fakten in Pails und `FactsOpsTests.java` um neue Fakten an eine bestehende Basis anzuhängen. Hierzu wird eine Erweiterung von Pail verwendet, die zur Serialisierung die generierten Thrift Klassen nutzt. Die entsprechenden Pail Strukturen (u.a. `ThriftPailStructure.java` und `DataPailStructure.java`) sind im Paket `manning.tap` enthalten. In den Testklassen kann mit der Konstanten `LOCAL` (Default ist `true`) zwischen lokalem und verteiltem Dateisystem umgeschaltet werden.

Führen Sie in der VM beide JUnit Tests zunächst lokal und dann auf HDFS aus (vergewissern Sie sich vorher, dass das Hadoop HDFS Subsystem gestartet wurde).

##### Aufgabe 1.3 (Pail HDFS Dateien)
- Suchen Sie im HDFS diejenigen Dateien, die durch die Pails aus den Testfällen geschrieben wurden.
- Erklären Sie die Dateistruktur des Pails `friends_pail3`. Was ist der Unterschied zum `friends_pail2`?
- Suchen Sie sich eine Pail Datei aus und finden Sie heraus, wie Sie im "Cluster" verteilt wurde.

##### Aufgabe 1.4 (Neue Fakten in Pails schreiben)
- Sie sollen nun Pail zum Import von neuen Fakten aus der lokalen Datei `pageviews.txt` im Ordner `lab1/src/main/resources` verwenden.
- Stellen Sie dazu die Klasse `Batchloader` im Paket `de.hska.iwi.bdelab.batchstore` fertig. Hier ist der Code zum Einlesen der Datei schon fertig. Es fehlt das Erstellen von Fakten und das Schreiben in Pails.
- Verwenden Sie einen temporären Pail und einen Master Pail. Schreiben Sie die Fakten zunächst in den temporären Pail und hängen Sie diesen dann an den Master Pail an (Pail#absorb).

## Lab2: Batch Processing
In der Aufgabe wird ein einfaches Beispiel für die Batch Verarbeitung mit MapReduce betrachtet und auf die Verarbeitung von Fakten in Pails übertragen. <!--  Optional wird dann noch JCascalog als Ansatz zur deklarativen Beschreibung von Batch Verarbeitung auf höherem Abstraktionsgrad verwendet. -->

### Lab2 Vorbereitung
Als Plattform wird **Hadoop 2.7.0 YARN** und auf dieser Basis das **MapReduce** Framework verwendet. Die Komponenten sind auf der **VirtualBox Appliance** `VISLABv82` vorbereitet. Im Pool liegt die Virtual Appliance lokal vor und muss für Ihren Account installiert werden.

### <a name="lab2durchführung">Lab2 Durchführung</a>
Melden Sie sich an der `VISLABv82` VM an und starten Sie dort Eclipse. Aktualisieren Sie das `bdelab` Projekt (optional bei vorhandener Netzwerkverbindung).

```
cd ~vislab/git/bdelab
git pull
cd lab2
```

Die Aufgabe basiert auf dem Maven Projekt `bdelab2` im Ordner `bdelab/lab2`. Importieren Sie das Projekt zunächst wieder in Ihren Eclipse Workspace (falls nicht schon geschehen).

#### <a name="lab2mapreduce">MapReduce</a>
Die Vorberechnung von Views aus der Faktenbasis erfolgt durch parallele Batch Verarbeitung in einem Cluster. Als Clustersystem wird Hadoop YARN verwendet, das die Verteilung von parallelen Jobs auf (theoretisch) viele Rechnerknoten übernimmt. Auf der `VISLABv82` VM ist Hadoop in der Version 2.7.0 als Single Host Cluster für den User `hadoop` installiert. Für die Verarbeitung müssen sowohl HDFS als auch YARN gestartet werden (verwenden Sie dazu am besten ein separates Terminal):

```
su - hadoop
start-dfs.sh
start-yarn.sh
```

Mit `jps` können Sie die Java Prozesse auflisten, die die Hadoop Komponenten enthalten sollten.

```
jps
3285 ResourceManager
3434 NodeManager
2922 DataNode
2747 NameNode
3135 SecondaryNameNode
```

Nun können Jobs mit dem `hadoop` Befehl eingestellt werden.

##### Aufgabe 2.1 (WordCount)
Zunächst soll ein einfaches Beispiel betrachtet werden. Im Paket `de.hska.iwi.bdelab.batchjobs` liegen die Klassen `WordCountOldAPI` und `WordCountNewAPI` mit MapReduce Jobs für das klassische Word Count Beispiel. Für MapReduce existieren zwei APIs: `org.apache.hadoop.mapred` und die neuere `org.apache.hadoop.mapreduce`. Die APIs unterscheiden sich leicht in der Verwendung, sind aber weitgehend äquivalent. Ein [Tutorium](http://hadoop.apache.org/docs/r1.2.1/mapred_tutorial.html) findet sich auf der Hadoop Webseite.

Vor der Verwendung muss das Projekt mit maven erstellt werden:
```
cd ~vislab/git/bdelab/lab2
mvn package
```

Der Job wird dann als User `hadoop` ausgeführt. Dieser schreibt davor noch Eingabedaten in das HDFS:
```
su - hadoop
hadoop fs -mkdir -p wc/in
echo "Hello World Bye World" > file01
echo "Hello Hadoop Goodbye Hadoop" > file02
hadoop fs -put file01 wc/in
hadoop fs -put file02 wc/in
```

Dann kann der Job gestartet werden:
```
hadoop jar ~vislab/git/bdelab/lab2/target/bdelab2-0.0.1-SNAPSHOT-jar-with-dependencies.jar de.hska.iwi.bdelab.batchjobs.WordCountOldAPI wc/in wc/out
```

Das Ergebnis findet sich im Anschluss im HDFS:
```
hadoop fs -cat wc/out/part-00000
```

Zur Wiederholung des Experiments muss der Ordner `wc/out` wieder gelöscht werden:

```
hadoop fs -rm -skipTrash wc/out/*
hadoop fs -rmdir wc/out
```

##### Aufgabe 2.2 (Pageviews)
MapReduce Jobs können ihre Daten aus vielen Quellen beziehen. Die Klasse `CountFacts` zählt z.B. die Anzahl der Fakten eines Batch Stores auf Basis von Pails.

Um zunächst eine Datenbasis zu erstellen, kann die Klasse `Batchloader` als Java Anwendung gestartet werden (am einfachsten in Eclipse). Der `Batchloader` liest Pageviews aus einer Textdatei und schreibt sie in einen Pail. Dann kann der Job gestartet werden:
```
su - hadoop
hadoop jar ~vislab/git/bdelab/lab2/target/bdelab2-0.0.1-SNAPSHOT-jar-with-dependencies.jar de.hska.iwi.bdelab.batchjobs.CountFakts
```

Das Ergebnis findet sich im Anschluss im HDFS:
```
hadoop fs -cat tmp/bdetests/mapredout/part-00000
```

Schauen sie sich den Quelltext von `CountFacts` an. Dort sehen Sie die Konfiguration des Jobs zur Verwendung des Pails. Beachten Sie auch den Mapper: Hier werden die eingehenden Bytes mit dem generierten Thrift Serializer in Data Objekte gewandelt und können in der map Funktion verarbeitet werden.

**Aufgabe:** erstellen Sie einen MapReduce Job zur Berechnung eines Pageview Index. Der Index soll für alle vorkommenden Stundenintervalle die Anzahl der Aufrufe einzelner Seiten zählen. Die Ausgabe soll als Textdatei der Form `<Datum/Stunde> <URL> <Anzahl der Aufrufe>` erfolgen. Orientieren Sie sich dafür am `CountFacts` Job

## Lab3: Batch Workflow / Cascalog
Die Aufgabe vertieft die Verarbeitungsschritte im Batch Layer. Es wird **Cascalog** als Framework für die deklarative Erstellung komplexer MapReduce Verarbeitungsprozesse eingeführt. Auf Basis von Cascalog wird ein einfacher Batch Workflow mit Daten-Import, -Normalisierung und -Vorverarbeitung realisiert.

### Lab3 Vorbereitung
Als Plattform wird weiterhin **Hadoop 2.7.0 YARN** verwendet. Die Umgebung ist auf der **VirtualBox Appliance** `VISLABv83` vorbereitet. Im Pool liegt die Virtual Appliance lokal vor (`/usr/local/share/...`) und muss für Ihren Account installiert werden.

### Lab3 Durchführung
Melden Sie sich an der `VISLABv83` VM an und starten Sie dort Eclipse. Aktualisieren Sie falls nötig das `bdelab` Projekt (siehe [Lab2 Durchführung](#lab2durchführung)). Die Aufgabe basiert auf dem Maven Projekt `bdelab3` im Ordner `bdelab/lab3`. Importieren Sie das Projekt zunächst wieder in Ihren Eclipse Workspace (falls nicht schon geschehen).

#### Cascalog
Cascalog ist ein Framework zur deklarativen Beschreibung komplexer Abfragen, die als MapReduce Programme auf einem Hadoop YARN Cluster ausgeführt werden können. Cascalog ist in *Clojure* geschrieben, wir verwenden die zugehörige Java API **JCascalog** zur Programmierung von Cascalog Anfragen in Java.

Eine praktische Einführung in die wichtigsten und für die Aufgabe notwendigen Grundlagen liegt als kurzer Foliensatz vor. Weitere [Cascalog Dokumentation](https://github.com/nathanmarz/cascalog/wiki/JCascalog) findet sich im Web.

Zur Ausführung der Cascalog Programme muss wieder Hadoop aktiviert werden. Öffnen Sie eine Konsole als User `hadoop` und starten Sie sowohl HDFS als auch YARN (siehe [Lab2 MapReduce](#lab2mapreduce)).

##### Aufgabe 3.1 Cascalog CountFacts
Als erste Schritte mit JCascalog sollen Sie mit einigen Beispielen experimentieren. Im Projekt bdelab3 ist die schon bei MapReduce gesehene **CountFacts** Abfrage in JCascalog implementiert. Die Abfrage finden Sie in der Klasse `de.hska.iwi.bdelab.batchqueries.CountFacts2`. Viele weitere Beispiele für Cascalog Abfragen finden Sie im Paket `de.hska.iwi.bdelab.batchqueries.examples`.

Um neue Fakten zu generieren, existiert eine modifizierte Variante des schon bekannten `Batchloader`. Mit der Option `-r` setzt das Programm die Faktenbasis zurück. Ansonsten werden 1000 Fakten in einen Pail für neue Daten eingefügt. Mit der Option `-m` werden die Daten auch in den Master Pail übernommen. Führen Sie an dieser Stelle den `Batchloader` mit beiden Optionen aus:
```
hadoop jar /home/vislab/git/bdelab/lab3/target/bdelab3-0.0.1-SNAPSHOT-jar-with-dependencies.jar de.hska.iwi.bdelab.batchstore.Batchloader -r -m
```

Nun starten Sie `CountFacts2` wie folgt:
```
hadoop jar /home/vislab/git/bdelab/lab3/target/bdelab3-0.0.1-SNAPSHOT-jar-with-dependencies.jar de.hska.iwi.bdelab.batchqueries.CountFacts2
```

Prüfen Sie das Ergebnis. Generieren Sie mehr Daten (`Batchloader -m` *ohne* `-r`).

##### Aufgabe 3.2 URL Normalisierung
Die Klasse `de.hska.iwi.bdelab.batchqueries.SimpleBatchWorkflow` implementiert eine Verarbeitungskette im Batch Layer mit Aufnahme neuer Daten, Normalisierung von URLs und Vorberechnung von Pageviews für mehrere Intervall-Granularitäten.

Führen Sie vor dem Start den `Batchloader -r` aus. Dadurch werden alle Dateien zunächst gelöscht und dann 1000 Einträge in den Pail für neue Daten eingetragen, noch nicht jedoch in den Master Pail (dies tut der `SimpleBatchWorkflow` selbst). Sie starten den `SimpleBatchWorkflow` mit:
```
hadoop jar /home/vislab/git/bdelab/lab3/target/bdelab3-0.0.1-SNAPSHOT-jar-with-dependencies.jar de.hska.iwi.bdelab.batchqueries.SimpleBatchWorkflow
```

**Aufgabe:** Die Cascalog Query für die URL-Normalisierung ist noch nicht implementiert (sie ist zwar vorhanden, tut aber noch nichts sinnvolles). In diesem Projekt enthalten die Fakten unterschiedliche URL-Varianten für die gleichen Ressourcen. Implementieren Sie die URL-Normalisierung aus der Vorlesung, so dass ein korrekter Pageview Index berechnet wird.

## Lab4: Stream Processing / Storm
Die Aufgabe veranschaulicht die Verarbeitung im Speed Layer. Wir betrachten dabei One-at-a-Time Streaming auf Basis von Storm. Als Datenquelle dient eine Multi-Consumer-Queue auf Basis von Kafka.  

Mit dieser Konfiguration untersuchen wir zunächst das *Beispiel einer einfachen Word Count Implementierung*. Die Aufgabe besteht dann in der *Implementierung des Pageviews-pro-Zeit Index als Storm Topologie*.

### Lab4 Hintergrund
[Apache Storm](http://storm.apache.org/) realisiert eine Plattform für One-at-a-Time (und Micro-batched) Stream Verarbeitung in Cluster-Umgebungen. Verarbeitungsprozesse werden durch *Topologien* aus *Spouts* und *Bolts* spezifiziert, die vom zentralen *Nimbus* als *Tasks* auf verschiedene *Worker* Knoten eines Clusters verteilt werden (siehe [Apache Storm Concepts](http://storm.apache.org/releases/1.0.2/Concepts.html)).

Wir nutzen eine Möglichkeit von Storm aus, Topologien auch ohne verteilte Infrastruktur auf einem `LocalCluster` innerhalb eines Java Prozesses auszuführen (siehe [Apache Storm Local Mode](http://storm.apache.org/releases/1.0.2/Local-mode.html)).

Solche Streaming Prozesse können leicht als Java Anwendungen aus einer IDE wie Eclipse oder IntelliJ gestartet werden.

[Apache Kafka](http://kafka.apache.org/) stellt eine performante Infrastruktur für die Verwaltung, Speicherung und Verarbeitung von Datenströmen bereit. Kafka kann u.a. replizierte und partitionierte Message Queues bereitstellen.

Insbesondere können Kafka *Topics* als Multi-Consumer-Queues genutzt werden, bei denen *Records*, bestehend aus *Name* und *Value* für verschiedene Streaming Prozesse (ConsumerGroups) genutzt und bei Bedarf gemäß ihrer Position wiederholt werden können (siehe [Apache Kafka Intro](http://kafka.apache.org/intro)).

Die Semantik von Kafka Topics passt optimal zur Stream-Verarbeitung in Storm und wird dort zur garantierten Verarbeitung (at-least-once) von Nachrichten verwendet.

### Lab4 Vorbereitung
Als Infrastruktur benötigen wir nur **Kafka 0.9+** (für Storm nutzen wir einen eingebetteten "Cluster"). Die Umgebung ist auf der **VirtualBox Appliance** `VISLABv84` vorbereitet. Im Pool liegt die Virtual Appliance lokal vor (`/usr/local/share/...`) und muss für Ihren Account installiert werden.

Kafka kann aber auch sehr leicht selbst installiert werden (siehe [Kafka Quickstart](http://kafka.apache.org/quickstart)).

### Lab4 Durchführung
Melden Sie sich an der `VISLABv84` VM an und starten Sie dort Eclipse. Aktualisieren Sie falls nötig das `~vislab/git/bdelab` Projekt (siehe [Lab2 Durchführung](#lab2durchführung)). Die Aufgabe basiert auf dem Maven Projekt `storm-word-count` im Ordner `~vislab/git/bdelab/lab4/stormwordcount`. Importieren Sie das Projekt zunächst wieder in Ihren Eclipse Workspace (falls nicht schon geschehen).

### Aufgabe 4.1
Probieren Sie das Wordcount Beispiel aus. Hierzu muss zunächst Kafka vorbereitet werden, danach kann die Storm Topology zur Ausführung kommen.

#### Kafka Wordcount Topic und Producer aufsetzen
Das schnelle Aufsetzen von Kafka wird in [Kafka Quickstart](http://kafka.apache.org/quickstart) beschrieben. Folgen Sie dieser Anleitung und
- ...starten sie den **Zookeeper Server**
- ...starten Sie den **Kafka Server**
- ...richten Sie ein **Topic** `sentence` ein
- ...starten Sie einen **Producer** für `sentence`
- ...testen Sie das Topic mit einem **Consumer**

Auf `VISLABv84` ist Kafka im Verzeichnis `~vislab/lib/kafka_2.11-0.10.1.0` installiert.

#### Storm Topologie ausführen
Öffnen Sie das *storm-word-count Projekt* in Ihrer IDE. `WordcountTopology` ist im Package `de.hska.iwi.vsys.bdelab.streaming` spezifiziert. Die Topologie nutzt [storm-kafka-client](https://github.com/apache/storm/tree/v1.0.2/external/storm-kafka-client) zur Definition des `SentenceSpout` (als *Consumer* des Kafka Topics `sentence`).

Starten Sie `WordcountTopology` als Java-Anwendung in der IDE. Geben Sie nun einige Sätze in den Command Line Producer ein und beobachten Sie die Ausgaben der Topologie, um die Verarbeitung nachzuverfolgen. (Sie können auch in der der `WordcountTopology` die Debug-Ausgabe aktivieren, um u.a. das "Acking" der Tupel zu beobachten.)

Versuchen Sie, die Implementierung der Topologie mit Spout und Bolts nachzuvollziehen.

### Aufgabe 4.2
Die eigentliche Aufgabe besteht nun darin, die Berechnung des Pageview-pro-Zeit Index als Storm Topologie zu implementieren.

#### Kafka vorbereiten
Legen Sie zunächst ein passendes Kafka Topic für Pageview Ereignisse an. Gehen Sie für die Kafka Record Values (d.h. die Nachrichteninhalte) vom Format der vorangegangenen Aufgaben aus:

```
<IP> <URL> <EPOCH-TIME>
```

(siehe Datei `pageviews2.txt` aus bdelab3).

#### Storm Topologie
Für die Pageview Topologie können Sie praktischerweise das storm-word-count Projekt kopieren und anpassen.

- Passen Sie die Konfiguration des Kafka Spout an.
- Realisieren Sie Datenextraktion, URL-Normalisierung, Aufteilung in Stunden-Buckets und Zählerfunktion als Bolts.
- Kombinieren Sie alle Komponenten mit passenden Stream Groupings zu einer Topologie.

#### Ausführung
Starten Sie die Topologie in der IDE. Zur Erzeugung eines Streams kopieren Sie den Inhalt der Pageview Datei aus Aufgabe 3 per Copy/Paste in den Kafka Command Line Producer.

## Referenzen

[1] Nathan Marz, James Warren, "Big Data: Principles and best practices of scalable realtime data systems", Manning, 2015
