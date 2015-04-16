#include "inputstate.h"

InputState::InputState()
{
    SDL_Event event;

    while( SDL_PollEvent( &event ) )
    {
        switch ( event.type )
        {
            case SDL_QUIT:
                quit = true;
                break;
            case SDL_KEYDOWN:
                handle_key_event( event );
                break;
        }
    }

    const Uint8* state = SDL_GetKeyboardState(NULL);

    move_left = state[SDL_SCANCODE_A];
    move_right = state[SDL_SCANCODE_D];
    move_forward = state[SDL_SCANCODE_W];
    move_backward = state[SDL_SCANCODE_S];
    ascend = state[SDL_SCANCODE_SPACE];
    descend = state[SDL_SCANCODE_C];

    Uint32 button_state = SDL_GetRelativeMouseState( &x_delta, &y_delta );
    hit_action = SDL_BUTTON( SDL_BUTTON_LEFT );
    use_action = SDL_BUTTON( SDL_BUTTON_RIGHT );

}

void InputState::handle_key_event( SDL_Event& event )
{
    switch( event.key.keysym.sym )
    {
        case SDLK_e:
            toggle_mouse = true;
            break;
        case SDLK_g:
            toggle_flying = true;
            break;
        case SDLK_ESCAPE:
        case SDLK_q:
            quit = true;
    }
}
