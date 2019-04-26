# Aufgabe 2: MapReduce

In der Aufgabe wird ...

1. ...der praktische Umgang mit *Map Reduce* anhand von Beispielen eingeübt.
2. ...die Vorberechnung konkreter Abfragen eines Big Data Systems mittels
   Neuberechnung von *Batch Views*  implementiert.

## Lab2 Vorbereitung

Als Plattform wird der verteilte Ressourcen Manager [**Hadoop YARN
2.7.3**](http://hadoop.apache.org/docs/current/hadoop-yarn/hadoop-yarn-site/YARN.html)
für verteilte Anwendungen auf Basis von [**Hadoop Map
Reduce**](http://hadoop.apache.org/docs/current/hadoop-mapreduce-client/hadoop-mapreduce-client-core/MapReduceTutorial.html)
verwendet.

## Lab2 Durchführung

Melden Sie sich im LKIT Pool an und starten Sie dort Eclipse (alternativ ist
z.B. auch Atom verfügbar). Aktualisieren Sie das `bdelab` Git Repository.

```bash
cd ~/git/bdelab # bzw eigener Pfad
git pull
cd lab2
```

Die Aufgabe basiert auf dem Maven Projekt `bdelab2` im Ordner `bdelab/lab2`.
Importieren Sie das Projekt zunächst wieder in Ihren Eclipse Workspace.

Im Projekt muss wieder eine Konfigurationsdatei angelegt werden, um den
IZ-Accountnamen für HDFS zu spezifizieren. Dazu ist ein Template vorgegeben, das
sie wie folgt kopieren und anpassen können:

```bash
cd ~/git/bdelab/lab2/src/main/resources
cp template.hadoop.properties hadoop.properties
vim hadoop.properties # file editieren
```

Ändern Sie die Property `hadoop.user.name` auf ihren IZ-Accountnamen.

Das Projekt basiert auf dem vorherigen Projekt `bdelab1`, **daher muss bdelab1 mit
Maven installiert werden**. Dies geschieht wie folgt:

```bash
cd ~/git/bdelab/lab1
mvn install
```

Nun kann das Projekt mit Maven gebaut werden:

```bash
cd ~/git/bdelab/lab2 # bzw eigener Pfad
./genthrift.sh # Geänderte Thrift Klassen für Aufgabe 2.2 generieren
mvn install
```

### MapReduce

Die Vorberechnung von Views aus der Faktenbasis erfolgt durch parallele Batch
Verarbeitung in einem Cluster. Als Plattform für das Management der Ressourcen
im Cluster wird Hadoop YARN verwendet. Das Map Reduce Framework übernimmt die
Verteilung von parallelen Jobs auf unterschiedliche Rechnerknoten. Im LKIT ist
Hadoop (zZ in der Version 2.7.3) als Multi-Node Cluster installiert und läuft
dort permanent. Jobs können mit dem `hadoop` (oder auch `yarn`) Skript
eingestellt werden.

#### Aufgabe 2.1 (WordCount)

Zum warm werden betrachten wir ein einfaches Beispiel. Im Paket
`de.hska.iwi.bdelab.batchjobs` liegen die Klassen `WordCountOldAPI` und
`WordCountNewAPI` mit MapReduce Jobs für das klassische Word Count Beispiel. Für
MapReduce existieren zwei APIs: `org.apache.hadoop.mapred` und die neuere
`org.apache.hadoop.mapreduce`. Die APIs unterscheiden sich leicht in der
Verwendung, sind aber weitgehend äquivalent. Ein
[Tutorium](http://hadoop.apache.org/docs/r1.2.1/mapred_tutorial.html) findet
sich auf der Hadoop Webseite.

Zunächst werden nun Eingabedaten in das HDFS geschrieben:

```bash
hadoop fs -mkdir -p /user/$USER/wc/in
echo "Hello World Bye World" > file01
echo "Hello Hadoop Goodbye Hadoop" > file02
hadoop fs -put file01 /user/$USER/wc/in
hadoop fs -put file02 /user/$USER/wc/in
```

Dann kann der Job gestartet werden:

```bash
hadoop jar ~/git/bdelab/lab2/target/bdelab2-0.0.1-SNAPSHOT-jar-with-dependencies.jar de.hska.iwi.bdelab.batchjobs.WordCountOldAPI /user/$USER/wc/in /user/$USER/wc/out
```

Das Ergebnis findet sich im Anschluss im HDFS:

```bash
hadoop fs -cat /user/$USER/wc/out/part-00000
```

Zur Wiederholung des Experiments muss der Ordner `wc/out` wieder gelöscht
werden:

```bash
hadoop fs -rm -skipTrash /user/$USER/wc/out/*
hadoop fs -rmdir /user/$USER/wc/out
```

#### Aufgabe 2.2 (Pageviews)

MapReduce Jobs können ihre Daten aus vielen Quellen beziehen. Die Klasse
`CountFacts` zählt z.B. die Anzahl der Fakten eines Batch Stores auf Basis von
*Pails*.

Um zunächst eine Datenbasis zu erstellen, kann das Skript `batchloader.sh` in
lab2 in einer Shell gestartet werden.

```bash
$ ./batchloader.sh -h
Using Hadoop script found in path.
usage: batchloader [-f <arg>] [-g <arg>] [-h] [-m] [-r] [-s] [-v]
 -f,--file <arg>       use a non-default input file of base-records
 -g,--generate <arg>   generate number of records (rounded down to full
                       1000th, 1000 is default)
 -h,--help             show help
 -m,--master           move new data to 'master' pail (otherwise they
                       remain in 'new' pail)
 -r,--reset            reset all fact data files before importing
 -s,--show             dump generated records to stdout (slow)
 -v,--verbose          be verbose
```

Der `Batchloader` liest Pageviews aus einer Textdatei, generiert daraus einen
beliebig langen randomisierten Clickstream und schreibt diesen in einen 'new'
oder 'master' Pail.

Generieren sie ein paar Clicks (z.B. 10.000.000 - knapp 1GB) in den master Pail
wie folgt:

```bash
./batchloader.sh -g 10000000 -m -r
```

Dann kann der Job gestartet werden:

```bash
hadoop jar ~/git/bdelab/lab2/target/bdelab2-0.0.1-SNAPSHOT-jar-with-dependencies.jar de.hska.iwi.bdelab.batchjobs.CountFacts
```

Das Ergebnis findet sich im Anschluss im HDFS:

```bash
hadoop fs -cat /user/$USER/bdetmp/fact-count/part-00000
```

Schauen sie sich den Quelltext von `CountFacts` an. Dort sehen Sie die
Konfiguration des Jobs zur Verwendung des Pails. Beachten Sie auch den Mapper:
Hier werden die eingehenden Bytes mit dem generierten Thrift Serializer in `Data`
Objekte gewandelt und können in der `map` Funktion verarbeitet werden.

Öffnen Sie in einem Browser die [LKIT YARN Web
UI](http://iwi-lkit-ux-06.hs-karlsruhe.de:8088) und suchen Sie ihren job in der
Liste.

##### Aufgabe 2.1.1: implementieren sie die Vorberechnung eines Pageview Index als MapReduce Job

Nun sind sie an der Reihe. Implementieren sie den in der Vorlesung besprochenen **Pageview Index** wie folgt:

- Der Index soll für alle vorkommenden Stundenintervalle die Anzahl der Aufrufe
  einzelner Seiten zählen.
- Die Ausgabe soll als Textdatei der Form `<Datum/Stunde> <URL> <Anzahl der
  Aufrufe>` im HDFS erfolgen.
- Orientieren Sie sich dafür am `CountFacts` Job.
- Erhöhen Sie die Anzahl der Reducer.

##### Aufgabe 2.1.2: Untersuchen sie den Pageview Index Job

Beantworten sie nun bitte noch einige Fragen zu der letzten Aufgabe:

- Wie lange ist der Job gelaufen?
- Wieviele Mapper sind zum Einsagz gekommen und wie lange haben diese im Schnitt
  gearbeitet?
- Wieviele Daten haben Mapper und Reducer jeweils erzeugt?

Die gesuchten Informationen können sie in der YARN Web App finden. Die Ausgabe
auf der Konsole reicht dazu nicht. Suchen sie in der Web App die statistische
Auswertung ihres Jobs.