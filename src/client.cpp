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

#include "inputstate.h"

#ifdef TARGET_OS_MAC
    #include <OpenGL/gl.h>
#else
    #include <GL/gl.h>
#endif

#include <iostream>

#include <SDL2/SDL_net.h>

/* Wrapper for: glGetIntegerv */
GLint glGetInteger( GLenum pname )
{
    GLint data;
    glGetIntegerv( pname, &data );
    return data;
}

Client::Client( Config* config ): config(config), world(config)
{}

void Client::init_SDL()
{
    if ( SDL_Init( SDL_INIT_EVERYTHING ) != 0 )
    {
        std::cout << "SDL_Init Error: " << SDL_GetError() << std::endl;
        return;
    }

    if ( SDLNet_Init() == -1 )
    {
        std::cout << "SDLNet error: " << SDLNet_GetError() << std::endl;
        return;
    }

    SDL_GL_SetAttribute( SDL_GL_CONTEXT_MAJOR_VERSION, 2 );
    SDL_GL_SetAttribute( SDL_GL_CONTEXT_MINOR_VERSION, 1 );

    int width = config->get_int( "window_width", 1280 );
    int height = config->get_int( "window_height", 720 );

    window = SDL_CreateWindow( "Voxicity", 0, 0, width, height, SDL_WINDOW_OPENGL | SDL_WINDOW_RESIZABLE );

    if ( window == nullptr )
    {
        std::cout << "SDL_CreateWindow Error: " << SDL_GetError() << std::endl;
        SDL_Quit();
        return;
    }

    input_handler = new InputHandler( window, &player, &world );

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

    std::cout << "Setting up OpenGL states\n";
    glShadeModel(GL_SMOOTH);
    glEnable( GL_DEPTH_TEST );
    glEnable( GL_TEXTURE_2D );
    glEnable( GL_CULL_FACE );
    glEnable( GL_BLEND );
    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    glClearColor( 126.0f / 255.0f, 169.0f / 255.0f, 254.0f / 255.0f, 1.0f );
    glEnableClientState( GL_VERTEX_ARRAY );
    glEnableClientState( GL_TEXTURE_COORD_ARRAY );

    std::printf( "Number of texture units: %d\n", glGetInteger( GL_MAX_TEXTURE_UNITS ) );
    std::printf( "Number of image texture units: %d\n", glGetInteger( GL_MAX_TEXTURE_IMAGE_UNITS ) );
    std::printf( "Number of vertex texture units: %d\n", glGetInteger( GL_MAX_VERTEX_TEXTURE_IMAGE_UNITS ) );
    std::printf( "Number of combined vertex/image texture units: %d\n", glGetInteger( GL_MAX_COMBINED_TEXTURE_IMAGE_UNITS ) );
}

void Client::init_login()
{
/*
    LWJGLRenderer gui_renderer = new LWJGLRenderer();
    LoginGUI login_gui = new LoginGUI();
    ThemeManager theme = ThemeManager.createThemeManager( Voxicity.class.getResource( "/login.xml" ), gui_renderer );
    GUI gui = new GUI( login_gui, gui_renderer );
    gui.applyTheme( theme );

    while ( !login_gui.is_login_pressed() )
    {
        GL11.glClear( GL11.GL_COLOR_BUFFER_BIT );
        gui.update();
        Display.update();

        if ( Display.isCloseRequested() )
        {
            Display.destroy();
            System.exit( 0 );
        }
    }

    Socket client_s = new Socket( login_gui.get_server_name(), 11000 );
    Client client = new Client( config, new NetworkConnection( client_s ) );
*/
}

void Client::init()
{
    init_SDL();
    init_GL();
    init_login();
}

void Client::run()
{
    init();

    while( !quitting )
    {
        InputState input;
        quitting = input.quit;

        update();

        glClear( GL_COLOR_BUFFER_BIT );
        SDL_GL_SwapWindow( window );
    }
}

void Client::update()
{

}
