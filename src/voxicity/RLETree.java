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

import java.nio.ByteBuffer;

public class RLETree
{
	private class Node
	{
		public int pos;
		public int data;

		public Node next, prev;
		public Node left, right;
		public int height;

		public Node( int pos, int data )
		{
			this.pos = pos;
			this.data = data;
			this.height = 0;
		}

		public String toString()
		{
			return "(" + pos + " " + data + ")";
		}
	}

	Node head = new Node( 0, 0 );
	Node root = head;
	int size = 1;

	public synchronized void load( ByteBuffer buf )
	{
		int remaining = buf.getInt();

		Node tail = new Node( buf.getInt(), buf.getInt() );
		head = tail;
		root = tail;

		remaining -= 2 * Integer.SIZE;

		while( remaining != 0 )
		{
			Node a = new Node( buf.getInt(), buf.getInt() );
			list_insert_after( a, tail );
			root = tree_insert( root, a );
			tail = a;
			remaining -= 2 * Integer.SIZE;
		}
	}

	public synchronized void load( java.io.DataInputStream in ) throws java.io.IOException
	{
		int remaining = in.readInt();

		Node tail = new Node( in.readInt(), in.readInt() );
		head = tail;
		root = tail;

		remaining -= 2 * Integer.SIZE;

		while( remaining != 0 )
		{
			Node a = new Node( in.readInt(), in.readInt() );
			list_insert_after( a, tail );
			root = tree_insert( root, a );
			tail = a;
			remaining -= 2 * Integer.SIZE;
		}
	}

	synchronized int get( int pos )
	{
		if ( pos < 0 )
			return -1;

		return tree_search( root, pos ).data;
	}

	/* Currently seeks through the linked list of runs */
	private Node seek_node( int pos )
	{
		// Stores the current node being looked at
		Node node = head;

		// Until completion
		while ( true )
		{
			// If already at pos, return this one
			if ( node.pos == pos )
				return node;

			// If this is the end of the list, return this one
			if ( node.next == null )
				return node;

			// node.next is not null from here

			// If the next node is greater than pos, return this one
			if ( node.next.pos > pos )
				return node;

			// If the next node is closer to pos, move on there
			if ( node.next.pos <= pos )
				node = node.next;
		}
	}

	// Will always succeed. pos is never less than 0 and runs stretch infinitely if unhindered
	synchronized void set( int pos, int data )
	{
		if ( pos < 0 )
		{
			System.out.println( "Error! No runs starting at less than 0 allowed!" );
			return;
		}

		Node node = new Node( pos, data );

		// Get the node containing this position
		Node start = tree_search( root, node.pos );
		Node prev = start.prev;
		Node next = start.next;

		// Nothing to do, the position in this run already has that data
		if ( start.data == node.data )
			return;

		// node.data != start.data from here

		// If at the start of the run, insert there
		if ( node.pos == start.pos )
		{
			// At the start of a single element run
			// Just set the data and try collapse
			if ( ( start.next != null ) && ( start.next.pos == ( start.pos + 1 ) ) )
			{
				// Set the data
				start.data = node.data;

				// Try collapsing nodes
				collapse( start );

				return;
			}
			else // At the start of a longer run
			{
				// Push start up by one
				start.pos += 1;

				// Insert node to tree
				root = tree_insert( root, node );

				// Insert node to list
				list_insert_before( node, start );

				// Try collapsing nodes
				collapse( node );

				return;
			}
		}
		else // Somewhere out in a run, by definition in a run longer than 1 length
		{
			// If splitting the end of the run
			if ( ( start.next != null ) && ( start.next.pos == ( node.pos + 1 ) ) )
			{
				// Insert node to tree
				root = tree_insert( root, node );

				// Insert node after start to list
				list_insert_after( node, start );

				// Try collapsing the nodes
				collapse( node );

				return;
			}
			else // Inside the run, need to split it
			{
				// New run with the old data at pos + 1
				Node split = new Node( node.pos + 1, start.data );

				// Insert split to tree
				root = tree_insert( root, split );

				// Insert node to tree
				root = tree_insert( root, node );

				// Insert node after start to list
				list_insert_after( node, start );

				// Insert split after node to list
				list_insert_after( split, node );

				// Try collapsing the nodes, start from the back
				collapse( split );
				collapse( node );

				return;
			}
		}
	}

	// Inserts Node a before node before Node b in the linked list
	// Sets head if needed
	private void list_insert_before( Node a, Node b )
	{
		// Don't operate on null
		if ( ( a == null ) || ( b == null ) )
			return;

		// Do not add nodes in unsorted order
		if ( a.pos >= b.pos )
			return;

		// Inserting before head
		if ( b == head )
		{
			// Link a and b
			a.prev = null;
			a.next = b;
			b.prev = a;
			// Set the new head
			head = a;
		}
		else // Inserting into the list
		{
			// Link a and b's prev
			b.prev.next = a;
			a.prev = b.prev;

			// Link a and b
			a.next = b;
			b.prev = a;
		}

		// Increase the size of the list
		size++;
	}

