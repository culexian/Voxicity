package voxicity;

import org.lwjgl.util.vector.Vector3f;

public class AABB
{
	public Vector3f pos = new Vector3f();
	public Vector3f dim = new Vector3f();

	public AABB( float width, float height, float depth )
	{
		dim.x = width / 2;
		dim.y = height / 2;
		dim.z = depth / 2;
	}

	public boolean collides( final AABB rhs )
	{
		if ( ( left() < rhs.right() && right() > rhs.left() ) &&
		     ( bottom() < rhs.top() && top() > rhs.bottom() ) &&
		     ( back() < rhs.front() && front() > rhs.back() ) )
			return true;

		return false; 
	}

	public float left()
	{
		return pos.x - dim.x;
	}

	public float right()
	{
		return pos.x + dim.x;
	}

	public float top()
	{
		return pos.y + dim.y;
	}

	public float bottom()
	{
		return pos.y - dim.y;
	}

	public float front()
	{
		return pos.z + dim.z;
	}

	public float back()
	{
		return pos.z - dim.z;
	}

	public float top_intersect( final AABB rhs )
	{
		return top() - rhs.bottom();
	}
}
