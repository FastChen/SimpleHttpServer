rmdir /S /Q build

javac -d build src/SimpleHttpServer.java

jar cfm SimpleHttpServer.jar MANIFEST.MF -C build .

@PAUSE