	// Insert Node a after Node b in the linked list
	private void list_insert_after( Node a, Node b )
	{
		// Don't operate on null
		if ( ( a == null ) || ( b == null ) )
			return;

		// Do not add nodes in unsorted order
		if ( a.pos <= b.pos )
			return;

		// No next item on the list
		if ( b.next == null )
		{
			//Reset a's next
			a.next = null;

			// Link a and b
			b.next = a;
			a.prev = b;
		}
		else // Inside the list
		{
			// Link a and b's next
			b.next.prev = a;
			a.next = b.next;

			// Link a and b
			a.prev = b;
			b.next = a;
		}

		// Increase the size of the list
		size++;
	}

	// Removes a Node from the linked list
	// Sets the head if needed
	private void list_remove( Node n )
	{
		// Don't operate on null
		if ( n == null )
			return;

		// If we're removing the head
		if ( n == head )
		{
			// New head has no previous node
			n.next.prev = null;
			// Set the new head
			head = n.next;
		}
		else if ( n.next == null ) // Removing the tail
		{
			// Link the previous to the next( null )
			n.prev.next = null;
		}
		else // Inside the list
		{
			// Link the previous and next nodes to eachother
			n.prev.next = n.next;
			n.next.prev = n.prev;
		}

		// Decrease the list size
		size--;
	}

	// Try to collapse Node n in both directions
	private void collapse( Node n )
	{
		// Don't operate on null
		if ( n == null )
			return;

		// Try next first as prev first might remove n from the list
		collapse( n, n.next );
		collapse( n.prev, n );
	}

	private void collapse( Node a, Node b )
	{
		// Don't operate on null
		if ( ( a == null ) || ( b == null ) )
			return;

		// Check that the links work( shouldn't ever fail if all else is done right )
		// Also check that a and b share the same data value
		if ( ( a.next == b ) && ( b.prev == a ) && ( a.data == b.data ) )
		{
			// Remove b from tree
			root = tree_delete( root, b );

			// Remove b from the list
			list_remove( b );
		}
	}

	// Insert a new node into the tree at the first available position
	// that fits its pos attribute starting from _root
	// Return the new _root
	private static Node tree_insert( Node _root, Node node )
	{
		// If node is null, keep the root
		if ( node == null )
			return _root;

		// If _root is null, return the new _root
		if ( _root == null )
			return node;

		// If a node with this pos already exists, return without doing anything
		if ( node.pos == _root.pos )
			return _root;

		// If node.pos is less than this root, add it to the left subtree
		if ( node.pos < _root.pos )
			_root.left = tree_insert( _root.left, node );
		else // node.pos is greater than _root.pos
			_root.right = tree_insert( _root.right, node );

		// Balance the tree
		return tree_balance( _root );
	}

	// Remove a node from the tree
	// and chooses a successor if needed
	// Return the new root
	private static Node tree_delete( Node _root, Node node )
	{
		// If either _root or node is invalid, return
		if ( _root == null || node == null )
			return _root;

		// If zero or one child, use simple remove
		if ( ( node.left == null ) || ( node.right == null ) )
			_root = tree_simple_remove( _root, node );
		else // Two children, composite event
		{
			Node heir = find_heir( node );

			// Remove the heir from the tree
			_root = tree_simple_remove( _root, heir );

			// Replace node with heir in the tree
			_root = tree_replace( _root, node, heir );
		}

		return _root;
	}

	// Removes a node from the tree using the simple method
	// No replacement takes place
	private static Node tree_simple_remove( Node _root, Node node )
	{
		// Do not operate on null
		if ( ( _root == null ) || ( node == null ) )
			return _root;

		// Do not attempt to remove a node with two children
		if ( ( node.left != null ) && ( node.right != null ) )
			return tree_balance( _root );

		// Found the node to be removed
		// If left is null, return right and vice-versa
		// If both are null, return null
		if ( _root == node )
			_root = ( _root.left == null ) ? _root.right : _root.left;
		else if ( node.pos < _root.pos ) // node.pos is less than _root.pos
			_root.left = tree_simple_remove( _root.left, node );
		else // node.pos is greater than _root.pos
			_root.right = tree_simple_remove( _root.right, node );

		return tree_balance( _root );
	}

	// Find an heir for the root node, return that.
	// _root node has 2 children at this point
	// Returns the right-most node in the left subtree i.e. _root.prev
	// or the left-most node in the right subtree i.e. _root.next
	// return node will always have one or no children.
	private static Node find_heir( Node _root )
	{
		Node cur = _root.left;

		while ( cur.right != null )
			cur = cur.right;

		return cur;
	}

	// Searches in tree _root for Node old and replaces it with Node heir
	private static Node tree_replace( Node _root, Node old, Node heir )
	{
		// If something is invalid, do nothing
		if ( _root == null || old == null || heir == null )
			return _root;

		// If the old node has been found, copy links from old to heir
		// Then return heir as the new _root
		if ( _root.pos == old.pos )
		{
			heir.left = old.left;
			heir.right = old.right;
			update_height( heir );
			return heir;
		}

		// Not found, search subtrees

		// If old is in the left subtree, search there
		// and replace if needed
		if ( old.pos < _root.pos )
			_root.left =  tree_replace( _root.left, old, heir );
		else // It's in the right subtree, try replacing there
			_root.right = tree_replace( _root.right, old, heir );

		return _root;
	}

