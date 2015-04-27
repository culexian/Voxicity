#ifndef INPUTHANDLER_H
#define INPUTHANDLER_H

#include "inputstate.h"
#include "player.h"
#include "world.h"

#include <list>
#include <SDL2/SDL.h>

class InputHandler
{
    Player* player;
    World* world;
    SDL_Window* window;

    long last_block_change;
    long last_frame = 0;

    float yaw;
    float pitch;

    float mouse_speed = 2.0f;
    float camera_offset = 0.75f;

    void update_movement( float delta, const InputState& in_state );
    void toggle_mouse_grab();
    void set_flying( bool fly );
    bool can_change_block();
    void place_block();
    void remove_block();
    Vector3f calc_place_loc();
    int get_time_delta();

    // Handle the player collisions by moving the player out of solid volumes
    // and disabling jumping if needed
    void handle_collisions( float delta );

    // Correct X-axis velocity to the nearest box that collides
    // with the YZ-corridor in the direction of travel
    float correct_x_velocity( const std::list<AABB>& boxes, const AABB& box, float dist );

    // Correct Y-axis velocity to the nearest box that collides
    // with the XZ-corridor in the direction of travel
    float correct_y_velocity( const std::list<AABB>& boxes, const AABB& box, float y_delta );

    // Correct Z-axis velocity to the nearest box that collides
    // with the XY-corridor in the direction of travel
    float correct_z_velocity( const std::list<AABB>& boxes, const AABB& box, float z_delta );

    public:
    InputHandler( SDL_Window* window, Player* player, World* world );

    Vector3f place_loc{ 0, 0, 0 };

    void init();
    void update();
    void update_camera();
};

#endif // INPUTHANDLER_H
