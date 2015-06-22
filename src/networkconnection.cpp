#include "networkconnection.h"

// Connection is constructed with a socket that's already been prepared
NetworkConnection::NetworkConnection( Socket* s )
{
    this->s = s;

    // Start the threads that send/receive packets in/out of the socket
    start_receive_thread();
    start_send_thread();
}

void NetworkConnection::start_receive_thread()
{
    receiver = std::thread( [&]
    {
        while ( true )
        {
            int id = s->readInt();
            int length = s->readInt();
            auto data = s->read( length );
            incoming.put( PacketFactory::create( id, data ) );
        }
    } );
}

void NetworkConnection::start_send_thread()
{
    sender = std::thread( [&]
    {
        while ( true )
        {
            Packet* p = outgoing.take();
            auto data = p->serialize();

            s->writeInt( p->get_id() );
            s->writeInt( data.size() );
            s->write( data );
        }
    });
}

void NetworkConnection::close()
{
    s->close();
}

bool NetworkConnection::is_closed() const
{
    return s->isClosed();
}

void NetworkConnection::wait_send()
{
    Connection::wait_send();

    /*// Flush the socket
    try {
        out.flush();
    }
    catch ( IOException e )
    {
        System.out.println( e );
        e.printStackTrace();
    }*/
}
