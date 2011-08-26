all:
	javac -cp .:jar/* src/voxicity/*.java
	jar cvfe Voxicity.jar voxicity.Voxicity -C src/ .

test:
	java -cp .:jar/*:Voxicity.jar voxicity.Voxicity

clean:
	rm -rv src/voxicity/*.class
	rm -v Voxicity.jar
