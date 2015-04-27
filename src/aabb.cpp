#include "aabb.h"

#include <iostream>
#include <limits>

// Construct the AABB volume.
// Select the min and max points
// from the two vectors supplied
AABB::AABB( const Vector3f& a, const Vector3f& b ):
        min( std::min( a.x, b.x ),
            std::min( a.y, b.y ),
            std::min( a.z, b.z ) ),
        max( std::max( a.x, b.x ),
            std::max( a.y, b.y ),
            std::max( a.z, b.z ) )
{}

AABB::AABB()
    : AABB( Vector3f(), Vector3f() )
{}

// Construct an AABB from dimensions of this vector
AABB::AABB( const Vector3f& dimensions ):
    AABB( Vector3f(), dimensions )
{}

// Alternate version of AABB dimensional specification
// by floats
AABB::AABB( float x, float y, float z ):
    AABB( Vector3f( x, y, z ) )
{}

// Construct the AABB of two AABBs
// Uses the min and max values of both
AABB::AABB( const AABB& a, const AABB& b )
{
    auto a_min( a.min_corner() );
    auto b_min( b.min_corner() );
    auto a_max( a.max_corner() );
    auto b_max( b.max_corner() );

    min.set( std::min( a_min.x, b_min.x ),
        std::min( a_min.y, b_min.y ),
        std::min( a_min.z, b_min.z ) );

    max.set( std::max( a_max.x, b_max.x ),
        std::max( a_max.y, b_max.y ),
        std::max( a_max.z, b_max.z ) );
}

// Returns a copy of the minimum corner
const Vector3f& AABB::min_corner() const
{
    return min;
}

// Returns a copy of the maximum corner
const Vector3f& AABB::max_corner() const
{
    return max;
}

// Return the maximum x-extent
float AABB::max_x() const
{
    return max.x;
}

// Return the minimum x-extent
float AABB::min_x() const
{
    return min.x;
}

// Return the maximum y-extent
float AABB::max_y() const
{
    return max.y;
}

// Return the minimum y-extent
float AABB::min_y() const
{
    return min.y;
}

// Return the maximum z-extent
float AABB::max_z() const
{
    return max.z;
}

// Return the minimum z-extent
float AABB::min_z() const
{
    return min.z;
}

// Returns the vector to the center point of the volume
Vector3f AABB::position() const
{
    return Vector3f( ( min.x + max.x ) / 2,
        ( min.y + max.y ) / 2,
        ( min.z + max.z ) / 2 );
}

// Return a vector of the distance from the center point
// to the maximum corner. X is width, Y is height, z is depth.
Vector3f AABB::dimensions() const
{
    return Vector3f( ( max.x - min.x ) / 2,
        ( max.y - min.y ) / 2,
        ( max.z - min.z ) / 2 );
}

void AABB::translate( const Vector3f& v )
{
    Vector3f::add( min, v, min );
    Vector3f::add( max, v, max );
}

void AABB::translate( float x, float y, float z )
{
    translate( Vector3f( x, y, z ) );
}

void AABB::scale( float x, float y, float z )
{
    Vector3f center = position();
    Vector3f dim = dimensions();

    dim.x *= x;
    dim.y *= y;
    dim.z *= z;

    min.set( -dim.x, -dim.y, -dim.z );
    max.set( dim.x, dim.y, dim.z );

    center_on( center );
}

void AABB::center_on( const Vector3f& v )
{
    Vector3f delta = Vector3f::sub( v, position(), delta );
    translate( delta );
}

void AABB::center_on( float x, float y, float z )
{
    center_on( Vector3f( x, y, z ) );
}

bool AABB::intersects( const AABB& rhs ) const
{
    // Check each axis for collision in standard AABB style
    if ( max_x() < rhs.min_x() ||
         min_x() > rhs.max_x() ||
         max_y() < rhs.min_y() ||
         min_y() > rhs.max_y() ||
         max_z() < rhs.min_z() ||
         min_z() > rhs.max_z() )
        return false;

    // If all the checks fail, the AABBs collide
    return true; 
}

float AABB::collision_distance( const Vector3f& start, const Vector3f& length ) const
{
    /* Multisampling code, maybe replace later */
    AABB tiny_box( 0.000005f, 0.000005f, 0.000005f );
    tiny_box.center_on( start );
    int samples = static_cast<int>(length.lengthSquared() * 10);
    Vector3f sample_step = length;
    sample_step.scale( 1 / ( samples * 1.0f ) );

    for ( int i = 0 ; i < samples ; i++ )
    {
        if ( intersects( tiny_box ) )
        {
            Vector3f distance = Vector3f::sub( tiny_box.position(), start, distance );
            return distance.length();
        }

        tiny_box.translate( sample_step );
    }

    return std::numeric_limits<float>::infinity();
}

Direction AABB::collision_side( const Vector3f& start, const Vector3f& length ) const
{
    float nearest_distance = std::numeric_limits<float>::infinity();
    Direction nearest_dir = None;

    Vector3f dir_normal = length.normalise( dir_normal );

    for ( int dir = East ; dir != None ; dir++ )
    {
        float distance = collision_side_distance( start, dir_normal, static_cast<Direction>(dir) );

        if ( distance < std::numeric_limits<float>::infinity() )
        {
            Vector3f collide_point = dir_normal;
            collide_point.scale( distance );
            collide_point = Vector3f::add( start, collide_point, collide_point );

            bool collides_col = false;

            switch ( dir )
            {
                case West:
                case East:
                    collides_col = collides_x_col( collide_point );
                break;

                case North:
                case South:
                    collides_col = collides_z_col( collide_point );
                break;

                case Up:
                case Down:
                    collides_col = collides_y_col( collide_point );
                break;
            }

            if ( collides_col && distance < nearest_distance )
            {
                nearest_distance = distance;
                nearest_dir = static_cast<Direction>(dir);
            }
        }

    }

    std::printf( "Nearest side found is %f away on the %s side \n", nearest_distance, directions[nearest_dir].c_str() );

    return nearest_dir;
}

