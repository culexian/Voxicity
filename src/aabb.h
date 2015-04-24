#ifndef AABB_H
#define AABB_H

#include "constants.h"
#include "vector3f.h"

#include <string>

class AABB
{
    Vector3f min;
    Vector3f max;

    public:
    // Construct the AABB volume.
    // Select the min and max points
    // from the two vectors supplied
    AABB( const Vector3f& a, const Vector3f& b );

    // Construct an AABB from dimensions of this vector extending positively from the origin
    AABB( const Vector3f& dimensions );
    AABB( float x, float y, float z );

    // Constructs an AABB covering the two given AABBs
    AABB( const AABB& a, const AABB& b );

    const Vector3f& min_corner() const;
    const Vector3f& max_corner() const;

    float max_x() const;
    float min_x() const;
    float max_y() const;
    float min_y() const;
    float max_z() const;
    float min_z() const;

    // Returns the vector to the center point of the volume
    Vector3f position() const;

    // Return a vector of the distance from the center point
    // to the maximum corner. X is width, Y is height, z is depth.
    Vector3f dimensions() const;

    void translate( const Vector3f& v );
    void translate( float x, float y, float z );
    void scale( float x, float y, float z );
    void center_on( const Vector3f& v );
    void center_on( float x, float y, float z );

    bool intersects( const AABB& rhs ) const;
    float collision_distance( const Vector3f& start, const Vector3f& length ) const;
    Direction collision_side( const Vector3f& start, const Vector3f& length ) const;
    float collision_side_distance( const Vector3f& start, const Vector3f& length, Direction dir ) const;
    float ray_plane_intersect( const Vector3f& line_start, const Vector3f& line_dir, const Vector3f& plane_normal, const Vector3f& plane_point ) const;
    Vector3f get_vert( int i ) const;

    // Test an AABB against the X-axis
    bool intersects_x( const AABB& rhs ) const;

    // Test and AABB against the Y-axis
    bool intersects_y( const AABB& rhs ) const;

    // Test an AABB against the Z-axis
    bool intersects_z( const AABB& rhs ) const;

    // Test an AABB against the XY-corridor
    bool intersects_xy( const AABB& rhs ) const;

    // Test an AABB against the XZ-corridor
    bool intersects_xz( const AABB& rhs ) const;

    // Test an AABB against the YZ-corridor
    bool intersects_yz( const AABB& rhs ) const;

    bool collides_point( const Vector3f& point ) const;
    bool collides_x_col( const Vector3f& point ) const;
    bool collides_y_col( const Vector3f& point ) const;
    bool collides_z_col( const Vector3f& point ) const;

    std::string toString() const;
};

#endif // AABB_H
