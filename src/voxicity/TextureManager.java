/*
 * Copyright 2011, Erik Lund
 *
 * This file is part of Voxicity.
 *
 *  Voxicity is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Voxicity is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Voxicity.  If not, see <http://www.gnu.org/licenses/>.
 */


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
