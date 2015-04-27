#include "worldutil.h"

#include "vector3f.h"

#include <cmath>

namespace WorldUtil
{
    // Get the full list of intersecting volumes for this AABB in this World
    std::list<AABB> get_intersecting_volumes( const World& world, const AABB& box )
    {
        std::list<AABB> list;
        Vector3f min = box.min_corner();
        Vector3f max = box.max_corner();

        // Check from the floored minima up to and including the
        // up-rounded maxima of the bounding volume
        for ( int i = static_cast<int>(std::floor(min.x)) ; i <= std::ceil( max.x ) ; i++ )
            for ( int j = static_cast<int>(std::floor(min.y)) ; j <= std::ceil( max.y ) ; j++ )
                for ( int k = static_cast<int>(std::floor(min.z)) ; k <= std::ceil( max.z ) ; k++ )
                    list.push_back( world.get_hit_box( i, j, k ) );

        return list;
    }
}
