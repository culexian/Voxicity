all:
	cd src; make

test:
	./voxicity

cpp:
	g++ -std=c++11 -g src/main.cpp -o voxicity

cppmac:
	g++ -std=c++11 -stdlib=libc++ src/main.cpp
	
clean:
	rm -v voxicity
	cd src; make clean
