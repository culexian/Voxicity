all:
	g++ -std=c++11 -g *.cpp -lSDL2 -lGL -o ../voxicity

mac:

	g++ -std=c++11 -stdlib=libc++ -I/Library/Frameworks/SDL2.framework/Headers -framework OpenGL -framework SDL2 -g *.cpp -o ../voxicity

clean:
	rm -v *.o
