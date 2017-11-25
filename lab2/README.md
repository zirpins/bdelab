# Aufgabe 2: MapReduce

In der Aufgabe wird ... 

1. ...der praktische Umgang mit *Map Reduce* anhand von Beispielen eingeübt.
2. ...die Vorberechnung konkreter Abfragen eines Big Data Systems mittels Neuberechnung von *Batch Views*  implementiert.

## Lab2 Vorbereitung

Als Plattform wird der verteilte Ressourcen Manager [**Hadoop YARN 2.7.3**](http://hadoop.apache.org/docs/current/hadoop-yarn/hadoop-yarn-site/YARN.html) für verteilte Anwendungen auf Basis von [**Hadoop Map Reduce**](http://hadoop.apache.org/docs/current/hadoop-mapreduce-client/hadoop-mapreduce-client-core/MapReduceTutorial.html) verwendet.

## Lab2 Durchführung

Melden Sie sich im LKIT Pool an und starten Sie dort Eclipse. Aktualisieren Sie das `bdelab` Git Repository.

```
cd ~/git/bdelab # bzw eigener Pfad
git pull
cd lab2
```

Die Aufgabe basiert auf dem Maven Projekt `bdelab2` im Ordner `bdelab/lab2`. Importieren Sie das Projekt zunächst wieder in Ihren Eclipse Workspace (falls nicht schon geschehen). 

### MapReduce

Die Vorberechnung von Views aus der Faktenbasis erfolgt durch parallele Batch Verarbeitung in einem Cluster. Als Plattform für das Management der Ressourcen im Cluster wird Hadoop YARN verwendet. Das Map Reduce Framework übernimmt die Verteilung von parallelen Jobs auf unterschiedliche Rechnerknoten. Im LKIT ist Hadoop (zZ in der Version 2.7.3) als Multi-Node Cluster installiert und läuft dort permanent. Jobs können mit dem `hadoop` (oder auch `yarn`) Skript eingestellt werden.

#### Aufgabe 2.1 (WordCount)

Zunächst soll ein einfaches Beispiel betrachtet werden. Im Paket `de.hska.iwi.bdelab.batchjobs` liegen die Klassen `WordCountOldAPI` und `WordCountNewAPI` mit MapReduce Jobs für das klassische Word Count Beispiel. Für MapReduce existieren zwei APIs: `org.apache.hadoop.mapred` und die neuere `org.apache.hadoop.mapreduce`. Die APIs unterscheiden sich leicht in der Verwendung, sind aber weitgehend äquivalent. Ein [Tutorium](http://hadoop.apache.org/docs/r1.2.1/mapred_tutorial.html) findet sich auf der Hadoop Webseite.

Vor der Verwendung muss das Projekt mit maven erstellt werden:

```
cd ~/git/bdelab/lab2 # bzw eigener Pfad
./genthrift.sh # Thrift Klassen für Aufgabe 2.2 generieren
mvn package
```

Zunächst sind dann noch Eingabedaten in das HDFS zu schreiben:

```
hadoop fs -mkdir -p /user/<IZ-Name>/wc/in
echo "Hello World Bye World" > file01
echo "Hello Hadoop Goodbye Hadoop" > file02
hadoop fs -put file01 /user/<IZ-Name>/wc/in
hadoop fs -put file02 /user/<IZ-Name>/wc/in
```

Nun kann der Job gestartet werden:

```
hadoop jar ~/git/bdelab/lab2/target/bdelab2-0.0.1-SNAPSHOT-jar-with-dependencies.jar de.hska.iwi.bdelab.batchjobs.WordCountOldAPI /user/<IZ-Name>/wc/in /user/<IZ-Name>/wc/out
```

Das Ergebnis findet sich im Anschluss im HDFS:

```
hadoop fs -cat /user/<IZ-Name>/wc/out/part-00000
```

Zur Wiederholung des Experiments muss der Ordner `wc/out` wieder gelöscht werden:

```
hadoop fs -rm -skipTrash /user/<IZ-Name>/wc/out/*
hadoop fs -rmdir /user/<IZ-Name>/wc/out
```

#### Aufgabe 2.2 (Pageviews)

MapReduce Jobs können ihre Daten aus vielen Quellen beziehen. Die Klasse `CountFacts` zählt z.B. die Anzahl der Fakten eines Batch Stores auf Basis von *Pails*.

Um zunächst eine Datenbasis zu erstellen, kann das Skript `batchloader.sh` in lab2 in einer Shell  gestartet werden. 

```
./batchloader.sh -h
usage: batchloader [-g <arg>] [-h] [-m] [-r] [-s] [-v]
 -g,--generate <arg>   generate number of records (rounded down to full 1000th, 1000 is default)
 -h,--help             show help
 -m,--master           move new data to 'master' pail (otherwise they remain in 'new' pail)
 -r,--reset            reset all batch data files before importing
 -s,--show             dump generated records to stdout (slow)
 -v,--verbose          be verbose
```

Der `Batchloader` liest Pageviews aus einer Textdatei, generiert daraus einen beliebig langen randomisierten Clickstream und schreibt diesen in einen 'new' oder 'master' Pail. 

Generieren sie ein paar Clicks (z.B. 10.000.000 (knapp 1GB), bitte zunächst nicht mehr, um den Cluster im Laborbetrieb zu schonen) in den master Pail wie folgt:

```
./batchloader.sh -g 10000000 -m
```

Dann kann der Job gestartet werden:

```
hadoop jar ~/git/bdelab/lab2/target/bdelab2-0.0.1-SNAPSHOT-jar-with-dependencies.jar de.hska.iwi.bdelab.batchjobs.CountFacts
```

Das Ergebnis findet sich im Anschluss im HDFS:

```
hadoop fs -cat /user/<IZ-Name>/bdetmp/fact-count/part-00000
```

Schauen sie sich den Quelltext von `CountFacts` an. Dort sehen Sie die Konfiguration des Jobs zur Verwendung des Pails. Beachten Sie auch den Mapper: Hier werden die eingehenden Bytes mit dem generierten Thrift Serializer in Data Objekte gewandelt und können in der map Funktion verarbeitet werden.

Öffnen Sie in einem Browser die [LKIT YARN Web UI](http://iwi-lkit-ux-06.hs-karlsruhe.de:8088) und suchen Sie ihren job in der Liste.

##### Aufgabe 2.1.1: implementieren sie die Vorberechnung eines Pageview Index als MapReduce Job

- Der Index soll für alle vorkommenden Stundenintervalle die Anzahl der Aufrufe einzelner Seiten zählen.
- Die Ausgabe soll als Textdatei der Form `<Datum/Stunde> <URL> <Anzahl der Aufrufe>` erfolgen.
- Orientieren Sie sich dafür am `CountFacts` Job.
- Erhöhen Sie die Anzahl der Reducer.

##### Aufgabe 2.1.2: Untersuchen sie den Pageview Index Job

- Wie lange ist der Job gelaufen?
- Wieviele Mapper sind zum Einsagz gekommen und wie lange haben diese im Schnitt gearbeitet?
- Wieviele Daten haben Mapper und Reducer jeweils erzeugt?
