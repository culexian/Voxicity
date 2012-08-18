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

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class InputState
{
	public boolean
		move_left,
		move_right,
		move_forward,
		move_backward,
		ascend,
		descend,
		toggle_mouse,
		toggle_flying,
		hit_action,
		use_action,
		quit;

	public int
		x_delta,
		y_delta;

	public InputState()
	{
		while ( Keyboard.next() )
		{
			toggle_mouse = ( Keyboard.getEventKey() == Keyboard.KEY_E && Keyboard.getEventKeyState() );
			toggle_flying = ( Keyboard.getEventKey() == Keyboard.KEY_G && Keyboard.getEventKeyState() );
			quit |= ( Keyboard.getEventKey() == Keyboard.KEY_ESCAPE && Keyboard.getEventKeyState() );
			quit |= ( Keyboard.getEventKey() == Keyboard.KEY_Q && Keyboard.getEventKeyState() );
		}

		move_left = Keyboard.isKeyDown( Keyboard.KEY_A );
		move_right = Keyboard.isKeyDown( Keyboard.KEY_D );
		move_forward = Keyboard.isKeyDown( Keyboard.KEY_W );
		move_backward = Keyboard.isKeyDown( Keyboard.KEY_S );
		ascend = Keyboard.isKeyDown( Keyboard.KEY_SPACE );
		descend = Keyboard.isKeyDown( Keyboard.KEY_C );
		hit_action = Mouse.isButtonDown( 0 );
		use_action = Mouse.isButtonDown( 1 );

		x_delta = Mouse.getDX();
		y_delta = Mouse.getDY();
	}
}
