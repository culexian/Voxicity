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

import java.awt.Font;

import org.lwjgl.opengl.GL11;

import org.newdawn.slick.Color;
import org.newdawn.slick.TrueTypeFont;

public class TextRenderer
{
	static TrueTypeFont font;

	public static void init()
	{
		Font awt_font = new Font( "Monospace", Font.BOLD, 24 );
		font = new TrueTypeFont( awt_font, true );
	}

	public static void draw( String text, int x, int y )
	{
		GL11.glMatrixMode(GL11.GL_PROJECTION);      // Select Projection
    GL11.glPushMatrix();      // Push The Matrix
    GL11.glLoadIdentity();        // Reset The Matrix
    GL11.glOrtho( 0, 1200 , 720 , 0, -1, 1 );  // Select Ortho Mode
    GL11.glMatrixMode(GL11.GL_MODELVIEW);  // Select Modelview Matrix
    GL11.glPushMatrix();    // Push The Matrix
    GL11.glLoadIdentity();    // Reset The Matrix

		// disable depth testing and enable orthographic view
     GL11.glDisable(GL11.GL_DEPTH_TEST);
//
		font.drawString( x, y, text, Color.green );

		GL11.glEnable(GL11.GL_DEPTH_TEST);

		GL11.glMatrixMode( GL11.GL_PROJECTION ); // Select Projection
    GL11.glPopMatrix();    // Pop The Matrix
    GL11.glMatrixMode( GL11.GL_MODELVIEW );  // Select Modelview
    GL11.glPopMatrix();    // Pop The Matrix

	}
}
