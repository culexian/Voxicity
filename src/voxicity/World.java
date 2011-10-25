package voxicity;

import java.util.Map;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class World
{
	// Chunk lookup map
	Map< Collection< Integer >, Chunk > chunks = new HashMap< Collection< Integer >, Chunk >();

	public Chunk get_chunk( final int x, final int y, final int z )
	{
		ArrayList< Integer > chunk_id = new ArrayList< Integer >(){{ add(x); add(y); add(z); }};

		if ( chunks.containsKey( chunk_id ) )
			return chunks.get( chunk_id );

		return null;
	}

	public void set_chunk( int x, int y, int z, Chunk chunk )
	{

	}
}
