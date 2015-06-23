#include "listener.h"

#include "networkconnection.h"

Listener::Listener( Config* config )
{
    socket = Socket( 11000 );
    thread = std::thread( [&]{ run(); } );
}

void Listener::run()
{
    while ( !quitting )
    {
        Socket* s = socket.accept();
        std::printf( "Got new connection\n" );
        connections.put( new NetworkConnection( s ) );
        std::printf( "Put new connection in queue\n" );
    }
}

void Listener::quit()
{
    quitting = true;
    socket.close();
}

Connection* Listener::get_new_connection()
{
    return connections.poll();
}
