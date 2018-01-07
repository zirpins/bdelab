# Aufgabe 4: Stream Processing / Apache Storm
Die Aufgabe veranschaulicht die Verarbeitung im Speed Layer. Wir betrachten dabei *One-at-a-Time Streaming* auf Basis von [Apache Storm](http://storm.apache.org/). Als Datenquelle dient eine *Multi-Consumer-Queue* auf Basis von [Apache Kafka](http://kafka.apache.org/).

Mit dieser Konfiguration untersuchen wir zunächst das *Beispiel einer einfachen Word Count Implementierung*. Die Aufgabe besteht dann in der *Implementierung des Pageviews-pro-Zeit Index als Storm Topologie*.

## Lab4 Hintergrund
[Apache Storm](http://storm.apache.org/) realisiert eine Plattform für One-at-a-Time (und Micro-batched) Stream Verarbeitung in Cluster-Umgebungen. Verarbeitungsprozesse werden durch *Topologien* aus *Spouts* und *Bolts* spezifiziert, die vom zentralen *Nimbus* als *Tasks* auf verschiedene *Worker* Knoten eines Clusters verteilt werden (siehe [Apache Storm Concepts](http://storm.apache.org/releases/current/Concepts.html)).

Wir nutzen eine Möglichkeit von Storm aus, Topologien auch ohne verteilte Infrastruktur auf einem `LocalCluster` innerhalb eines Java Prozesses auszuführen (siehe [Apache Storm Local Mode](http://storm.apache.org/releases/current/Local-mode.html)).

Solche Streaming Prozesse können leicht als Java Anwendungen aus einer IDE wie Eclipse oder IntelliJ gestartet werden.

[Apache Kafka](http://kafka.apache.org/) stellt eine performante Infrastruktur für die Verwaltung, Speicherung und Verarbeitung von Datenströmen bereit. Kafka kann u.a. replizierte und partitionierte Message Queues bereitstellen.

Insbesondere können Kafka *Topics* als Multi-Consumer-Queues genutzt werden, bei denen *Records*, bestehend aus *Name* und *Value* für verschiedene Streaming Prozesse (ConsumerGroups) genutzt und bei Bedarf gemäß ihrer Position wiederholt werden können (siehe [Apache Kafka Intro](http://kafka.apache.org/intro)).

Die Semantik von Kafka Topics passt optimal zur Stream-Verarbeitung in Storm und wird dort zur garantierten Verarbeitung (at-least-once) von Nachrichten verwendet.

## Lab4 Vorbereitung

### Infrastruktur
Als Infrastruktur benötigen wir nur eine **Kafka 0.9+** Plattform, die im LKIT bereitgestellt wird. Für Storm nutzen wir einen eingebetteten "Cluster"). 

Auf `iwi-lkit-ux-06` läuft ein Kafka Server, der die Verwaltung von Queues übernimmt. 

Auf jedem Poolrechner gibt es zudem Kafka Tools in Form von Skripten zur Interaktion mit dem Kafka Server.

### Topic, Producer und Consumer Skripte
Kafka bietet die Möglichkeit per Konsole Topics anzulegen sowie Producer und Consumer zu starten. Dies erfolgt mittels Skripten, welche sich innerhalb des Kafka Verzeichnisses in folgendem Ordner befinden:

```
/usr/local/opt/kafka_2.11-1.0.0/bin
```

Die relevanten Skripte sind:  
- `kafka-console-consumer.sh`
- `kafka-console-producer.sh`  
- `kafka-topics.sh`

Sie werden die Verwendung dieser Skripte in Aufgabe 4.1 kennenlernen.

### Die Verwandlung
Das Kafka Topic namens "metamorph" wurde für Sie bereits vorbereitet. Sie teilen sich dieses Topic mit Ihren Kommilitonen und können es zum Beispiel für das Wordcount Beispiel verwenden.

Es enthält die englische Ausgabe von Franz Kafkas Erzählung Die Verwandlung ([Durch Projekt Gutenberg bereitgestellt](http://www.gutenberg.org/ebooks/5200)).

**Falls Sie aus Versehen Änderungen am Topic durchgeführt haben, teilen Sie dies bitte einem Betreuer mit.**

## Lab4 Durchführung
Melden Sie sich im LKIT Pool an und starten Sie dort Eclipse. Aktualisieren Sie das `bdelab` Git Repository.

```
cd ~/git/bdelab # bzw eigener Pfad
git pull
cd lab4
```

Die Aufgabe basiert auf dem Maven Projekt `storm-word-count` im Ordner `bdelab/lab4/stormwordcount`. Importieren Sie das Projekt zunächst wieder in Ihren Eclipse Workspace.

## Aufgabe 4.1
Probieren Sie das Wordcount Beispiel aus. Hierzu muss zunächst Kafka vorbereitet werden, danach kann die Storm Topology zur Ausführung kommen.

### Kafka Wordcount Topic und Producer aufsetzen
Das schnelle Aufsetzen von Kafka wird in [Kafka Quickstart](http://kafka.apache.org/quickstart) beschrieben. Folgen Sie dieser Anleitung und
- ...richten Sie ein **Topic** `sentence_<IZ-ID>` ein
- ...starten Sie einen **Producer** für `sentence_<IZ-ID>`
- ...testen Sie das Topic mit einem **Consumer**

### Storm Topologie ausführen
Öffnen Sie das *storm-word-count Projekt* in Ihrer IDE. `WordcountTopology` ist im Package `de.hska.iwi.vsys.bdelab.streaming` spezifiziert. Die Topologie nutzt [storm-kafka-client](https://github.com/apache/storm/tree/v1.0.2/external/storm-kafka-client) zur Definition des `SentenceSpout` (als *Consumer* des Kafka Topics `sentence_<IZ-ID>`). **Achtung:** Ersetzen Sie `sentence_<IZ-ID>` in `SentenceSpout.java` durch ihre Kennung.

Bauen Sie dann das Projekt und starten Sie `WordcountTopology` als Java-Anwendung in der IDE. Geben Sie nun einige Sätze in den Command Line Producer ein und beobachten Sie die Ausgaben der Topologie, um die Verarbeitung nachzuverfolgen. (Sie können auch in der der `WordcountTopology` die Debug-Ausgabe aktivieren, um u.a. das "Acking" der Tupel zu beobachten.)

Versuchen Sie, die Implementierung der Topologie mit Spout und Bolts nachzuvollziehen.

## Aufgabe 4.2
Die eigentliche Aufgabe besteht nun darin, die Berechnung des Pageview-pro-Zeit Index als Storm Topologie zu implementieren.

### Kafka vorbereiten
Legen Sie zunächst ein passendes Kafka Topic für Pageview Ereignisse an (wählen Sie einen individuellen Namen, um Namenskonflikte mit anderen Teilnehmern zu vermeiden). Gehen Sie für die Kafka Record Values (d.h. die Nachrichteninhalte) vom Format der vorangegangenen Aufgaben aus:

```
<IP> <URL> <EPOCH-TIME>
```

(siehe Datei `pageviews2.txt` aus bdelab3).

### Storm Topologie
Für die Pageview Topologie können Sie praktischerweise das storm-word-count Projekt kopieren und anpassen.

- Passen Sie die Konfiguration des Kafka Spout an.
- Realisieren Sie Datenextraktion, URL-Normalisierung, Aufteilung in Stunden-Buckets und Zählerfunktion als Bolts.
- Kombinieren Sie alle Komponenten mit passenden Stream Groupings zu einer Topologie.

### Ausführung
Starten Sie die Topologie in der IDE. Zur Erzeugung eines Streams kopieren Sie den Inhalt der Pageview Datei aus Aufgabe 3 per Copy/Paste in den Kafka Command Line Producer.