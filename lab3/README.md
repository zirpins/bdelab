# Aufgabe 3: Batch Workflow

Die Aufgabe vertieft die Verarbeitungsschritte im Batch Layer. Es wird **Cascalog** als Framework für die deklarative Erstellung komplexer MapReduce Verarbeitungsprozesse eingeführt. Auf Basis von Cascalog wird ein einfacher Batch Workflow mit Daten-Import, -Normalisierung und -Vorverarbeitung realisiert. 

## Lab3 Vorbereitung

Als Plattform wird weiterhin **Hadoop 2.7.3** verwendet. Cascalog basiert auf verschiedenen Frameworks, die auf der Hadoop Plattform aufsetzen. Die komplette Umgebung ist im LKIT schon fertig installiert.

## Lab3 Durchführung

Melden Sie sich im LKIT Pool an und starten Sie dort Eclipse. Aktualisieren Sie das `bdelab` Git Repository.

```
cd ~/git/bdelab # bzw eigener Pfad
git pull
cd lab3
```

Die Aufgabe basiert auf dem Maven Projekt `bdelab3` im Ordner `bdelab/lab3`. Importieren Sie das Projekt zunächst wieder in Ihren Eclipse Workspace.

Auch in diesem Projekt muss wieder eine Konfigurationsdatei angelegt werden, um den IZ-Accountnamen für HDFS zu spezifizieren. Dazu ist ein Template vorgegeben, das sie wie folgt kopieren und anpassen können:

```
cd ~/git/bdelab/lab3/src/main/resources
cp template.hadoop.properties hadoop.properties
vim hadoop.properties # file editieren
```

Ändern Sie die Property `hadoop.user.name` auf ihren IT-Accountnamen.

Das Projekt basiert auf dem vorherigen Projekt `bdelab2`, daher muss dieses mit Maven installiert werden. Dies geschieht wie folgt:

```
cd ~/git/bdelab/lab2
mvn install
```


### Cascalog

Cascalog ist ein Framework zur deklarativen Beschreibung komplexer Abfragen, die als MapReduce Programme auf einem Hadoop YARN Cluster ausgeführt werden können. Cascalog ist in *Clojure* geschrieben, wir verwenden die zugehörige Java API **JCascalog** zur Programmierung von Cascalog Anfragen in Java.

Eine praktische Einführung in die wichtigsten und für die Aufgabe notwendigen Grundlagen liegt als kurzer Foliensatz vor. Weitere [Cascalog Dokumentation](https://github.com/nathanmarz/cascalog/wiki/JCascalog) findet sich im Web.

#### Aufgabe 3.1 Cascalog CountFacts

Als erste Schritte mit JCascalog sollen Sie mit einigen Beispielen experimentieren. Im Projekt bdelab3 ist die schon bei MapReduce gesehene **CountFacts** Abfrage in JCascalog implementiert. Eine lokale Version der Abfrage finden Sie in der Klasse `de.hska.iwi.bdelab.batchqueries.CountFacts2` (die Variante `CountFacts3` läuft als verteilter Map Reduce Job im Cluster und schreibt das Ergebnis auf HDFS). Viele weitere Beispiele für Cascalog Abfragen finden Sie im Paket `de.hska.iwi.bdelab.batchqueries.examples`.

Um neue Fakten zu generieren, wird der schon bekannte Batchloader verwendet. Mit der Option `-r` setzt das Programm die Faktenbasis zurück. Ansonsten werden 1000 Fakten in einen Pail für neue Daten eingefügt. Mit der Option `-m` werden die Daten auch in den Master Pail übernommen (sonst nur in den 'New Pail' für neue Fakten). Führen Sie an dieser Stelle den Batchloader mit beiden Optionen aus:

```
~/git/bdelab/lab2/batchloader.sh -r -m -g 1000000
```

Nun starten Sie `CountFacts2` wie folgt:

```
cd ~/git/bdelab/lab3 # wechseln in Projektordner
mvn package # Projekt bauen
hadoop jar target/bdelab3-0.0.1-SNAPSHOT-jar-with-dependencies.jar de.hska.iwi.bdelab.batchqueries.CountFacts2
```

Prüfen Sie das Ergebnis. Generieren Sie mehr Daten (Option `-g <records>` z.B. für 10000000 Records).

#### Aufgabe 3.2 URL Normalisierung

Die Klasse `de.hska.iwi.bdelab.batchqueries.SimpleBatchWorkflow` implementiert eine Verarbeitungskette im Batch Layer mit Aufnahme neuer Daten, Normalisierung von URLs und Vorberechnung von Pageviews für mehrere Intervall-Granularitäten.

Führen Sie vor dem Start den Batchloader aus:

```
~/git/bdelab/lab2/batchloader.sh -r -g 1000000 -f ~/git/bdelab/lab3/pageviews2.txt
```

Dadurch werden alle Dateien zunächst gelöscht und dann 1000000 Einträge in den Pail für neue Daten eingetragen, noch nicht jedoch in den Master Pail (dies tut der `SimpleBatchWorkflow` selbst - warum?). Durch die Option `-f <filename>` wird eine alternative Basis zur Generierung der Daten verwendet (siehe unten). Sie starten den `SimpleBatchWorkflow` mit:

```
hadoop jar ~/git/bdelab/lab3/target/bdelab3-0.0.1-SNAPSHOT-jar-with-dependencies.jar de.hska.iwi.bdelab.batchqueries.SimpleBatchWorkflow
```

**Aufgabe:** Die Cascalog Query für die URL-Normalisierung ist noch nicht implementiert (sie ist zwar vorhanden, tut aber noch nichts sinnvolles). In der alternativen Datenbasis enthalten die Fakten unterschiedliche URL-Varianten (mit Query Parametern) für die gleichen Ressourcen. Implementieren Sie die URL-Normalisierung aus der Vorlesung, so dass ein korrekter Pageview Index berechnet wird.
