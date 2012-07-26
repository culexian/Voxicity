all:
	javac -cp .:jar/* src/voxicity/*.java
	jar cvfme Voxicity.jar Manifest.mf voxicity.Voxicity -C src/ .

test:
	java -XX:+UseConcMarkSweepGC -Xmx4G -Djava.library.path=native/linux -jar Voxicity.jar voxicity.Voxicity --mode server

pack:
	

clean:
	rm -rv src/voxicity/*.class
	rm -rv src/voxicity/scene/*.class
	rm -v Voxicity.jar
