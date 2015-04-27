#ifndef WORLDUTIL_H
#define WORLDUTIL_H

#include "aabb.h"
#include "world.h"

#include <list>

namespace WorldUtil
{
    // Get the full list of intersecting volumes for this AABB in this World
    std::list<AABB> get_intersecting_volumes( const World& world, const AABB& box );
}

#endif // WORLDUTIL_H