	// Search the tree from root to a run using binary search
	// and returning the Node containing the requested pos in its run
	private static Node tree_search( Node _root, int pos )
	{
		if ( pos < 0 )
		{
			System.out.println( "No positions less than 0 allowed." );
			return null;
		}

		// If the _root is null, return null
		if ( _root == null )
			return null;

		// If _root.pos is equal to to pos, return _root
		if ( pos == _root.pos )
			return _root;

		// If pos is less than _root.pos, search the left subtree
		// If there is no left subtree, return the previous list node
		if ( pos < _root.pos )
		{
			if ( _root.left == null )
			{
				return _root.prev;
			}
			else
				return tree_search( _root.left, pos );
		}
		else // pos > _root.pos, work through the right subtree
		{
			// Search the right node if it exists
			if ( _root.right == null )
				return _root;
			else // Right node exists, check it
				return tree_search( _root.right, pos );
		}
	}

	// Rebalances the tree at _root
	// Returns the new _root
	private static Node tree_balance( Node _root )
	{

		// Nothing to do, return null
		if ( _root == null )
			return _root;

		// _root has no children, is at height 0
		if ( _root.left == null && _root.right == null )
		{
			update_height( _root );
			return _root;
		}

		// Check for a right-heavy tree
		if ( compare_height( _root.right, _root.left ) > 1 )
		{
			// Check for a left-leaning, right subtree.
			// If so, do Right-rotation on right subtree
			if ( compare_height( _root.right.left, _root.right.right ) > 0 )
				_root.right = tree_right_rotate( _root.right );

			// Then do Left-rotation on _root
			_root = tree_left_rotate( _root );
		}
		// Check for a left-heavy tree
		else if ( compare_height( _root.left, _root.right ) > 1 )
		{
			// Check for a right-leaning, left subtree.
			// If so, do Left-rotation left subtree
			if ( compare_height( _root.left.right, _root.left.left ) > 0 )
				_root.left = tree_left_rotate( _root.left );

			// Then do Right-rotation on _root
			_root = tree_right_rotate( _root );
		}

		update_height( _root );

		return _root;
	}

	// Perform a tree rotation to the left
	// Update heights and return new root
	private static Node tree_left_rotate( Node _root )
	{
		Node new_root = _root.right;
		_root.right = new_root.left;
		new_root.left = _root;
		update_height( _root );
		update_height( new_root );
		return new_root;
	}

	// Perform a tree rotation to the right
	// Update heights and return new root
	private static Node tree_right_rotate( Node _root )
	{
		Node new_root = _root.left;
		_root.left = new_root.right;
		new_root.right = _root;
		update_height( _root );
		update_height( new_root );
		return new_root;
	}

	// Return difference in Node a and Node b's heights
	private static int compare_height( Node a, Node b )
	{
		return get_height( a ) - get_height( b );
	}

	// Return the height of a node or 0
	private static int get_height( Node a )
	{
		return a == null ? 0 : a.height;
	}

	// Set the height of the this node to the height of its highest child + 1
	private static void update_height( Node _root )
	{
		_root.height = 1 + Math.max( get_height( _root.left ), get_height( _root.right ) );
	}

	static String tree_traverse( Node _root )
	{
		if ( _root == null )
			return "";
		else
			return "(" + tree_traverse( _root.left ) + " " + _root.pos + " " + _root.data + " " + tree_traverse( _root.right ) + ")";
	}

	public String toString()
	{
		Node node = head;
		int count = 0;
		String out = new String();

		while ( node != null )
		{
			
			out += node.toString();
			node = node.next;
			count++;
		}

		String tree_out = tree_traverse( root );

		out += " " + count + " runs.\n";

		out += tree_out;

		return out;
	}

	synchronized ByteBuffer serialize()
	{
		// Allocate a buffer with space for an int and the rest of the list
		ByteBuffer buf = ByteBuffer.allocate( Integer.SIZE + 2 * Integer.SIZE * size );

		// Each run is 2 ints = 2 * Integer.SIZE
		buf.putInt( 2 * Integer.SIZE * size );

		// Iterate over the runs and write them to the bufer
		Node cur = head;
		while ( cur != null )
		{
			buf.putInt( cur.pos );
			buf.putInt( cur.data );
			cur = cur.next;
		}

		buf.rewind();

		return buf;
	}

	synchronized void serialize( java.io.DataOutputStream out ) throws java.io.IOException
	{
		// Write the size of this variable length list
		out.writeInt( 2 * Integer.SIZE * size );

		// Iterate over the runs and write them to the buffer
		Node cur = head;
		while ( cur != null )
		{
			out.writeInt( cur.pos );
			out.writeInt( cur.data );
			cur = cur.next;
		}
	}
}
