all:
	g++ -std=c++11 -g src/main.cpp -o voxicity
	javac -cp .:jar/* src/voxicity/*.java
	jar cvfme Voxicity.jar Manifest.mf voxicity.Voxicity -C src/ . -C gui/ .

test:
	java -XX:+UseConcMarkSweepGC -Xmx4G -Djava.library.path=native/linux -jar Voxicity.jar voxicity.Voxicity --mode client

server_test:
	java -XX:+UseConcMarkSweepGC -Xmx4G -Djava.library.path=native/linux -jar Voxicity.jar voxicity.Voxicity --mode server &

cpp:
	g++ -std=c++11 -g src/main.cpp -o voxicity

cppmac:
	g++ -std=c++11 -stdlib=libc++ src/main.cpp
	
pack:
	

clean:
	rm -rv src/voxicity/*.class
	rm -v Voxicity.jar
