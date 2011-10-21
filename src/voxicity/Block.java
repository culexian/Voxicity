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
	int pos_x,pos_y,pos_z;
	Color color = new Color();

	int vert_buf = 0;

	public Block( int pos_x, int pos_y, int pos_z, ReadableColor color )
	{
		this.pos_x = pos_x;
		this.pos_y = pos_y;
		this.pos_z = pos_z;
		this.color = new Color( color );

		IntBuffer int_buf = BufferUtils.createIntBuffer(1);
		GL15.glGenBuffers( int_buf );
		this.vert_buf = int_buf.get(0);

		gen_vert_buffer();
	}

	public void render()
	{
		GL11.glPushMatrix();

		GL11.glTranslatef( pos_x, pos_y, pos_z );

		// set the color of the quad (R,G,B,A)
		GL11.glColor3f( color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f );


		GL11.glBegin(GL11.GL_QUADS);

		// Front
		GL11.glVertex3f( -10, -10, -10 );
		GL11.glVertex3f( 10, -10, -10 );
		GL11.glVertex3f( 10, 10, -10 );
		GL11.glVertex3f( -10, 10, -10 );

		// left
		GL11.glVertex3f( -10, -10, -10 );
		GL11.glVertex3f( -10, -10, 10 );
		GL11.glVertex3f( -10, 10, 10 );
		GL11.glVertex3f( -10, 10, -10 );

		// right
		GL11.glVertex3f( 10, -10, -10 );
		GL11.glVertex3f( 10, -10, 10 );
		GL11.glVertex3f( 10, 10, 10 );
		GL11.glVertex3f( 10, 10, -10 );

		// front
		GL11.glVertex3f( -10, -10, 10 );
		GL11.glVertex3f( 10, -10, 10 );
		GL11.glVertex3f( 10, 10, 10 );
		GL11.glVertex3f( -10, 10, 10 );

		// top
		GL11.glVertex3f( -10, 10, -10 );
		GL11.glVertex3f( -10, 10, 10 );
		GL11.glVertex3f( 10, 10, 10 );
		GL11.glVertex3f( 10, 10, -10 );

		// bottom
		GL11.glVertex3f( -10, -10, -10 );
		GL11.glVertex3f( -10, -10, 10 );
		GL11.glVertex3f( 10, -10, 10 );
		GL11.glVertex3f( 10, -10, -10 );

		GL11.glEnd();


		GL11.glTranslatef( 0, 30, 0 );

		int indices[] = { 0, 1, 2, 3 };
		IntBuffer buf = BufferUtils.createIntBuffer( 4 );
		buf.put( indices );
//		buf.rewind();

		IntBuffer buf_name = BufferUtils.createIntBuffer(1);
		GL15.glGenBuffers( buf_name );
		int ind_buf = buf_name.get( 0 );

		GL15.glBindBuffer( GL15.GL_ELEMENT_ARRAY_BUFFER, ind_buf );
		GL15.glBufferData( GL15.GL_ELEMENT_ARRAY_BUFFER, buf, GL15.GL_STATIC_DRAW );

		GL11.glEnableClientState( GL11.GL_VERTEX_ARRAY );
		GL15.glBindBuffer( GL15.GL_ARRAY_BUFFER, vert_buf );
		GL11.glVertexPointer( 3, GL11.GL_FLOAT, 0, 0 );

//		GL11.glEnableClientState( GL11.GL_INDEX_ARRAY );
		GL15.glBindBuffer( GL15.GL_ELEMENT_ARRAY_BUFFER, ind_buf );
//		GL12.glDrawRangeElements( GL11.GL_POINTS, 0, 3, 4, GL11.GL_UNSIGNED_INT, 0 );
//		GL11.glDrawElements( GL11.GL_POINTS, 4, GL11.GL_UNSIGNED_INT, 0 );
		GL11.glDrawArrays( GL11.GL_QUADS, 0, 4 );

		//
//		GL11.glDrawElements( GL11.GL_LINES, buf );
//
		GL15.glBindBuffer( GL15.GL_ELEMENT_ARRAY_BUFFER, 0 );

//		GL11.glDrawArrays( GL11.GL_LINES, 0, 4 );

		GL15.glBindBuffer( GL15.GL_ARRAY_BUFFER, 0 );

		GL15.glDeleteBuffers( ind_buf );

		GL11.glDisableClientState( GL11.GL_VERTEX_ARRAY );

		GL11.glPopMatrix();
	}

	void gen_vert_buffer()
	{
		float[] verts = { -10, -10, -10, 10, -10, -10, 10, 10, -10, -10, 10, -10 };
		FloatBuffer buf = BufferUtils.createFloatBuffer( 12 );
		buf.put( verts );
//		buf.rewind();

		GL15.glBindBuffer( GL15.GL_ARRAY_BUFFER, vert_buf );
		GL15.glBufferData( GL15.GL_ARRAY_BUFFER, buf, GL15.GL_STATIC_DRAW );
		
	}
}
