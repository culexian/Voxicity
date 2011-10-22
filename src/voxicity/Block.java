package voxicity;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL15;
import org.lwjgl.util.ReadableColor;
import org.lwjgl.util.Color;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.opengl.ARBVertexBufferObject;
import org.lwjgl.BufferUtils;
import java.nio.IntBuffer;
import java.nio.FloatBuffer;

public class Block
{
	int block_id = 0;
	int block_data = 0;

	int pos_x,pos_y,pos_z;
	Color color = new Color();

	int vert_buf = 0;
	int index_buf = 0;

	public Block( int pos_x, int pos_y, int pos_z, ReadableColor color )
	{
		this.pos_x = pos_x;
		this.pos_y = pos_y;
		this.pos_z = pos_z;
		this.color = new Color( color );

		IntBuffer int_buf = BufferUtils.createIntBuffer(2);
		GL15.glGenBuffers( int_buf );

		this.vert_buf = int_buf.get(0);
		this.index_buf = int_buf.get(1);

		gen_vert_buffer();
		gen_index_buffer();
	}

	public void render()
	{
		GL11.glPushMatrix();

		// Translate to block location offset
		GL11.glTranslatef( pos_x, pos_y, pos_z );

		// set the color of the quad (R,G,B,A)
		GL11.glColor3f( color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f );

		// Bind VBO to vertex pointer
		GL11.glEnableClientState( GL11.GL_VERTEX_ARRAY );
		GL15.glBindBuffer( GL15.GL_ARRAY_BUFFER, vert_buf );
		GL11.glVertexPointer( 3, GL11.GL_FLOAT, 0, 0 );

		// Bind index array
		GL15.glBindBuffer( GL15.GL_ELEMENT_ARRAY_BUFFER, index_buf );

		// Draw the block
		GL12.glDrawRangeElements( GL11.GL_QUADS, 0, 7, 24, GL11.GL_UNSIGNED_INT, 0 );

		// Unbind both buffers
		GL15.glBindBuffer( GL15.GL_ELEMENT_ARRAY_BUFFER, 0 );
		GL15.glBindBuffer( GL15.GL_ARRAY_BUFFER, 0 );

		// Disable Vertex pointer
		GL11.glDisableClientState( GL11.GL_VERTEX_ARRAY );

		GL11.glPopMatrix();
	}

	void gen_vert_buffer()
	{
		// 8 vertices make a block
		float[] verts = {
		                   10,  10,  10,
		                   10,  10, -10,
		                   10, -10,  10,
		                   10, -10, -10,
		                  -10,  10,  10,
		                  -10,  10, -10,
		                  -10, -10,  10,
		                  -10, -10, -10
		                };

		// Store the vertices in a buffer
		FloatBuffer buf = BufferUtils.createFloatBuffer( 8 * 3 );
		buf.put( verts );
		buf.rewind();

		// Pass the buffer to a VBO
		GL15.glBindBuffer( GL15.GL_ARRAY_BUFFER, vert_buf );
		GL15.glBufferData( GL15.GL_ARRAY_BUFFER, buf, GL15.GL_STATIC_DRAW );
		
	}

	void gen_index_buffer()
	{
		// Create indexes for a cube, 6 sides, 1 quad each
		int indices[] = {
		                  0, 1, 3, 2, // right
		                  4, 5, 7, 6, // left
		                  0, 1, 5, 4, // top
		                  2, 3, 7, 6, // bottom
		                  1, 3, 7, 5, // back
		                  0, 2, 6, 4, // front
		                };

		// Store the indices in a buffer
		IntBuffer buf = BufferUtils.createIntBuffer( 6 * 4 );
		buf.put( indices );
		buf.rewind();

		// Pass the buffer to an IBO
		GL15.glBindBuffer( GL15.GL_ELEMENT_ARRAY_BUFFER, index_buf );
		GL15.glBufferData( GL15.GL_ELEMENT_ARRAY_BUFFER, buf, GL15.GL_STATIC_DRAW );
	}
}
