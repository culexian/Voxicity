all:
	javac -cp .:jar/* src/voxicity/*.java
	jar cvfme Voxicity.jar Manifest.mf voxicity.Voxicity -C src/ .

test:
	java -XX:+UseConcMarkSweepGC -Xmx4G -Djava.library.path=native/linux -jar Voxicity.jar voxicity.Voxicity --mode server &
	java -XX:+UseConcMarkSweepGC -Xmx4G -Djava.library.path=native/linux -jar Voxicity.jar voxicity.Voxicity --mode client

pack:
	

clean:
	rm -rv src/voxicity/*.class
	rm -v Voxicity.jar
