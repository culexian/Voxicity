#ifndef CONSTANTS_H
#define CONSTANTS_H

#include <string>
#include <vector>

namespace Constants
{
    namespace Chunk
    {
        const int side_length = 16;
    }

    namespace Blocks
    {
        enum
        {
            air = 0,
            dirt,
            stone,
            grass
        };
    }
}

enum Direction
{
    East = 0,
    West,
    North,
    South,
    Up,
    Down,
    All,
    None
};

const std::vector<std::string> directions{ "East", "West", "North", "South", "Up", "Down", "All", "None" };

#endif // CONSTANTS_H
