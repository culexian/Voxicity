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

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

public class TextureManager
{
	static HashMap<String, Texture> textures = new HashMap<String, Texture>();

	private TextureManager()
	{

	}

	public static int get_texture( String name )
	{
		if ( name == null )
			return 0;

		if ( textures.containsKey( name ) )
			return textures.get( name ).getTextureID();

		try
		{
			Texture new_tex = TextureLoader.getTexture( "PNG", ResourceLoader.getResourceAsStream( name ), true, GL11.GL_NEAREST );
			System.out.println( "Loaded texture: " + new_tex );

			textures.put( name, new_tex );

			int tex_bak = GL11.glGetInteger( GL11.GL_TEXTURE_BINDING_2D );
			
			GL11.glBindTexture( GL11.GL_TEXTURE_2D, new_tex.getTextureID() );
			GL11.glTexParameteri( GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE );

			GL11.glBindTexture( GL11.GL_TEXTURE_2D, tex_bak );

			return new_tex.getTextureID();
		}
		catch ( java.io.IOException e )
		{
			System.out.println( "Failed to load texture " + name );
			e.printStackTrace();
			System.exit(0);
		}

		return 0;
	}
}
