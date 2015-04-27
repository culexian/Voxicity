#include "inputhandler.h"

#include "util.h"
#include "worldutil.h"

#include <cmath>
#include <iostream>
#include <limits>

InputHandler::InputHandler( SDL_Window* window, Player* player, World* world ):
    window(window), player(player), world(world)
{
    get_time_delta();
}

void InputHandler::init()
{
    SDL_SetWindowGrab( window, SDL_TRUE );
}

void InputHandler::update()
{
    // Get the number of seconds as a float since the last update
    float delta = get_time_delta() / 1000.0f;

    // Get the input state
    InputState in_state;

    // TODO Event queue for Client
/*    if ( in_state.quit )
        client.quit();
*/
    if ( in_state.toggle_mouse )
        toggle_mouse_grab();

    if ( in_state.toggle_flying )
        set_flying( !player->flying );

    update_movement( delta, in_state );
    handle_collisions( delta );

    if ( in_state.use_action )
        place_block();

    if ( in_state.hit_action )
        remove_block();
}

void InputHandler::update_movement( float delta, const InputState& in_state )
{
    // Set friction factor
    float friction = 0.99f;

    //Store the last_position
    player->last_pos = player->pos;

    if ( SDL_GetWindowGrab( window ) )
    {
        int x_delta = in_state.x_delta;
        int y_delta = in_state.y_delta;

        Vector3f move;

        if ( in_state.move_left )
        {
            if ( player->flying )
                move.x -= 80;
            else
            {
                if ( player->jumping )
                    move.x -= 35;
                else
                    move.x -= 60;
            }
        }

        if ( in_state.move_right )
        {
            if ( player->flying )
                move.x += 80;
            else
            {
                if ( player->jumping )
                    move.x += 35;
                else
                    move.x += 60;
            }
        }

        if ( in_state.move_forward )
        {
            if ( player->flying )
                move.z -= 80;
            else
            {
                if ( player->jumping )
                    move.z -= 35;
                else
                    move.z -= 60;
            }
        }

        if ( in_state.move_backward )
        {
            if ( player->flying )
                move.z += 80;
            else
            {
                if ( player->jumping )
                    move.z += 35;
                else
                    move.z += 60;
            }
        }

        if ( player->flying )
        {
            if ( in_state.ascend )
                move.y += 80;

            if ( in_state.descend )
                move.y -= 80;
        }
        else
        {
            if ( in_state.ascend && !player->jumping )
            {
                player->velocity.y = 10.0f;
                player->jumping = true;
            }

            if ( player->jumping )
                move.y = -30;
        }

        yaw += ( x_delta / 800.0f ) * 45.0f * mouse_speed;
        pitch += ( y_delta / 800.0f ) * 45.0f * mouse_speed;

        // Make sure spinning idiots don't get ludicrously high rotations
        yaw = yaw > 361.0f ? yaw - 360.0f : yaw;
        yaw = yaw < -361.0 ? yaw + 360.0f : yaw;

        // Avoid NaN in the frustum calculations
        pitch = std::min( pitch, 90.0f );
        pitch = std::max( pitch, -90.0f );

        float cos_yaw = ( float ) std::cos( Util::deg_to_rad( yaw ) );
        float sin_yaw = ( float ) std::sin( Util::deg_to_rad( yaw ) );
        float cos_pitch = ( float ) std::cos( Util::deg_to_rad( pitch ) );
        float sin_pitch = ( float ) std::sin( Util::deg_to_rad( pitch ) );

        float corr_x = ( move.x * cos_yaw ) + ( move.z * -sin_yaw );
        float corr_z = ( move.x * sin_yaw ) + ( move.z * cos_yaw );

        player->accel.x = corr_x;
        player->accel.y = move.y;
        player->accel.z = corr_z;

        player->velocity.x += player->accel.x * delta; 
        player->velocity.y += player->accel.y * delta;
        player->velocity.z += player->accel.z * delta;

        if ( player->velocity.x != 0.0f || player->velocity.y != 0.0f || player->velocity.z != 0.0f )
        {
            Vector3f friction_vec = player->velocity;
            friction_vec = friction_vec.normalise( friction_vec );
            friction_vec.negate();
            friction_vec.scale( 30 * friction * delta );

            if ( player->velocity.lengthSquared() < friction_vec.lengthSquared() )
            {
                player->velocity.x = 0;
                player->velocity.z = 0;

                if ( player->flying )
                    player->velocity.y = 0;
            }
            else
            {
                if ( !std::isnan( friction_vec.lengthSquared() ) )
                {
                    player->velocity.x += friction_vec.x;
                    player->velocity.z += friction_vec.z;

                    if ( player->flying )
                        player->velocity.y += friction_vec.y;
                }
            }
        }

        if ( player->velocity.lengthSquared() < 0.001 )
        {
            player->velocity.x = 0;
            player->velocity.z = 0;

            if ( player->flying )
                player->velocity.y = 0;
        }

        if ( player->flying )
        {
            if ( player->velocity.lengthSquared() > 100.0f )
            {
                float ratio = 10.0f / player->velocity.length();
                player->velocity.scale( ratio );
            }
        }
        else
        {
            Vector3f horiz_vel( player->velocity.x, 0, player->velocity.z );
            if ( horiz_vel.lengthSquared() > 25.0f )
            {
                float ratio = 5.0f / horiz_vel.length();
                horiz_vel.scale( ratio );
                player->velocity.x = horiz_vel.x;
                player->velocity.z = horiz_vel.z;
            }
        }

        // Set the look vector
        player->look.set( sin_yaw * cos_pitch * 4, sin_pitch * 4, cos_yaw * cos_pitch * -4 );
        player->forward.set( sin_yaw, 0, -cos_yaw );
    }
}

