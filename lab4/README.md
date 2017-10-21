# Aufgabe 4: Stream Processing / Storm TODO ANPASSEN NACH MIGRATION
Die Aufgabe veranschaulicht die Verarbeitung im Speed Layer. Wir betrachten dabei One-at-a-Time Streaming auf Basis von Storm. Als Datenquelle dient eine Multi-Consumer-Queue auf Basis von Kafka.

Mit dieser Konfiguration untersuchen wir zunächst das *Beispiel einer einfachen Word Count Implementierung*. Die Aufgabe besteht dann in der *Implementierung des Pageviews-pro-Zeit Index als Storm Topologie*.

## Lab4 Hintergrund
[Apache Storm](http://storm.apache.org/) realisiert eine Plattform für One-at-a-Time (und Micro-batched) Stream Verarbeitung in Cluster-Umgebungen. Verarbeitungsprozesse werden durch *Topologien* aus *Spouts* und *Bolts* spezifiziert, die vom zentralen *Nimbus* als *Tasks* auf verschiedene *Worker* Knoten eines Clusters verteilt werden (siehe [Apache Storm Concepts](http://storm.apache.org/releases/1.0.2/Concepts.html)).

Wir nutzen eine Möglichkeit von Storm aus, Topologien auch ohne verteilte Infrastruktur auf einem `LocalCluster` innerhalb eines Java Prozesses auszuführen (siehe [Apache Storm Local Mode](http://storm.apache.org/releases/1.0.2/Local-mode.html)).

Solche Streaming Prozesse können leicht als Java Anwendungen aus einer IDE wie Eclipse oder IntelliJ gestartet werden.

[Apache Kafka](http://kafka.apache.org/) stellt eine performante Infrastruktur für die Verwaltung, Speicherung und Verarbeitung von Datenströmen bereit. Kafka kann u.a. replizierte und partitionierte Message Queues bereitstellen.

Insbesondere können Kafka *Topics* als Multi-Consumer-Queues genutzt werden, bei denen *Records*, bestehend aus *Name* und *Value* für verschiedene Streaming Prozesse (ConsumerGroups) genutzt und bei Bedarf gemäß ihrer Position wiederholt werden können (siehe [Apache Kafka Intro](http://kafka.apache.org/intro)).

Die Semantik von Kafka Topics passt optimal zur Stream-Verarbeitung in Storm und wird dort zur garantierten Verarbeitung (at-least-once) von Nachrichten verwendet.

## Lab4 Vorbereitung
Als Infrastruktur benötigen wir nur **Kafka 0.9+** (für Storm nutzen wir einen eingebetteten "Cluster"). Die Umgebung ist auf der **VirtualBox Appliance** `VISLABv84` (oder neuer) vorbereitet. (Im Pool liegt die Virtual Appliance lokal vor (`/usr/local/share/vbox/`) und muss für Ihren Account installiert werden: öffnen sie dazu die Anwendung `virtualbox` und wählen sie im Dateimenü den Befehl zum importieren einer Appliance. Wählen sie die aktuellste Version der VM.)

Kafka kann aber auch sehr leicht selbst installiert werden (siehe [Kafka Quickstart](http://kafka.apache.org/quickstart)).

## Lab4 Durchführung
Melden Sie sich an der `VISLABv84+` VM an und starten Sie dort Eclipse. Aktualisieren Sie falls nötig das `~vislab/git/bdelab` Projekt (siehe [Lab2 Durchführung](#lab2durchführung)). Die Aufgabe basiert auf dem Maven Projekt `storm-word-count` im Ordner `~vislab/git/bdelab/lab4/stormwordcount`. Importieren Sie das Projekt zunächst wieder in Ihren Eclipse Workspace (falls nicht schon geschehen).

## Aufgabe 4.1
Probieren Sie das Wordcount Beispiel aus. Hierzu muss zunächst Kafka vorbereitet werden, danach kann die Storm Topology zur Ausführung kommen.

### Kafka Wordcount Topic und Producer aufsetzen
Das schnelle Aufsetzen von Kafka wird in [Kafka Quickstart](http://kafka.apache.org/quickstart) beschrieben. Folgen Sie dieser Anleitung und
- ...starten sie den **Zookeeper Server**
- ...starten Sie den **Kafka Server**
- ...richten Sie ein **Topic** `sentence` ein
- ...starten Sie einen **Producer** für `sentence`
- ...testen Sie das Topic mit einem **Consumer**

Auf `VISLABv84+` ist Kafka im Verzeichnis `~vislab/lib/kafka_2.11-0.10.1.0` installiert.

### Storm Topologie ausführen
Öffnen Sie das *storm-word-count Projekt* in Ihrer IDE. `WordcountTopology` ist im Package `de.hska.iwi.vsys.bdelab.streaming` spezifiziert. Die Topologie nutzt [storm-kafka-client](https://github.com/apache/storm/tree/v1.0.2/external/storm-kafka-client) zur Definition des `SentenceSpout` (als *Consumer* des Kafka Topics `sentence`).

Starten Sie `WordcountTopology` als Java-Anwendung in der IDE. Geben Sie nun einige Sätze in den Command Line Producer ein und beobachten Sie die Ausgaben der Topologie, um die Verarbeitung nachzuverfolgen. (Sie können auch in der der `WordcountTopology` die Debug-Ausgabe aktivieren, um u.a. das "Acking" der Tupel zu beobachten.)

Versuchen Sie, die Implementierung der Topologie mit Spout und Bolts nachzuvollziehen.

## Aufgabe 4.2
Die eigentliche Aufgabe besteht nun darin, die Berechnung des Pageview-pro-Zeit Index als Storm Topologie zu implementieren.

### Kafka vorbereiten
Legen Sie zunächst ein passendes Kafka Topic für Pageview Ereignisse an. Gehen Sie für die Kafka Record Values (d.h. die Nachrichteninhalte) vom Format der vorangegangenen Aufgaben aus:

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

# Referenzen

[1] Nathan Marz, James Warren, "Big Data: Principles and best practices of scalable realtime data systems", Manning, 2015
