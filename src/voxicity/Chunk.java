package voxicity;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.Color;

public class Chunk
{
	int x, y, z;

	Block[] blocks = new Block[Constants.Chunk.block_number];

	public Chunk( int x, int y, int z )
	{
		this.x = x * Constants.Chunk.side_length;
		this.y = y * Constants.Chunk.side_length;
		this.z = z * Constants.Chunk.side_length;

		generate_blocks();
	}

	public int get_block_pos( int x, int y, int z )
	{
		int block_pos = 0;
		block_pos += x;
		block_pos += y * Constants.Chunk.side_length * Constants.Chunk.side_length;
		block_pos += z * Constants.Chunk.side_length;

		return block_pos;
	}

	public Block get_block( int x, int y, int z )
	{
		int block_pos = get_block_pos( x, y, z );

		if ( ( block_pos < 0 ) || ( block_pos > Constants.Chunk.block_number - 1 ) )
			return null;

		return blocks[block_pos];
	}

	public void set_block( int x, int y, int z, Block block )
	{
		int block_pos = get_block_pos( x, y, z );
		if ( ( block_pos < 0 ) || ( block_pos > Constants.Chunk.block_number - 1 ) )
			return;

		blocks[block_pos] = block;
	}

	public void draw()
	{
		GL11.glPushMatrix();
		GL11.glTranslatef( x, y, z );

		for ( Block block : blocks )
		{
			if ( block != null )
				block.render();
		}

		GL11.glPopMatrix();
	}

	public void generate_blocks()
	{
		for ( int i = 0 ; i < 1500; i++ )
		{
			int x = i % 32;
			int y = i / 32 / 32 % 32;
			int z = i / 32 % 32;
			blocks[i] = new Block( x, y, z, new Color( 100, 100, 100 + i * 5) );
		}
	}
}
