package voxicity;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

public class Voxicity
{
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

		while ( !Display.isCloseRequested() )
		{
			Display.update();
		}

		Display.destroy();
	}

	public static void main( String[] args )
	{
		System.out.println( "Hello world! I'm Voxicity!" );

		System.out.println( "Making a display now" );
		Voxicity voxy = new Voxicity();
		voxy.init();
	}
}
