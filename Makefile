all:
	cd src; make

test:
	./voxicity

mac:
	cd src; make mac
	
clean:
	rm -v voxicity
	cd src; make clean
