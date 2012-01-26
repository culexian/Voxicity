all:
	javac -cp .:jar/* src/voxicity/*.java src/voxicity/*/*.java
	jar cvfme Voxicity.jar Manifest.mf voxicity.Voxicity -C src/ .

test:
	java -XX:+UseConcMarkSweepGC -Xmx4G -Djava.library.path=native/linux -jar Voxicity.jar voxicity.Voxicity

pack:
	

clean:
	rm -rv src/voxicity/*.class
	rm -v Voxicity.jar
