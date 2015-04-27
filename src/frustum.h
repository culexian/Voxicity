#ifndef FRUSTUM_H
#define FRUSTUM_H

#include "aabb.h"
#include "vector3f.h"

class Frustum
{
    Vector3f pos{ 0, 0, 0 };
    Vector3f right{ 1, 0, 0 };
    Vector3f up{ 0, 1, 0 };
    Vector3f look{ 0, 0, 1 };

    float ratio = 0;
    float vert_angle_rads = 0;
    float near_dist = 0;
    float far_dist = 0;

    float tan_vert_angle = 0;
    float near_width = 0;
    float near_height = 0;
    float far_width = 0;
    float far_height = 0;

    public:
    void set_attribs( float vert_angle, float ratio, float near_dist, float far_dist );
    void set_pos( const Vector3f& pos, const Vector3f& look, const Vector3f& up, const Vector3f& forward );
    bool contains_point( const Vector3f& point ) const;

    // Check if an AABB collides with this frustum by checking all the points
    // in turn against each set of planes
    bool collides( const AABB& box ) const;
};

#endif // FRUSTUM_H
