# Aufgabe 2: MapReduce TODO ANPASSEN NACH MIGRATION
## <a name="lab2vorbereitung">Lab2 Vorbereitung</a> 
Als Plattform wird **Hadoop 2.7.0 YARN** und auf dieser Basis das **MapReduce** Framework verwendet. Die Komponenten sind auf der **VirtualBox Appliance** `VISLABv82` (oder neuer) vorbereitet. (Im Pool liegt die Virtual Appliance lokal vor (`/usr/local/share/vbox/`) und muss für Ihren Account installiert werden: öffnen sie dazu die Anwendung `virtualbox` und wählen sie im Dateimenü den Befehl zum importieren einer Appliance. Wählen sie die aktuellste Version der VM.)

## <a name="lab2durchführung">Lab2 Durchführung</a>
Melden Sie sich an der `VISLABv82+` VM an und starten Sie dort Eclipse. Aktualisieren Sie das `bdelab` Projekt (optional bei vorhandener Netzwerkverbindung).

```
cd ~vislab/git/bdelab
git pull
cd lab2
```

Die Aufgabe basiert auf dem Maven Projekt `bdelab2` im Ordner `bdelab/lab2`. Importieren Sie das Projekt zunächst wieder in Ihren Eclipse Workspace (falls nicht schon geschehen).

### <a name="lab2mapreduce">MapReduce</a>
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

#### Aufgabe 2.1 (WordCount)
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

#### Aufgabe 2.2 (Pageviews)
MapReduce Jobs können ihre Daten aus vielen Quellen beziehen. Die Klasse `CountFacts` zählt z.B. die Anzahl der Fakten eines Batch Stores auf Basis von Pails.

Um zunächst eine Datenbasis zu erstellen, kann die Klasse `Batchloader` als Java Anwendung gestartet werden (am einfachsten in Eclipse). Der `Batchloader` liest Pageviews aus einer Textdatei und schreibt sie in einen Pail. Dann kann der Job gestartet werden:
```
su - hadoop
hadoop jar ~vislab/git/bdelab/lab2/target/bdelab2-0.0.1-SNAPSHOT-jar-with-dependencies.jar de.hska.iwi.bdelab.batchjobs.CountFacts
```

Das Ergebnis findet sich im Anschluss im HDFS:
```
hadoop fs -cat tmp/bdetests/mapredout/part-00000
```

Schauen sie sich den Quelltext von `CountFacts` an. Dort sehen Sie die Konfiguration des Jobs zur Verwendung des Pails. Beachten Sie auch den Mapper: Hier werden die eingehenden Bytes mit dem generierten Thrift Serializer in Data Objekte gewandelt und können in der map Funktion verarbeitet werden.

**Aufgabe:** erstellen Sie einen MapReduce Job zur Berechnung eines Pageview Index. Der Index soll für alle vorkommenden Stundenintervalle die Anzahl der Aufrufe einzelner Seiten zählen. Die Ausgabe soll als Textdatei der Form `<Datum/Stunde> <URL> <Anzahl der Aufrufe>` erfolgen. Orientieren Sie sich dafür am `CountFacts` Job