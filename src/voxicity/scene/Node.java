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

import java.util.list;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

class Node
{
	Vector3f pos = new Vector3f();

	Node parent = null;
	List<Node> children = new ArrayList<Node>();

	boolean dirty = true;

	public void clean()
	{
		if ( dirty )
		{
			// Clean all child nodes
			for ( Node child : children )
				child.clean();

			// Then clean this node
			clean_self();
		}
	}

	abstract void clean_self();

	public void mark()
	{
		dirty = true;
		if ( parent != null )
			parent.mark();
	}

	public void render()
	{
		GL11.glPushMatrix();

			GL11.glTranslatef( pos.x, pos.y, pos.z );
			for( Node child : children )
				child.render();

		GL11.glPopMatrix();
	}

	public boolean has_child( Node child )
	{
		for ( Node node : children )
			if ( node == child )
				return true;

		return false;
	}

	public void add_child( Node child )
	{
		if ( !has_child( child ) )
			children.add( child );
	}

	public void remove_child( Node child )
	{
		Iterator<Node> iter = children.iterator();

		while ( iter.hasNext() )
			if ( iter.next() == child )
				iter.remove();
	}
}
