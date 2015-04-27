#include "frustum.h"

#include "util.h"

#include <cmath>

void Frustum::set_attribs( float vert_angle, float ratio, float near_dist, float far_dist )
{
    this->ratio = ratio;
    this->vert_angle_rads = Util::deg_to_rad( vert_angle );
    this->near_dist = near_dist;
    this->far_dist = far_dist;

    this->tan_vert_angle = std::tan( this->vert_angle_rads );

    this->near_height = near_dist * tan_vert_angle;
    this->near_width = near_height * ratio;

    this->far_height = far_dist * tan_vert_angle;
    this->far_width = far_height * ratio;
}

void Frustum::set_pos( const Vector3f& pos, const Vector3f& look, const Vector3f& up, const Vector3f& forward )
{
    this->pos = pos;

    this->look = Vector3f( look );
    this->look = this->look.normalise( this->look );

    this->right = Vector3f::cross( forward, up, this->right );
    this->right = this->right.normalise( this->right );

    this->up = Vector3f::cross( this->right, this->look, this->up );

    Vector3f near_center = Vector3f( this->look );
    near_center.scale( near_dist );

    Vector3f far_center = Vector3f( this->look );
    far_center.scale( far_dist );
}

bool Frustum::contains_point( const Vector3f& point ) const
{
    Vector3f v = Vector3f::sub( point, pos, v );


    float x_dot = Vector3f::dot( v, right );
    float y_dot = Vector3f::dot( v, up );
    float z_dot = Vector3f::dot( v, look );


    if ( z_dot > far_dist || z_dot < near_dist )
        return false;

    float y_extreme = z_dot * tan_vert_angle;
    if ( y_dot > y_extreme || y_dot < -y_extreme )
        return false;

    float x_extreme = y_extreme * ratio;
    if ( x_dot > x_extreme || x_dot < -x_extreme )
        return false;

    return true;
}

// Check if an AABB collides with this->frustum by checking all the points
// in turn against each set of planes
bool Frustum::collides( const AABB& box ) const
{
    // Create an array of all the points, their x_dot, y_dot and z_dot values
    Vector3f points[8];//{ new Vector3f(), new Vector3f(), new Vector3f(), new Vector3f(), new Vector3f(), new Vector3f(), new Vector3f(), new Vector3f() };
    float dots[8][3];

    points[0] = Vector3f::sub( Vector3f( box.min_x(), box.max_y(), box.max_z() ), pos, points[0] );
    points[1] = Vector3f::sub( Vector3f( box.max_x(), box.max_y(), box.max_z() ), pos, points[1] );
    points[2] = Vector3f::sub( Vector3f( box.min_x(), box.min_y(), box.max_z() ), pos, points[2] );
    points[3] = Vector3f::sub( Vector3f( box.max_x(), box.min_y(), box.max_z() ), pos, points[3] );
    points[4] = Vector3f::sub( Vector3f( box.min_x(), box.max_y(), box.min_z() ), pos, points[4] );
    points[5] = Vector3f::sub( Vector3f( box.max_x(), box.max_y(), box.min_z() ), pos, points[5] );
    points[6] = Vector3f::sub( Vector3f( box.min_x(), box.min_y(), box.min_z() ), pos, points[6] );
    points[7] = Vector3f::sub( Vector3f( box.max_x(), box.min_y(), box.min_z() ), pos, points[7] );

    for ( int i = 0 ; i < 8 ; i++ )
    {
        dots[i][0] = Vector3f::dot( points[i], right );
        dots[i][1] = Vector3f::dot( points[i], up );
        dots[i][2] = Vector3f::dot( points[i], look );
    }

    bool z_check = false;

    for ( int i = 0 ; i < 8 ; i++ )
        z_check |= ( dots[i][2] < far_dist );

    if ( !z_check )
        return false;

    for ( int i = 0 ; i < 8 ; i++ )
        z_check |= ( dots[i][2] > near_dist );

    if ( !z_check )
        return false;

    bool y_check = false;

    for ( int i = 0 ; i < 8 ; i++ )
    {
        float y_extreme = dots[i][2] * tan_vert_angle;
        y_check |= ( dots[i][1] < y_extreme );
    }

    if ( !y_check )
        return false;

    for ( int i = 0 ; i < 8 ; i++ )
    {
        float y_extreme = dots[i][2] * tan_vert_angle;
        y_check |= ( dots[i][1] > -y_extreme );
    }

    if ( !y_check )
        return false;

    bool x_check = false;

    for ( int i = 0 ; i < 8 ; i++ )
    {
        float x_extreme = ( dots[i][2] * tan_vert_angle ) * ratio;
        x_check |= ( dots[i][0] < x_extreme );
    }

    if ( !x_check )
        return false;

    for ( int i = 0 ; i < 8 ; i++ )
    {
        float x_extreme = ( dots[i][2] * tan_vert_angle ) * ratio;
        x_check |= ( dots[i][0] > -x_extreme );
    }

    if ( !x_check )
        return false;

    return true;
}
