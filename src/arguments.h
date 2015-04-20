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

#ifndef ARGUMENTS_H
#define ARGUMENTS_H

#include <string>
#include <unordered_map>
#include <unordered_set>

class Arguments
{
    // Map for pairs of string( --option argument )
    std::unordered_map< std::string, std::string > pairs;

    // Map for flags( -abc )
    std::unordered_set< char > flags;

    void parse_pairs( int argc, char* argv[] );
    void parse_flags( int argc, char* argv[] );

    public:
    // Takes an array of Strings as an argument and constructs
    // the maps of pairs and flags from it
    Arguments( int argc, char* argv[] );

    // Returns the value of this key in the pairs map
    std::string get_value( std::string key ) const;

    // Returns the value of this key in the pairs map
    // If that value is null, return the default value
    std::string get_value( std::string key, std::string default_value ) const;

    // Return whether or not the flag is present
    bool get_flag( char key ) const;
};

#endif // ARGUMENTS_H