float AABB::collision_side_distance( const Vector3f& start, const Vector3f& length, Direction dir ) const
{
    if ( ( dir == All ) || ( dir == None ) )
        return std::numeric_limits<float>::infinity();

    float distance = std::numeric_limits<float>::infinity();

    std::cout << "Checking for collision on side " << dir << std::endl;

    switch ( dir )
    {
        case North:
            distance = ray_plane_intersect( start, length, Vector3f( 0.0f, 0.0f, 1.0f ), Vector3f( position().x, position().y, max_z() ) );
        break;
        case East:
            distance = ray_plane_intersect( start, length, Vector3f( 1.0f, 0.0f, 0.0f ), Vector3f( max_x(), position().y, position().z ) );
        break;
        case South:
            distance = ray_plane_intersect( start, length, Vector3f( 0.0f, 0.0f, -1.0f ), Vector3f( position().x, position().y, min_z() ) );
        break;
        case West:
            // TODO Check for bug, doesn't fit pattern of two positions, one max/min
            distance = ray_plane_intersect( start, length, Vector3f( -1.0f, 0.0f, 0.0f ), Vector3f( min_x(), position().y, max_z() ) );
        break;
        case Up:
            distance = ray_plane_intersect( start, length, Vector3f( 0.0f, 1.0f, 0.0f ), Vector3f( position().x, max_y(), position().z ) );
        break;
        case Down:
            distance = ray_plane_intersect( start, length, Vector3f( 0.0f, -1.0f, 0.0f ), Vector3f( position().x, min_y(), position().z ) );
        break;
    }

    return distance;
}

float AABB::ray_plane_intersect( const Vector3f& line_start, const Vector3f& line_dir, const Vector3f& plane_normal, const Vector3f& plane_point ) const
{
    float distance = std::numeric_limits<float>::infinity();

    Vector3f line_dir_norm = line_dir.normalise( line_dir_norm );

    float dot_dir = Vector3f::dot( line_dir_norm, plane_normal );

    if ( dot_dir == 0.0f || dot_dir == -0.0f )
        return std::numeric_limits<float>::infinity();

    Vector3f line_start_plane_delta = Vector3f::sub( plane_point, line_start, line_start_plane_delta );

    float line_plane_distance = ( Vector3f::dot( plane_normal, line_start_plane_delta ) / dot_dir );

    if ( line_plane_distance < 0.0f )
        return std::numeric_limits<float>::infinity();

    std::cout << "Returning " << line_plane_distance << std::endl;
    return line_plane_distance;
}

Vector3f AABB::get_vert( int i ) const
{
    float x_coord = ( i - 1 ) / 4 < 1 ? min_x() : max_x();
    float y_coord = ( i - 1 ) % 4 / 2 < 1 ? min_y() : max_y();
    float z_coord = ( i - 1 ) % 2 < 1 ? min_z() : max_z();

    return Vector3f( x_coord, y_coord, z_coord );
}

// Test an AABB against the X-axis
bool AABB::intersects_x( const AABB& rhs ) const
{
    return !( min_x() >= rhs.max_x() || max_x() <= rhs.min_x() );
}

// Test and AABB against the Y-axis
bool AABB::intersects_y( const AABB& rhs ) const
{
    return !( min_y() >= rhs.max_y() || max_y() <= rhs.min_y() );
}

// Test an AABB against the Z-axis
bool AABB::intersects_z( const AABB& rhs ) const
{
    return !( min_z() >= rhs.max_z() || max_z() <= rhs.min_z() );
}

// Test an AABB against the XY-corridor
bool AABB::intersects_xy( const AABB& rhs ) const
{
    return intersects_x( rhs ) && intersects_y( rhs );
}

// Test an AABB against the XZ-corridor
bool AABB::intersects_xz( const AABB& rhs ) const
{
    return intersects_x( rhs ) && intersects_z( rhs );
}

// Test an AABB against the YZ-corridor
bool AABB::intersects_yz( const AABB& rhs ) const
{
    return intersects_y( rhs ) && intersects_z( rhs );
}

bool AABB::collides_point( const Vector3f& point ) const
{
    AABB tiny_box( std::numeric_limits<float>::epsilon() * 8, std::numeric_limits<float>::epsilon() * 8, std::numeric_limits<float>::epsilon() * 8 );
    tiny_box.center_on( point );
    return intersects( tiny_box );
}

bool AABB::collides_x_col( const Vector3f& point ) const
{
    AABB x_col( std::numeric_limits<float>::infinity(), dimensions().y * 2, dimensions().z * 2 );
    x_col.center_on( position() );
    return x_col.collides_point( point );
}

bool AABB::collides_y_col( const Vector3f& point ) const
{
    AABB y_col( dimensions().x * 2, std::numeric_limits<float>::infinity(), dimensions().z * 2 );
    y_col.center_on( position() );
    return y_col.collides_point( point );
}

bool AABB::collides_z_col( const Vector3f& point ) const
{
    AABB z_col( dimensions().x * 2, dimensions().y * 2, std::numeric_limits<float>::infinity() );
    z_col.center_on( position() );
    return z_col.collides_point( point );
}

std::string AABB::toString() const
{
    return min.to_string() + " " + max.to_string();
}
