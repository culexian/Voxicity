#include "config.h"

#include <fstream>
#include <sstream>
#include <iostream>
#include <regex>

// Create a new string start from the first non-whitespace and including the last non-whitespace
std::string Config::trim_string( const std::string& in ) const
{
    // Find the position of the first non-whitespace character in the string
    auto start = in.cbegin() + in.find_first_not_of( " \t" );

    // Find the position of the last non-whitespace character in the string + 1 for 0-counting
    auto end = in.cbegin() + in.find_last_not_of( " \t" ) + 1;

    // Create a string with the new start and end iterators and return it
    return std::string( start, end );
}

Config::Config( const Arguments& args )
{    
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

    std::printf( "Config loaded with %d options\n", options.size() );
}

std::string Config::get_value( const std::string& key ) const
{
    auto it = options.find( key );
    return ( it == options.end() ? "" : it->second );
}

std::string Config::get_value( const std::string& key, const std::string& default_value ) const
{
    std::string value = get_value( key );
    return ( value.empty() ? default_value : value );
}

int Config::get_int( const std::string& key) const
{
    return std::stoi( get_value( key ) );
}

int Config::get_int( const std::string& key, int default_value ) const
{
    std::string value = get_value( key );
    std::cout << value << "DEBUG" << std::endl;
    return ( value.empty() ? default_value : std::stoi( value ) );
}

float Config::get_float( const std::string& key ) const
{
    return std::stof( get_value( key ) );
}

float Config::get_float( const std::string& key, float default_value ) const
{
    std::string value = get_value( key );
    return ( value.empty() ? default_value : std::stof( value ) );
}
