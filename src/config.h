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

#ifndef CONFIG_H
#define CONFIG_H

#include <string>
#include <fstream>
#include <sstream>
#include <iostream>
#include <unordered_map>

#include "arguments.h"

class Config
{
	// The map of strings loaded in as options from the config file
	std::unordered_map< std::string, std::string > options;

	// Create a new string start from the first non-whitespace and including the last non-whitespace
	std::string trim_string( const std::string& in ) const
	{
		// Find the position of the first non-whitespace character in the string
		auto start = in.cbegin() + in.find_first_not_of( " \t" );

		// Find the position of the last non-whitespace character in the string + 1 for 0-counting
		auto end = in.cbegin() + in.find_last_not_of( " \t" ) + 1;

		// Create a string with the new start and end iterators and return it
		return std::string( start, end );
	}

public:
	Config( Arguments args ){
		
		std::ifstream file;
		std::string line;

		std::string filename = args.get_value( "config", "voxicity.cfg" );

		/* This regex function matches anything that contains zero or more whitespaces ( [[:s:]]*, where *
		 * means zero or more, and [[:s:]] means any whitespace in ECMAScript ), one or more letters or numbers 
		 * ( where [[:alnum:]] means any alphabetical character or number, lowercase or uppercase, and the plus sign
		 * (+) means that there is one or more of that element ), zero or more whitespaces ( as explained 
		 * above ), an equals-sign, zero or more whitespaces, one or more letters or whitespaces ( here 
		 * the paranthesises notify that [[:alnum:]] and [[:s:]] are in one group, and they are separated
		 * by a |-sign, which basically means "or", and the plus at the end means one or more ), and zero
		 * or more whitespaces at the end. An example of a line like this would be:
		 * 
		 * "	argument = i am an argument 	"
		 */
		std::regex argument_regex( "[[:s:]]*([[:alnum:]]|[[:punct:]])+[[:s:]]*=[[:s:]]*([[:alnum:]]|[[:s:]]|[[:punct:]])+[[:s:]]*" );

		file.open( filename );

		// Checks if the specified file exists
		if ( !file.is_open() ){
			std::cout << "Config file not found!\n";
		}
		else
		{
			while ( !file.eof() )
			{
				std::getline( file, line );

				if ( std::regex_match( line, argument_regex ) )
				{
					std::string arg, param;

					std::stringstream entire_line( line );

					// Read line until delimiter '=' is found
					std::getline( entire_line, arg, '=' );

					// Read the rest of the line until line break (duh)
					std::getline( entire_line, param );
					
					// Trim strings (removing all whitespaces so the lines can be read by the program)
					arg = trim_string( arg );
					param = trim_string( param );

					// Finds parameters from arguments and overwrites the ones found in the config
					param = args.get_value( arg, param );

					options.emplace( arg, param );

					std::cout << "Loaded option : " << arg << " = " << param << std::endl;
				}
				else if ( line.empty() ){
					continue;
				}
				else
				{
					std::cout << "Invalid line in config at: \"" << line << std::endl;
				}
			}
		}
	}

	std::string get_value( std::string key )
	{
		auto it = options.find( key );
		return ( it == options.end() ? "" : it->second );
	}

	std::string get_value( std::string key, std::string default_value )
	{
		std::string value = get_value( key );
		return ( value.empty() ? default_value : value );
	}
};

#endif // CONFIG_H
