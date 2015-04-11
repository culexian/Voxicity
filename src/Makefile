all:
	g++ -std=c++11 -g *.cpp -l SDL2 -o ../voxicity

mac:

	g++ -std=c++11 -stdlib=libc++ -I/Library/Frameworks/SDL2.framework/Headers -framework SDL2 -g main.cpp client.cpp -o ../voxicity

clean:
	rm -v *.o
