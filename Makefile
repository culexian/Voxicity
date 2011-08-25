all:
	javac src/voxicity/*.java
	jar cvfe Voxicity.jar voxicity.Voxicity -C src/ .

test:
	java -jar Voxicity.jar

clean:
	rm -rv src/voxicity/*.class
	rm -v Voxicity.jar
