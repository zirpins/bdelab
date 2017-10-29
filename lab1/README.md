# Aufgabe 1: Batch Storage
In der Aufgabe wird...

1. ...die Faktenbasis des Batch Layers als *Graph Schema* modelliert und mithilfe von *Apache Thrift* implementiert und
2. ...die physikalische Speicherung von Fakten auf *Hadoop HDFS* mit dem *dfs-datastore* Framework implementiert.

## Lab1 Vorbereitung
In der Aufgabe werden folgende zusätzliche Komponenten  benötigt:

- Zur Generierung von Klassen für das Datenmodell wird [Apache Thrift](https://thrift.apache.org) benötigt. Konkret muss der **Thrift Compiler** lokal installiert sein.
- Als verteiltes Dateisystem wird Hadoop HDFS verwendet.

Auf den Rechnern im LKIT Cluster ist diese Umgebung direkt verfügbar.

## Lab1 Durchführung
Klonen Sie das `bdelab` Projekt.

```
mkdir -p ~/git
cd ~/git
git clone https://github.com/zirpins/bdelab.git
```

Die Aufgabe basiert auf dem Maven Projekt `bdelab1` im Ordner `bdelab/lab1`. Importieren Sie das Projekt zunächst in Ihren Eclipse Workspace oder eine IDE ihrer Wahl.

### TEIL 1: Datenmodellierung
Das Projekt `bdelab1` realisiert einen unveränderbaren Speicher für die Fakten einer einfachen **Social Media App**. Die Struktur der Fakten ist durch das Schema `src/main/thrift/schema.thrift` gegeben. Die Klassen für das Schema werden mit folgendem Skript generiert:

```
cd ~/git/bdelab/lab1
./genthrift.sh
```

Die Klassen befinden sich dann im Paket `de.hska.iwi.bdelab.schema`. Die Klasse `de.hska.iwi.bdelab.batchstore.FriendFacts` in `src/test/java` instanziiert einige Objekte des Schemas. In diesem Beispiel können sie die Verwendung der generierten Thrift Klassen sehen.

#### Aufgabe 1.1.1 (Graph Schema)
- Skizzieren Sie das Graph Schema der gegebenen Social Media Anwendung. Sie können sich dazu beliebige Fakten als Beispiele ausdenken, es sollten am Ende Beispiele für alle Eigenschaften und Beziehungen aller Entitäten zu sehen sein. 

#### Aufgabe 1.1.2 (Thrift API)
- Erweitern Sie das Schema derart, dass für alle Benutzer die Besuchten Webseiten gespeichert werden können. Sie benötigen dazu weitere Entitäten und Beziehungen. Sichern sie die Identität von Seitenbesuch-Fakten durch ein "Nonce" ab. 
- Als Hilfe können Sie sich an den Folien der Vorlesung orientieren, auf denen ein entsprechendes Schema gezeigt wurde. 
- Generieren Sie am Ende die geänderten Klassen mit dem Thrift Compiler.

### TEIL 2: Datenspeicherung
Die Faktenbasis der Social Media App wird in Dateien eines *verteilten Dateisystems* (DFS) abgelegt. Als DFS wird Hadoop HDFS verwendet.

**HDFS**

Wie im Folgenden gezeigt, kann das [Hadoop Skript](http://hadoop.apache.org/docs/stable/hadoop-project-dist/hadoop-common/CommandsManual.html) verwendet werden, um z.B. das Dateisystem zu untersuchen:

```
/usr/local/opt/hadoop-2.7.3/bin/hadoop fs -ls /path
```

Eine einfache Web GUI ist unter [http://iwi-lkit-ux-06.hs-karlsruhe.de:50070](http://iwi-lkit-ux-06.hs-karlsruhe.de:50070) verfügbar.

**DFS Datastores**

Um nicht direkt mit der generischen FileSystem API arbeiten zu müssen, wird das Framework [dfs-datastores](https://github.com/nathanmarz/dfs-datastores) verwendet, das statt einzelner Dateien sogenannte *Pails* als Speichereinheit bereitstellt.

Als Dokumentation der Pail API dienen die **Tests** des Projekts (eine gute Einführung findet sich in [1]). Es ist daher (optional) empfehlenswert, das dfs-datastores Projekt als Referenz herunterzuladen. Allerdings basiert es auf dem [Leiningen Build Tool](http://leiningen.org) von Clojure und kann nur mit dem [Counterclockwise Plugin](http://doc.ccw-ide.org) in Eclipse importiert werden.

#### Vorbereitung

Das `bdelab1` Projekt enthält zwei JUnit Testfälle `FactsIOTest.java` zur Speicherung von Fakten in Pails und `FactsOpsTests.java` um neue Fakten an eine bestehende Basis anzuhängen. Hierzu wird eine Erweiterung von Pail verwendet, die zur Serialisierung die generierten Thrift Klassen nutzt. Die entsprechenden Pail Strukturen (u.a. `ThriftPailStructure.java` und `DataPailStructure.java`) sind im Paket `manning.tap` enthalten. In den Testklassen kann mit der Konstanten `LOCAL` (Default ist `true`) zwischen lokalem und verteiltem Dateisystem umgeschaltet werden.

Führen Sie beide JUnit Tests zunächst lokal und dann auf HDFS aus:

```
cd ~/git/bdelab/lab1
mvn test
```

#### Aufgabe 1.2.1 (Pail HDFS Dateien)
- Suchen Sie im HDFS diejenigen Dateien, die durch die Pails aus den Testfällen geschrieben wurden.
- Erklären Sie die Dateistruktur des Pails `friends_pail3`. Was ist der Unterschied zum `friends_pail2`?
- Suchen Sie sich eine Pail Datei aus und finden Sie heraus, wie Sie im Cluster verteilt wurde (mit `hadoop fsck`).

#### Aufgabe 1.2.2 (Neue Fakten in Pails schreiben)
- Sie sollen nun Pail zum Import von neuen Fakten aus der lokalen Datei `pageviews.txt` im Ordner `lab1/src/main/resources` verwenden.
- Stellen Sie dazu die Klasse `Batchloader` im Paket `de.hska.iwi.bdelab.batchstore` fertig. Hier ist der Code zum Einlesen der Datei schon fertig. Es fehlt das Erstellen von Fakten und das Schreiben in Pails.
- Verwenden Sie einen temporären Pail und einen Master Pail. Schreiben Sie die Fakten zunächst in den temporären Pail und hängen Sie diesen dann an den Master Pail an (Pail#absorb).

Bauen und Starten der Anwendung

- Zum Bauen der Anwendung kann Maven verwendet werden.

```
cd ~/git/bdelab/lab1
mvn package
```

- Die Anwendung wird über das `hadoop` Skript gestartet.

```
cd ~/git/bdelab/lab1/target
hadoop jar bdelab1-0.0.1-SNAPSHOT-jar-with-dependencies.jar de.hska.iwi.bdelab.batchstore.Batchloader
```

# Referenzen
[1] Nathan Marz, James Warren, "Big Data: Principles and best practices of scalable realtime data systems", Manning, 2015