void InputHandler::toggle_mouse_grab()
{
    SDL_SetWindowGrab( window, SDL_GetWindowGrab( window ) == SDL_TRUE ? SDL_FALSE : SDL_TRUE );
}

void InputHandler::set_flying( bool fly )
{
    if ( fly )
    {
        player->flying = true;
        player->jumping = false;
        player->accel.y = 0;
        player->velocity.y = 0;
        std::cout << "Flying is " << player->flying << " Jumping is " << player->jumping << std::endl;
    }
    else
    {
        player->flying = false;
        player->jumping = true;
        std::cout << "Flying is " << player->flying << " Jumping is " << player->jumping << std::endl;
    }
}

bool InputHandler::can_change_block()
{
    return Util::get_time_ms() - last_block_change > ( 1000 / 5 );
}

void InputHandler::place_block()
{
    if ( can_change_block() )
        last_block_change = Util::get_time_ms();
    else
        return;

    int id = world->get_block( place_loc.x, place_loc.y, place_loc.z );
    if ( id == Constants::Blocks::air )
    {
        // TODO Client event queue
        //client.tell_use_action( new BlockLoc( place_loc.x, place_loc.y, place_loc.z, world ), Direction.None );
    }
    else
    {
        Direction collision_side = world->get_hit_box( std::lrint(place_loc.x), std::lrint(place_loc.y), std::lrint(place_loc.z) ).collision_side( Vector3f( player->pos.x, player->pos.y + camera_offset, player->pos.z ), player->look );

        // TODO Fix Client event queue
        //client.tell_use_action( new BlockLoc( place_loc.x, place_loc.y, place_loc.z, world ), collision_side );
    }
}

void InputHandler::remove_block()
{
    if ( can_change_block() )
        last_block_change = Util::get_time_ms();
    else
        return;

    // TODO Fix Client event queue
    //client.tell_hit_action( std::lrint( place_loc.x ), std::lrint( place_loc.y ), std::lrint( place_loc.z ) );
}

void InputHandler::update_camera()
{
    Vector3f pos = player->pos;
    pos.y = pos.y + camera_offset;

    Vector3f up( 0, 1, 0 );

    // TODO Fix Client event queue
    //client.renderer.camera.set_pos( pos, player->look, up, player->forward );
}

Vector3f InputHandler::calc_place_loc()
{
    float nearest_distance = std::numeric_limits<float>::infinity();
    Vector3f nearest_block( std::lrint( player->pos.x + player->look.x ), std::lrint( player->pos.y + camera_offset + player->look.y ), std::lrint( player->pos.z + player->look.z ));

    int x_incr = static_cast<int>(Util::signum( player->look.x ));
    int y_incr = static_cast<int>(Util::signum( player->look.y ));
    int z_incr = static_cast<int>(Util::signum( player->look.z ));

    for ( int x = std::lrint( player->pos.x ) ; x != std::lrint( player->pos.x + player->look.x ) + x_incr ; x += x_incr)
        for ( int y = std::lrint( player->pos.y + camera_offset ) ; y != std::lrint( player->pos.y + camera_offset + player->look.y ) + y_incr ; y += y_incr )
            for ( int z = std::lrint( player->pos.z ) ; z != std::lrint( player->pos.z + player->look.z ) + z_incr ; z += z_incr )
            {
                AABB box = world->get_hit_box( x, y, z );

                float distance = box.collision_distance( Vector3f( player->pos.x, player->pos.y + camera_offset, player->pos.z ), player->look );
                    
                if ( distance < nearest_distance )
                {
                    nearest_distance = distance;
                    nearest_block.x = x;
                    nearest_block.y = y;
                    nearest_block.z = z;
                }
            }

    return nearest_block;
}

