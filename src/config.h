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
#include <unordered_map>

#include "arguments.h"

class Config
{
    // The map of strings loaded in as options from the config file
    std::unordered_map< std::string, std::string > options;

    // Create a new string start from the first non-whitespace and including the last non-whitespace
    std::string trim_string( const std::string& in ) const;

    public:
    Config( const Arguments& args );

    std::string get_value( const std::string& key ) const;
    std::string get_value( const std::string& key, const std::string& default_value ) const;

    int get_int( const std::string& key ) const;
    int get_int( const std::string& key, int default_value ) const;

    float get_float( const std::string& key ) const;
    float get_float( const std::string& key, float default_value ) const;
};

#endif // CONFIG_H
