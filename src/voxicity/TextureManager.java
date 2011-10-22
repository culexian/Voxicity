package voxicity;

import java.util.HashMap;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

class TextureManager
{
	static HashMap<String, Texture> textures = new HashMap<String, Texture>();

	private TextureManager()
	{

	}

	static int get_texture( String name )
	{
		if ( textures.containsKey( name ) )
			return textures.get( name ).getTextureID();

		try
		{
			Texture new_tex = TextureLoader.getTexture( "PNG", ResourceLoader.getResourceAsStream( name ) );
			System.out.println( "Loaded texture: " + new_tex );

			textures.put( name, new_tex );

			return new_tex.getTextureID();
		}
		catch ( java.io.IOException e )
		{
			e.printStackTrace();
			System.exit(0);
		}

		return 0;
	}
}
