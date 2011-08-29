package voxicity;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.Sys;

public class Voxicity
{
	// Set the library path for lwjgl
	static
	{
		System.setProperty( "java.library.path", "native" );
	}

	long last_frame = 0;
	float rot = 0;

	float rot_x;
	float rot_y;

	float camera[] = new float[3];

	public void init()
	{
		try
		{
			Display.setDisplayMode( new DisplayMode( 800, 600 ) );
			Display.create();
		}
		catch ( LWJGLException e )
		{
			e.printStackTrace();
			System.exit(0);
		}

		get_time_delta();
		setup_camera();
		Mouse.setGrabbed( true );

		while ( !Display.isCloseRequested() )
		{
			update( get_time_delta() );
			draw();
		}
			Display.destroy();
	}

	int get_time_delta()
	{
		// Get the time in milliseconds
		long new_time = (Sys.getTime() * 1000)  / Sys.getTimerResolution();
		int delta = (int) ( new_time - last_frame );
		last_frame = new_time;
		return delta;
	}

	void update( int delta )
	{
		if ( Keyboard.isKeyDown( Keyboard.KEY_A ) ) camera[0] -= 0.15 * delta;
		if ( Keyboard.isKeyDown( Keyboard.KEY_D ) ) camera[0] += 0.15 * delta;

		if ( Keyboard.isKeyDown( Keyboard.KEY_W ) ) camera[2] -= 0.15 * delta;
		if ( Keyboard.isKeyDown( Keyboard.KEY_S ) ) camera[2] += 0.15 * delta;

		if ( Keyboard.isKeyDown( Keyboard.KEY_SPACE ) ) camera[1] += 0.15 * delta;
		if ( Keyboard.isKeyDown( Keyboard.KEY_C ) ) camera[1] -= 0.15 * delta;

		int x_delta = Mouse.getDX();
		rot_x += ( x_delta / 800.0f ) * 45.0f;

		System.out.println( x_delta + " " + rot_x );
		rot += 0.15 * delta;
	}

	void draw()
	{
		GL11.glLoadIdentity();
		GLU.gluLookAt( camera[0], camera[1], camera[2], camera[0], camera[1], camera[2] - 10, 0,1,0 );
		GL11.glRotatef( rot_x, 0, 1, 0 );
		GL11.glTranslatef( 0, 0, -80 );
		//GL11.glRotatef( rot, 0, 1, 1 );
		GL11.glTranslatef( 0, 0, 80 );

		// Clear the screen and depth buffer
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

		GL11.glBegin(GL11.GL_QUADS);

		// Front
		GL11.glColor3f( 1.0f, 0.0f, 0.0f );
		GL11.glVertex3f( -10, -10, -90 );
		GL11.glColor3f( 0.0f, 1.0f, 0.0f );
		GL11.glVertex3f( 10, -10, -90 );
		GL11.glColor3f(0.5f,0.5f,1.0f);
		GL11.glVertex3f( 10, 10, -90 );
		GL11.glColor3f( 0.0f, 0.0f, 1.0f );
		GL11.glVertex3f( -10, 10, -90 );

		// left
		GL11.glVertex3f( -10, -10, -90 );
		GL11.glVertex3f( -10, -10, -70 );
		GL11.glVertex3f( -10, 10, -70 );
		GL11.glVertex3f( -10, 10, -90 );

		// right
		GL11.glVertex3f( 10, -10, -90 );
		GL11.glVertex3f( 10, -10, -70 );
		GL11.glVertex3f( 10, 10, -70 );
		GL11.glVertex3f( 10, 10, -90 );

		// front
		GL11.glColor3f( 0.5f, 1.0f, 0.0f );
		GL11.glVertex3f( -10, -10, -70 );
		GL11.glVertex3f( 10, -10, -70 );
		GL11.glVertex3f( 10, 10, -70 );
		GL11.glVertex3f( -10, 10, -70 );

		// top
		GL11.glColor3f( 1.0f, 0.5f, 0.0f );
		GL11.glVertex3f( -10, 10, -90 );
		GL11.glVertex3f( -10, 10, -70 );
		GL11.glVertex3f( 10, 10, -70 );
		GL11.glVertex3f( 10, 10, -90 );

		// bottom
		GL11.glColor3f( 0.0f, 0.5f, 1.0f );
		GL11.glVertex3f( -10, -10, -90 );
		GL11.glVertex3f( -10, -10, -70 );
		GL11.glVertex3f( 10, -10, -70 );
		GL11.glVertex3f( 10, -10, -90 );
		GL11.glEnd();

		Display.update();
	}

	void setup_camera()
	{
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GLU.gluPerspective( 45.0f, 1.333f, 1f, 1000f );
		GL11.glMatrixMode(GL11.GL_MODELVIEW);

		GL11.glShadeModel( GL11.GL_SMOOTH );
		GL11.glEnable( GL11.GL_DEPTH_TEST );
	}

	public static void main( String[] args )
	{
		System.out.println( "Hello world! I'm Voxicity!" );

		System.out.println( "Making a display now" );
		Voxicity voxy = new Voxicity();
		voxy.init();
	}
}
