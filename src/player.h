#ifndef PLAYER_H
#define PLAYER_H

#include "vector3f.h"

#include <string>

class Player
{
    public:
    std::string name = "Player";
    Vector3f last_pos;
    Vector3f pos{ 0, 3, 0 };
    Vector3f accel;
    Vector3f velocity;
    Vector3f look{ 0, 0, 1 };
    Vector3f forward{ 0, 0, 1 };

    bool flying = true;
    bool jumping = true;
};

#endif // PLAYER_H
