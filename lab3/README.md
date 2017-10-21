# Aufgabe 3: Batch Workflow / Cascalog TODO ANPASSEN NACH MIGRATION
Die Aufgabe vertieft die Verarbeitungsschritte im Batch Layer. Es wird **Cascalog** als Framework für die deklarative Erstellung komplexer MapReduce Verarbeitungsprozesse eingeführt. Auf Basis von Cascalog wird ein einfacher Batch Workflow mit Daten-Import, -Normalisierung und -Vorverarbeitung realisiert.

## Lab3 Vorbereitung
Als Plattform wird weiterhin **Hadoop 2.7.0 YARN** verwendet. Die Umgebung ist auf der **VirtualBox Appliance** `VISLABv83` (oder neuer) vorbereitet. (Im Pool liegt die Virtual Appliance lokal vor (`/usr/local/share/vbox/`) und muss für Ihren Account installiert werden: öffnen sie dazu die Anwendung `virtualbox` und wählen sie im Dateimenü den Befehl zum importieren einer Appliance. Wählen sie die aktuellste Version der VM.)

## Lab3 Durchführung
Melden Sie sich an der `VISLABv83+` VM an und starten Sie dort Eclipse. Aktualisieren Sie falls nötig das `bdelab` Projekt (siehe [Lab2 Durchführung](#lab2durchführung)). Die Aufgabe basiert auf dem Maven Projekt `bdelab3` im Ordner `bdelab/lab3`. Importieren Sie das Projekt zunächst wieder in Ihren Eclipse Workspace (falls nicht schon geschehen).

### Cascalog
Cascalog ist ein Framework zur deklarativen Beschreibung komplexer Abfragen, die als MapReduce Programme auf einem Hadoop YARN Cluster ausgeführt werden können. Cascalog ist in *Clojure* geschrieben, wir verwenden die zugehörige Java API **JCascalog** zur Programmierung von Cascalog Anfragen in Java.

Eine praktische Einführung in die wichtigsten und für die Aufgabe notwendigen Grundlagen liegt als kurzer Foliensatz vor. Weitere [Cascalog Dokumentation](https://github.com/nathanmarz/cascalog/wiki/JCascalog) findet sich im Web.

Zur Ausführung der Cascalog Programme muss wieder Hadoop aktiviert werden. Öffnen Sie eine Konsole als User `hadoop` und starten Sie sowohl HDFS als auch YARN (siehe [Lab2 MapReduce](#lab2mapreduce)).

#### Aufgabe 3.1 Cascalog CountFacts
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

#### Aufgabe 3.2 URL Normalisierung
Die Klasse `de.hska.iwi.bdelab.batchqueries.SimpleBatchWorkflow` implementiert eine Verarbeitungskette im Batch Layer mit Aufnahme neuer Daten, Normalisierung von URLs und Vorberechnung von Pageviews für mehrere Intervall-Granularitäten.

Führen Sie vor dem Start den `Batchloader -r` aus. Dadurch werden alle Dateien zunächst gelöscht und dann 1000 Einträge in den Pail für neue Daten eingetragen, noch nicht jedoch in den Master Pail (dies tut der `SimpleBatchWorkflow` selbst). Sie starten den `SimpleBatchWorkflow` mit:
```
hadoop jar /home/vislab/git/bdelab/lab3/target/bdelab3-0.0.1-SNAPSHOT-jar-with-dependencies.jar de.hska.iwi.bdelab.batchqueries.SimpleBatchWorkflow
```

**Aufgabe:** Die Cascalog Query für die URL-Normalisierung ist noch nicht implementiert (sie ist zwar vorhanden, tut aber noch nichts sinnvolles). In diesem Projekt enthalten die Fakten unterschiedliche URL-Varianten für die gleichen Ressourcen. Implementieren Sie die URL-Normalisierung aus der Vorlesung, so dass ein korrekter Pageview Index berechnet wird.