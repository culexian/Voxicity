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
		std::string line, arg, param;

		std::vector< std::string > vect;
		std::unordered_map< std::string, std::string > pairs;

			/* This regex function matches anything that contains zero or more whitespaces ( [[:s:]]*, where *
		 * means zero or more, and [[:s:]] means any whitespace in ECMAScript ), one or more letters 
		 * ( where [[:alpha:]] means any alphabetical character, lowercase or uppercase, and the plus sign
		 * (+) means that there is one or more of that element ), zero or more whitespaces ( as explained 
		 * above ), an equals-sign, zero or more whitespaces, one or more letters or whitespaces ( here 
		 * the paranthesises notify that [[:alnum:]] and [[:s:]] are in one group, and they are separated
		 * by a |-sign, which basically means "or", and the plus at the end means one or more ), and zero
		 * or more whitespaces at the end. An example of a line like this would be:
		 * 
		 * "	argument = i am an argument 	"
		 */
		std::regex argument_regex( "[[:s:]]*([[:alpha:]]|[[:punct:]])+[[:s:]]*=[[:s:]]*([[:alnum:]]|[[:s:]]|[[:punct:]])+[[:s:]]*" );

		file.open( filename );

		// Checks if the specified file exists
		if ( !file.is_open() ){
			std::cout << "Invalid config file specified!\n";
		}
		else
		{
			for ( int i = 0; !file.eof(); ++i )
			{
				std::getline( file, line );

				// Only try to parse lines with content
				if ( !line.empty() )
					vect.push_back( line );
			}

			for ( auto it = vect.begin(); it < vect.end(); it++ ){
				if ( std::regex_match( *it, argument_regex ) )
				{
					std::stringstream entire_line( *it );
					std::getline( entire_line, arg, '=' );
					std::getline( entire_line, param, '\n' );
				}
				else
				{
					std::cout << "Invalid line in config at: \"" << *it << std::endl;
				}
			}
		}

	}
};
