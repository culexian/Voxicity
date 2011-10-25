package voxicity;

import java.util.Map;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class World
{
	// Chunk lookup map
	Map< Collection< Integer >, Chunk > chunks = new HashMap< Collection< Integer >, Chunk >();

	public World()
	{
		set_chunk( 0, 0, 0, new Chunk( 0, 0, 0 ) );
	}

	public Chunk get_chunk( int x, int y, int z )
	{
		ArrayList<Integer> id = get_chunk_id( x, y, z );

		if ( chunks.containsKey( get_chunk_id( x, y, z ) ) )
		{
//			System.out.println( "Found chunk " + id );
			return chunks.get( id );
		}

//		System.out.println( "Did not find chunk" );
		return null;
	}

	public void set_chunk( int x, int y, int z, Chunk chunk )
	{
		chunks.put( get_chunk_id( x, y, z ), chunk );
	}

	public ArrayList<Integer> get_chunk_id( int x, int y, int z )
	{
		ArrayList<Integer> id = new ArrayList<Integer>();

		id.add( x / Constants.Chunk.side_length - ( x < 0 ? 1 : 0 ) );
		id.add( y / Constants.Chunk.side_length - ( y < 0 ? 1 : 0 ) );
		id.add( z / Constants.Chunk.side_length - ( z < 0 ? 1 : 0 ) );

		return id;
	}

	public Block get_block( int x, int y, int z )
	{
		BlockLoc loc = new BlockLoc( x, y, z, this );

		return loc.get_block();
	}
}
