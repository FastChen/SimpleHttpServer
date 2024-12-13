echo "Start build to ./src/build"

javac -d ./out/build ./src/*.java

jar cfm ./SimpleHttpServer.jar ./src/META-INF/MANIFEST.MF -C ./out/build .

echo "Build file ./SimpleHttpServer.jar"

@PAUSE