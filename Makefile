all:
	javac -cp .:jar/* src/voxicity/*.java
	jar cvfme Voxicity.jar Manifest.mf voxicity.Voxicity -C src/ . -C gui/ .

test:
	java -XX:+UseConcMarkSweepGC -Xmx4G -Djava.library.path=native/linux -jar Voxicity.jar voxicity.Voxicity --mode client

server_test:
	java -XX:+UseConcMarkSweepGC -Xmx4G -Djava.library.path=native/linux -jar Voxicity.jar voxicity.Voxicity --mode server &

pack:
	

clean:
	rm -rv src/voxicity/*.class
	rm -v Voxicity.jar
