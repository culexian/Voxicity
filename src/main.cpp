/*
 * Copyright 2014, Erik Lund
 *
 * This file is part of Voxicity.
 *
 *  Voxicity is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Voxicity is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Voxicity.  If not, see <http://www.gnu.org/licenses/>.
 */


#include "arguments.h"
#include "config.h"
#include "client.h"
#include "noise.h" // This is temporary, to see if the file compiles

void init( Arguments args )
{
	Config config( args );

	std::string mode = args.get_value( "mode", "client" );

	if ( mode == "server" )
	{
		// Start the server, it spawns its own thread
		// and takes over from here
		std::cout << "It works!\n";
	}
	else if ( mode == "client" )
	{
		Client client( config );
	}
	else
	{
		std::cout << "Invalid mode: " << mode << std::endl;
	}
}

int main( int argc, char* argv[] )
{
	// Parse the command line arguments and create the argument object
	Arguments cmd_args( argc, argv );

	/*
	// If the command line arguments are invalid, print the usage info and exit
	if ( !cmd_args.ok() )
	{
		print_usage();
		return 1;
	}
	*/
	// File new_out = new File( "voxicity.log" );
	// System.setOut( new PrintStream( new_out ) );

	// Initialize based on the command line arguments
	init( cmd_args );

	return 0;
}
