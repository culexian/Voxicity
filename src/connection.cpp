#include "connection.h"

void Connection::send( Packet* p )
{
    if ( !is_closed() )
        outgoing.put( p );
}

Packet* Connection::receive()
{
    if ( !is_closed() )
        return incoming.poll();
    else
        return nullptr;
}

bool Connection::is_closed() const
{
    return false;
}

// Wait until all packets have been sent
void Connection::wait_send()
{
    while ( !outgoing.empty() )
        ;
}
