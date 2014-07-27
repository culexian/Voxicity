#include <string>
#include <map>
#include <unordered_map>
#include <set>
#include <unordered_set>
#include <iostream>
#include <regex>

class Arguments
{
private:
	// Map for pairs of string( --option argument )
	map< string, string > pairs = new unordered_map< string, string >();

	// Map for flags( -abc )
	set< char > flags  = new unordered_set< char >();

public:

	// Takes an array of Strings as an argument and constructs
	// the maps of pairs and flags from it
	Arguments( string[] args )
	{
		parse_pairs( args );
		parse_flags( args );
	}

	// Returns the value of this key in the pairs map
	string get_value( string key )
	{
		return pairs.get( key );
	}

	// Returns the value of this key in the pairs map
	// If that value is null, return the default value
	string get_value( string key, string default_value )
	{
		string value = get_value( key );
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

	void parse_pairs( string[] args )
	{
		for ( int i = 0 ; i < args.length ; i++ )
		{
			// Check if the string matches two hyphens
			// followed by any number of characters and
			// then check that there is at least one more
			// string in args.
			if ( args[i].matches( "--.*" ) && ( i + 1 <= args.length ) )
			{
				if ( args[i + 1].matches( "--.*" ) )
					std::cout << "\"" << args[i] << " " << args[i + 1] + "\" is not an option pair.\n";

				// Put the new argument pair in the map,
				// overwriting any previous pair with the
				// same key.
				pairs.insert( args[i].substr( 2 ), args[i + 1] );

				// Skip one string ahead over the pair
				i++;
			}
		}
	}

	void parse_flags ( string[] args )
	{
		for ( int i = 0 ; i < args.length ; i++ )
		{
			// Check that the string matches the flag option format
			if ( args[i].matches( "-\\p{Alnum}?" ) )
			{
				// Add each flag in the string to the flags set
				for (int j = 1 ; j < args[i].length() ; j++ )
					flags.insert( args[i].at( j ) );
			}
		}
	}
}