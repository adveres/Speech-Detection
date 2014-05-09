README
This program was written in Eclipse and has an Eclipse project file structure.
I am using Java 1.6 on this machine, although 1.7 should work fine.

I programmed this on OSX, but tried it on Windows the night of submission. It worked okay, but I had to change how often I drain the DataLine that plays sound (I was doing it every 100ms, but Windows did not like that.) I ended up making it every 200ms, which seems to work on both platforms.

•   All source is under /src/speech_detection/*.java
•   Compiled *.class files go under /bin/speech_detection


Execute these commands inside the root project directory (where this README sits):

    Compile:    javac -d bin src/speech_detection/*.java

    Record:     java -cp ./bin speech_detection.Record

    Play:       java -cp ./bin speech_detection.Play [filename argument defaults to ‘speech.raw’]




Written by Adam Veres
For CS529 Multimedia Networking
Spring 2014, WPI
