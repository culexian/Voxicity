/*
 * Copyright 2014, Erik Lund
 *
 * This file is part of Voxicity.
 *
 *  Voxicity is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Voxicity is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Voxicity.  If not, see <http://www.gnu.org/licenses/>.
 */

#ifndef CLIENT_H
#define CLIENT_H

#include <SDL2/SDL.h>

#include "config.h"
#include "fpscounter.h"

class Client
{
    Config* config;
    FPSCounter fps_counter;
    SDL_Window* window;
    SDL_GLContext context;

    bool quitting = false;

    void init();
    void init_SDL();
    void init_GL();
    void init_login();
    void update();

    public:
    Client( Config* config );
    void run();
};

#endif // CLIENT_H
