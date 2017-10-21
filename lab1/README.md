# Aufgabe 1: Batch Storage TODO 1.2 Datenspeicherung Pfad anpassen
In der Aufgabe wird...
1. ...die Faktenbasis des Batch Layers als *Graph Schema* modelliert und mithilfe von *Apache Thrift* implementiert und
2. ...die physikalische Speicherung von Fakten auf *Hadoop HDFS* mit dem *dfs-datastore* Framework implementiert.

## Lab1 Vorbereitung
In der Aufgabe werden folgende zusätzliche Komponenten benötigt:
- Zur Generierung von Klassen für das Datenmodell wird [Apache Thrift](https://thrift.apache.org) benötigt. Konkret muss der **Thrift Compiler** lokal installiert sein.
- Als verteiltes Dateisystem wird Hadoop HDFS verwendet.

## Lab1 Durchführung
Aktualisieren Sie das `bdelab` Projekt.

```
cd ~vislab/git/bdelab
git pull
cd lab1
```

Die Aufgabe basiert auf dem Maven Projekt `bdelab1` im Ordner `bdelab/lab1`. Importieren Sie das Projekt zunächst in Ihren Eclipse Workspace (falls nicht schon geschehen).

### Datenmodellierung
Das Projekt `bdelab1` realisiert einen unveränderbaren Speicher für die Fakten einer einfachen **Social Media App**. Die Struktur der Fakten ist durch das Schema `src/main/thrift/schema.thrift` gegeben. Die Klassen für das Schema werden mit folgendem Skript generiert:

```
cd ~vislab/git/bdelab/lab1
./genthrift.sh
```

Die Klassen befinden sich dann im Paket `de.hska.iwi.bdelab.schema`. Die Klasse `de.hska.iwi.bdelab.batchstore.FriendFacts` in `src/test/java` instanziiert einige Objekte des Schemas.

#### Aufgabe 1.1 (Graph Schema)
- Skizzieren Sie das Graph Schema der gegebenen Social Media Anwendung.

#### Aufgabe 1.2 (Thrift API)
- Erweitern Sie das Schema derart, dass für alle Benutzer die Besuchten Webseiten gespeichert werden können. Generieren Sie dann die Klassen mit dem Thrift Compiler.

### Datenspeicherung
Die Faktenbasis der Social Media App wird in Dateien eines *verteilten Dateisystems* (DFS) abgelegt. Als DFS wird Hadoop HDFS verwendet.

TODO
WIe folgt gezeigt können die [Hadoop Skripte](http://hadoop.apache.org/docs/stable/hadoop-project-dist/hadoop-common/CommandsManual.html) verwendet werden, um z.B. das Dateisystem zu untersuchen:

```
/usr/local/opt/hadoop-2.7.3/bin/hadoop fs -ls /path/to/your/space
```

Eine einfache Web GUI ist unter [http://iwi-lkit-ux-06.hs-karlsruhe.de:50070](http://iwi-lkit-ux-06.hs-karlsruhe.de:50070) verfügbar.

Um nicht direkt mit der generischen FileSystem API arbeiten zu müssen, wird das Framework [dfs-datastores](https://github.com/nathanmarz/dfs-datastores) verwendet, das statt einzelner Dateien sogenannte *Pails* als Speichereinheit bereitstellt.

Als Dokumentation der Pail API dienen die Tests des Projekts (eine gute Einführung findet sich in [1]). Es ist daher (optional) empfehlenswert, das dfs-datastores Projekt als Referenz herunterzuladen (auf der VM schon geschehen). Allerdings basiert es auf dem [Leiningen Build Tool](http://leiningen.org) von Clojure und kann nur mit dem [Counterclockwise Plugin](http://doc.ccw-ide.org) in Eclipse importiert werden.

Das `bdelab1` Projekt enthält zwei JUnit Testfälle `FactsIOTest.java` zur Speicherung von Fakten in Pails und `FactsOpsTests.java` um neue Fakten an eine bestehende Basis anzuhängen. Hierzu wird eine Erweiterung von Pail verwendet, die zur Serialisierung die generierten Thrift Klassen nutzt. Die entsprechenden Pail Strukturen (u.a. `ThriftPailStructure.java` und `DataPailStructure.java`) sind im Paket `manning.tap` enthalten. In den Testklassen kann mit der Konstanten `LOCAL` (Default ist `true`) zwischen lokalem und verteiltem Dateisystem umgeschaltet werden.

Führen Sie in der VM beide JUnit Tests zunächst lokal und dann auf HDFS aus.

#### Aufgabe 1.3 (Pail HDFS Dateien)
- Suchen Sie im HDFS diejenigen Dateien, die durch die Pails aus den Testfällen geschrieben wurden.
- Erklären Sie die Dateistruktur des Pails `friends_pail3`. Was ist der Unterschied zum `friends_pail2`?
- Suchen Sie sich eine Pail Datei aus und finden Sie heraus, wie Sie im Cluster verteilt wurde.

#### Aufgabe 1.4 (Neue Fakten in Pails schreiben)
- Sie sollen nun Pail zum Import von neuen Fakten aus der lokalen Datei `pageviews.txt` im Ordner `lab1/src/main/resources` verwenden.
- Stellen Sie dazu die Klasse `Batchloader` im Paket `de.hska.iwi.bdelab.batchstore` fertig. Hier ist der Code zum Einlesen der Datei schon fertig. Es fehlt das Erstellen von Fakten und das Schreiben in Pails.
- Verwenden Sie einen temporären Pail und einen Master Pail. Schreiben Sie die Fakten zunächst in den temporären Pail und hängen Sie diesen dann an den Master Pail an (Pail#absorb).