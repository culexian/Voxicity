package voxicity;

public class BlockLoc
{
	public World world;
	public int x, y, z;

	public BlockLoc( int x, int y, int z, World world )
	{
		this.world = world;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Chunk get_chunk()
	{
		if ( world == null )
			return null;

		return world.get_chunk( x, y, z );
	}

	public Block get_block()
	{
		if ( get_chunk() == null )
			return null;

		int chunk_x = x % Constants.Chunk.side_length;
		int chunk_y = y % Constants.Chunk.side_length;
		int chunk_z = z % Constants.Chunk.side_length;

		return get_chunk().get_block( chunk_x, chunk_y, chunk_z );
	}
}
