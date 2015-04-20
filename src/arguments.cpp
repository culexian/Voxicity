#include "arguments.h"

#include <iostream>
#include <regex>

void Arguments::parse_pairs( int argc, char* argv[] )
{
    std::regex option_regex( "--[[:alnum:]]+" );
    std::regex param_regex( "[[:alnum:]]+" );

    for ( int i = 1 ; i < argc ; i++ )
    {
        // Check if the string matches two hyphens
        // followed by any number of characters and
        // then check that there is at least one more
        // string in args.
        if ( std::regex_match( argv[i], option_regex ) && ( i + 1 < argc ) )
        {
            // Make sure the parameter matches an alphanumeric string, otherwise continue
            if ( !std::regex_match( argv[i + 1], param_regex ) )
            {
                std::cout << "\"" << argv[i] << " " << argv[i + 1] << "\" is not an option pair.\n";
                i++;
                continue;
            }

            // Put the new argument pair in the map,
            // overwriting any previous pair with the
            // same key.
            pairs.emplace( std::string( argv[i] ).substr( 2, std::string( argv[i] ).length() - 2 ), std::string( argv[i + 1] ) );

            // Skip one string ahead over the pair
            i++;
        }
        else if ( std::regex_match( argv[i], option_regex ) && ( i + 1 == argc ) )
        {
            std::cout << "Option " << argv[i] << " needs a parameter\n";
        }
        else if ( !std::regex_match( argv[i], option_regex ) && !std::regex_match( argv[i], std::regex( "-[[:alpha:]]+" ) ) )
        {
            std::cout << argv[i] << " is not an option or a flag\n";
        }
    }
}

void Arguments::parse_flags( int argc, char* argv[] )
{
    for ( int i = 1 ; i < argc ; i++ )
    {
        // Check that the string matches the flag option format
        if ( std::regex_match( argv[i], std::regex( "-[[:alpha:]]+" ) ) )
        {
            std::cout << argv[i] << " matched as a flag!\n";

            std::string flag_string( argv[i] );
            // Add each flag in the string to the flags set
            for ( int j = 1 ; j < flag_string.size() ; j++ )
                flags.insert( flag_string[j] );
        }
    }
}

// Takes an array of Strings as an argument and constructs
// the maps of pairs and flags from it
Arguments::Arguments( int argc, char* argv[] )
{
    parse_pairs( argc, argv );
    parse_flags( argc, argv );
}

// Returns the value of this key in the pairs map
std::string Arguments::get_value( std::string key ) const
{
    auto iter = pairs.find( key );
    return ( iter == pairs.end() ? "" : iter->second );
}

// Returns the value of this key in the pairs map
// If that value is null, return the default value
std::string Arguments::get_value( std::string key, std::string default_value ) const
{
    std::string value = get_value( key );
    return ( value.empty() ? default_value : value );
}

// Return whether or not the flag is present
bool Arguments::get_flag( char key ) const
{
    return flags.count( key );
}
