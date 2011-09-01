package voxicity;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.Color;
import org.lwjgl.Sys;

public class Voxicity
{
	long last_fps_update = 0;
	int fps_count = 0;

	long last_frame = 0;
	float rot = 0;

	float rot_x;
	float rot_y;

	float camera[] = new float[3];

	boolean is_close_requested = false;

	Block test = new Block( new Vector3f( 0, 0, 100 ), Color.YELLOW );

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

		last_fps_update = get_time_ms();
		get_time_delta();
		setup_camera();
		Mouse.setGrabbed( true );

		while ( !is_close_requested )
		{
			update( get_time_delta() );
			draw();

			is_close_requested |= Display.isCloseRequested();
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
		if ( Keyboard.isKeyDown( Keyboard.KEY_ESCAPE ) )
			is_close_requested = true;

		if ( Keyboard.isKeyDown( Keyboard.KEY_Q ) )
			is_close_requested = true;

		float x_move = 0;
		float z_move = 0;

		if ( Keyboard.isKeyDown( Keyboard.KEY_A ) )
			x_move -= 0.15 * delta;

		if ( Keyboard.isKeyDown( Keyboard.KEY_D ) )
			x_move += 0.15 * delta;

		if ( Keyboard.isKeyDown( Keyboard.KEY_W ) )
			z_move -= 0.15 * delta;

		if ( Keyboard.isKeyDown( Keyboard.KEY_S ) )
			z_move += 0.15 * delta;

		if ( Keyboard.isKeyDown( Keyboard.KEY_SPACE ) ) camera[1] += 0.15 * delta;
		if ( Keyboard.isKeyDown( Keyboard.KEY_C ) ) camera[1] -= 0.15 * delta;

		int x_delta = Mouse.getDX();
		int y_delta = Mouse.getDY();

		rot_x += ( x_delta / 800.0f ) * 45.0f;
		rot_y += ( y_delta / 800.0f ) * ( 45.0f );

		rot_x = rot_x > 360.0f ? rot_x - 360.0f : rot_x;
		rot_x = rot_x < -360.0 ? rot_x + 360.0f : rot_x;

		rot_y = Math.min( rot_y, 90.0f );
		rot_y = Math.max( rot_y, -90.0f );

		float cos_rot_x = ( float ) Math.cos( Math.toRadians( rot_x ) );
		float sin_rot_x = ( float ) Math.sin( Math.toRadians( rot_x ) );

		//System.out.println( "Cos( rot_x ) = " + cos_rot_x );

		float corr_x = ( x_move * cos_rot_x ) - ( z_move * sin_rot_x );
		float corr_z = ( x_move * sin_rot_x ) + ( z_move * cos_rot_x );

		//System.out.println( "Corr. x: " + corr_x + " Corr. z: " + corr_z );

//		System.out.println( x_delta + " " + rot_x );
		rot += 0.15 * delta;
		camera[0] += corr_x;
		camera[2] += corr_z;

		update_fps();
	}

	void draw()
	{
		GL11.glLoadIdentity();
		GL11.glRotatef( -rot_y, 1, 0, 0 );
		GL11.glRotatef( rot_x, 0, 1, 0 );
		GLU.gluLookAt( camera[0], camera[1], camera[2], camera[0], camera[1], camera[2] - 10, 0,1,0 );


		// Clear the screen and depth buffer
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

		test.render();
		new Block( new Vector3f( 0,0, 120 ), Color.BLUE ).render();

		GL11.glTranslatef( 0, 0, -80 );
		GL11.glRotatef( rot, 0, 1, 1 );
		GL11.glTranslatef( 0, 0, 80 );

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

	void update_fps()
	{
		if ( get_time_ms() - last_fps_update > 1000 )
		{
			Display.setTitle( "FPS: " + fps_count );
			fps_count = 0;
			last_fps_update += 1000;
		}
		fps_count++;
	}

	long get_time_ms()
	{
		return (Sys.getTime() * 1000)  / Sys.getTimerResolution();
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
