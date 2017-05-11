Recolector de tweets para feelms:

- Toma tweets que tengan palabras definidas en el archivo word.dat.

- Los tweets que se obtienen desde twitter se guardan en una bd orientada a documentos.

Compilar con: gradle jar

Ejecutar con: java -cp build/libs/feelms_recolector-1.0.jar cl.feelms.twitter.streaming.TwitterStreaming
