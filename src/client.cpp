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

#include "client.h"

#include <GL/gl.h>

Client::Client( Config* config )
{
    config = config;

}

void Client::init_SDL()
{
    if ( SDL_Init( SDL_INIT_EVERYTHING ) != 0 )
    {
        std::cout << "SDL_Init Error: " << SDL_GetError() << std::endl;
        return;
    }

    SDL_GL_SetAttribute( SDL_GL_CONTEXT_MAJOR_VERSION, 2 );
    SDL_GL_SetAttribute( SDL_GL_CONTEXT_MINOR_VERSION, 1 );


    window = SDL_CreateWindow( "Voxicity", 0, 0, 1280, 720, SDL_WINDOW_OPENGL | SDL_WINDOW_RESIZABLE );

    if ( window == nullptr )
    {
        std::cout << "SDL_CreateWindow Error: " << SDL_GetError() << std::endl;
        SDL_Quit();
        return;
    }

    std::printf( "Created Voxicity main window\n" );
}

void Client::init_GL()
{
    SDL_GL_SetAttribute( SDL_GL_DOUBLEBUFFER, 1 );
    SDL_GL_SetAttribute( SDL_GL_DEPTH_SIZE, 24 );

    context = SDL_GL_CreateContext( window );

    if ( context == nullptr )
    {
        std::printf( "SDL_GL_CreateContext Error: %s\n", SDL_GetError() );
        SDL_Quit();
    }

    glClearColor( 126.0f / 255.0f, 169.0f / 255.0f, 254.0f / 255.0f, 1.0f );
    glClear( GL_COLOR_BUFFER_BIT );
}

void Client::init()
{
    init_SDL();
    init_GL();
    
}

void Client::run()
{
    init();

//    while( !quitting )
        update();

    SDL_GL_SwapWindow( window );

    SDL_Delay( 500 );
}

void Client::update()
{

}