int InputHandler::get_time_delta()
{
    // Get the time in milliseconds
    long new_time = Util::get_time_ms();
    int delta = (int) ( new_time - last_frame );
    last_frame = new_time;
    return delta;
}

// Handle the player collisions by moving the player out of solid volumes
// and disabling jumping if needed
void InputHandler::handle_collisions( float delta )
{
    // Epsilon used for small differences
    float epsilon = 0.0001f;

    // Copy the player velocity and scale it to the movement in this update
    Vector3f velocity = player->velocity;
    velocity.scale( delta );

    // Create two bounding boxes, one at the start and one at the end of the path
    AABB p( 0.5f, 1.75f, 0.5f );
    p.center_on( player->last_pos );
    AABB q = p;
    q.translate( velocity );

    // Find all the volumes possibly intersected by this path by combining one large bounding box
    const std::list<AABB> boxes = WorldUtil::get_intersecting_volumes( *world, AABB( p, q ) );

    // Translate the bounding volume as corrections are found in y, x, and z axes
    float y = correct_y_velocity( boxes, p, velocity.y );
    p.translate( 0, y, 0 );
    float x = correct_x_velocity( boxes, p, velocity.x );
    p.translate( x, 0, 0 );
    float z = correct_z_velocity( boxes, p, velocity.z );
    p.translate( 0, 0, z );

//		Vector3f.add( player->last_pos, new Vector3f( x, y, z ), player->pos );a
    player->pos = p.position();
    //p.center_on( player->pos );

    // If collisions happen in any direction, set that velocity to 0
    if ( x != velocity.x )
        player->velocity.x = 0;
    if ( y != velocity.y )
        player->velocity.y = 0;
    if ( z != velocity.z )
        player->velocity.z = 0;

    // Create a new box to test for solid volumes under the player
    AABB standing_box( p );
    // Translate the standing box slightly down to check for surfaces to
    // stand on
    standing_box.translate( 0, -epsilon, 0 );

    // Check if the player is standing on anything after having set y movement to 0
    // Disable jumping if so

    player->jumping = true;

    for ( const AABB& box : WorldUtil::get_intersecting_volumes( *world, standing_box ) )
        if ( standing_box.intersects( box ) && y == 0.0 )
            player->jumping = false;
}

// Correct X-axis velocity to the nearest box that collides
// with the YZ-corridor in the direction of travel
float InputHandler::correct_x_velocity( const std::list<AABB>& boxes, const AABB& box, float dist )
{
    for ( AABB hit : boxes )
    {
        if ( !box.intersects_yz( hit ) )
            continue;

        if ( dist < 0 && box.min_x() >= hit.max_x() )
        {
            float delta = hit.max_x() - box.min_x();
            dist = std::max( dist, delta );
        }
        else if ( dist > 0 && box.max_x() <= hit.min_x() )
        {
            float delta = hit.min_x() - box.max_x();
            dist = std::min( dist, delta );
        }
    }

    return dist;
}

// Correct Y-axis velocity to the nearest box that collides
// with the XZ-corridor in the direction of travel
float InputHandler::correct_y_velocity( const std::list<AABB>& boxes, const AABB& box, float y_delta )
{
    for ( AABB hit : boxes )
    {
        if ( !box.intersects_xz( hit ) )
            continue;

        // Check if we're descending on to the box
        if ( y_delta < 0 && ( box.min_y() >= hit.max_y() ) )
        {
            float delta = hit.max_y() - box.min_y();
            y_delta = std::max( y_delta, delta );
        }
        else if ( y_delta > 0 && box.max_y() <= hit.min_y() )
        {
            float delta = hit.min_y() - box.max_y();
            y_delta = std::min( y_delta, delta );
        }
    }

    return y_delta;
}

// Correct Z-axis velocity to the nearest box that collides
// with the XY-corridor in the direction of travel
float InputHandler::correct_z_velocity( const std::list<AABB>& boxes, const AABB& box, float z_delta )
{
    for ( AABB hit : boxes )
    {
        if ( !box.intersects_xy( hit ) )
            continue;

        if ( z_delta < 0 && box.min_z() >= hit.max_z() )
        {
            float delta = hit.max_z() - box.min_z();
            z_delta = std::max( z_delta, delta );
        }
        else if ( z_delta > 0 && box.max_z() <= hit.min_z() )
        {
            float delta = hit.min_z() - box.max_z();
            z_delta = std::min( z_delta, delta );
        }
    }

    return z_delta;
}
