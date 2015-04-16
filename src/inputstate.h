#ifndef INPUTSTATE_H
#define INPUTSTATE_H

#include <SDL2/SDL.h>

class InputState
{
    void handle_key_event( SDL_Event& event );

    public:
    bool
        move_left = false,
        move_right = false,
        move_forward = false,
        move_backward = false,
        ascend = false,
        descend = false,
        toggle_mouse = false,
        toggle_flying = false,
        hit_action = false,
        use_action = false,
        quit = false;

    int
        x_delta = 0,
        y_delta = 0;

    InputState();
};

#endif // INPUTSTATE_H
