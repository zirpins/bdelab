# Big Data Engineering Lab - BDElab
In verschiedenen Übungsaufgaben werden Konzepte aus der Vorlesung Big Data Engineering an praktischen Beispielen nachvollzogen.

## Allgemeine Vorbereitung
Die Aufgaben können auf Rechnern im Pool LI 137 oder auf dem eigenen Laptop bearbeitet werden. Die notwendige Umgebung variiert und wird in den Aufgaben erläutert. Grundsätzlich gilt:

- Die Basis aller Aufgaben bildet das vorliegende [Git Repository](https://github.com/zirpins/bdelab) auf github.

- Alle Aufgaben basieren auf Java. Hierzu wird eine Entwicklungsumgebung mit [**Java 1.7+ SDK**](http://www.oracle.com/technetwork/java/javase/downloads/index.html), [**Eclipse JEE**](https://www.eclipse.org/downloads/eclipse-packages/) , [**Maven**](https://maven.apache.org/install.html), und [**Git**](https://help.github.com/articles/set-up-git/) benötigt. Diese Umgebung sollte lokal und nativ auf dem Entwicklungsrechner vorliegen.

- In den meisten Aufgaben wird eine **virtualisierte Umgebung** für spezielle Tools und Plattformen bereitgestellt. Aktuell werden zur Virtualisierung [**VirtualBox**](https://www.virtualbox.org) und z.T. [**Vagrant**](https://www.vagrantup.com) verwendet und sollte auf dem Entwicklungsrechner zur Verfügung stehen.

## Lab1: Batch Storage
In der Aufgabe wird...
1. ...die Faktenbasis des Batch Layers als *Graph Schema* modelliert und mithilfe von *Apache Thrift* implementiert und 
2. ...die physikalische Speicherung von Fakten auf *Hadoop HDFS* mit dem *dfs-datastore* Framework implementiert.

### Vorbereitung
In der Aufgabe werden folgende zusätzliche Komponenten benötigt:
- Zur Generierung von Klassen für das Datenmodell wird [Apache Thrift](https://thrift.apache.org) benötigt. Konkret muss der **Thrift Compiler** lokal installiert sein.
- Als verteiltes Dateisystem wird Hadoop HDFS verwendet. Hierzu sollte eine einfache Hadoop Installation vorliegen. Wir verwenden zur Zeit [**Hadoop 2.7.0**](http://hadoop.apache.org/releases.html#Download).

Richten Sie diese Komponenten entweder auf Ihrem Entwicklungsrechner ein, oder installieren Sie die bereitgestellte **VirtualBox Appliance** `VISLABv80`.  Im Pool liegt die Virtual Appliance lokal vor und muss für Ihren Account installiert werden.

### Durchführung 
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

### Vorbereitung
Als Plattform wird **Hadoop 2.7.0 YARN** und auf dieser Basis das **MapReduce** Framework verwendet. Die Komponenten sind auf der **VirtualBox Appliance** `VISLABv82` vorbereitet. Im Pool liegt die Virtual Appliance lokal vor und muss für Ihren Account installiert werden.

### Durchführung 
Melden Sie sich an der `VISLABv82` VM an und starten Sie dort Eclipse. Aktualisieren Sie das `bdelab` Projekt (optional bei vorhandener Netzwerkverbindung).

```
cd ~vislab/git/bdelab
git pull
cd lab2
```

Die Aufgabe basiert auf dem Maven Projekt `bdelab2` im Ordner `bdelab/lab2`. Importieren Sie das Projekt zunächst wieder in Ihren Eclipse Workspace (falls nicht schon geschehen).

#### MapReduce
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

<!-- #### JCascalog -->
<!-- ##### Aufgabe 2.2 (Extended Word Count2) -->

## Referenzen

[1] Nathan Marz, James Warren, "Big Data: Principles and best practices of scalable realtime data systems", Manning, 2015
