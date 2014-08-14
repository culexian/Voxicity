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

#include <SDL2/SDL.h>

Client::Client( Config config )
{
	init_SDL( config );
}

void Client::init_SDL( Config config )
{
	if ( SDL_Init( SDL_INIT_EVERYTHING ) != 0 )
	{
		std::cout << "SDL_Init Error: " << SDL_GetError() << std::endl;
		return;
	}

	SDL_GL_SetAttribute( SDL_GL_CONTEXT_MAJOR_VERSION, 2 );
	SDL_GL_SetAttribute( SDL_GL_CONTEXT_MINOR_VERSION, 1 );

	SDL_Window* window = SDL_CreateWindow( "Voxicity", 0, 0, 1024, 768, SDL_WINDOW_OPENGL | SDL_WINDOW_RESIZABLE );
	if ( window == nullptr )
	{
		std::cout << "SDL_CreateWindow Error: " << SDL_GetError() << std::endl;
		SDL_Quit();
		return;
	}

	SDL_GLContext context = SDL_GL_CreateContext( window );
	if ( context == nullptr )
	{
		std::cout << "SDL_GL_CreateContext Error: " << SDL_GetError() << std::endl;
	}

	SDL_Delay( 5000 );
}
