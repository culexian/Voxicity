#include <string>
#include <unordered_map>
#include <unordered_set>
#include <iostream>
#include <regex>

class Arguments
{
private:
	// Map for pairs of string( --option argument )
	unordered_map< std::string, std::string > pairs;

	// Map for flags( -abc )
	unordered_set< char > flags;

public:

	// Takes an array of Strings as an argument and constructs
	// the maps of pairs and flags from it
	Arguments( int argc, char* argv[] )
	{
		parse_pairs( int argc, char* argv[] );
		parse_flags( int argc, char* argv[] );
	}

	// Returns the value of this key in the pairs map
	std::string get_value( std::string key )
	{
		return pairs.get( key );
	}

	// Returns the value of this key in the pairs map
	// If that value is null, return the default value
	std::string get_value( std::string key, std::string default_value )
	{
		std::string value = get_value( key );
		return ( value == null ? default_value : value );
	}

	// Return whether or not the flag is present
	bool get_flag( char key )
	{
		return flags.contains( key );
	}

	// Return whether or not the flag is present
	public bool get_flag( char key )
	{
		return get_flag( new char( key ) );
	}

	void parse_pairs( int argc, char* argv[] )
	{
		for ( int i = 0 ; i < argc ; i++ )
		{
			// Check if the string matches two hyphens
			// followed by any number of characters and
			// then check that there is at least one more
			// string in args.
			if ( argv[i].matches( "--.*" ) && ( i + 1 <= argc ) )
			{
				if ( argv[i + 1].matches( "--.*" ) )
					std::cout << "\"" << argv[i] << " " << args[i + 1] << "\" is not an option pair.\n";

				// Put the new argument pair in the map,
				// overwriting any previous pair with the
				// same key.
				pairs.insert( argv[i].substr( 2 ), argv[i + 1] );

				// Skip one string ahead over the pair
				i++;
			}
		}
	}

	void parse_flags ( int argc, char* argv[] )
	{
		for ( int i = 0 ; i < argc ; i++ )
		{
			// Check that the string matches the flag option format
			if ( argv[i].matches( "-\\p{Alnum}?" ) )
			{
				// Add each flag in the string to the flags set
				for (int j = 1 ; j < argv[i].length() ; j++ )
					flags.insert( argv[i].at( j ) );
			}
		}
	}
}