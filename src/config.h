/*
 * Copyright 2011, Erik Lund
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

#include <string>
#include <fstream>
#include <sstream>
#include <iostream>
#include <vector>
#include <unordered_map>
#include <cstdio>

class Config
{
public:
	Config( std::string filename ){
		
		std::ifstream file;
		std::string line, sarg, sparam;
		char arg[80], param[80];

		std::vector< std::string > vect;
		std::unordered_map< std::string, std::string > pairs;

		file.open( filename );

		if ( !file.is_open() ){
			std::cout << "Invalid config file specified!\n";
		}
		else
		{
			for ( int i = 0; !file.eof(); ++i )
			{
				std::getline( file, line );
				vect.push_back( line );
			}

			for ( auto it = vect.begin(); it < vect.end(); it++ ){
				if ( std::sscanf( it->c_str(), "%s = %[^\n]s", arg, param ) == 2 )
				{
					sarg = std::string( arg );
					sparam = std::string( param );

					std::cout << sarg << " " << sparam << std::endl;
				}
				else
				{
					std::cout << "Fuck you, that's wrong!\n";
				}
			}
		}

	}
};