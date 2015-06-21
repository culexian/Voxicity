#include "socket.h"

Socket::Socket( const std::string& host, int port )
{
    IPaddress ip;

    SDLNet_ResolveHost( &ip, host.c_str(), port );

    s = SDLNet_TCP_Open(&ip);
}

void Socket::close()
{
    closed = true;
    SDLNet_TCP_Close(s);
}

bool Socket::isClosed() const
{
    return closed;
}

int Socket::readInt()
{
    return SDLNet_Read32( read(4).data() );
}

std::vector<uint8_t> Socket::read( int length )
{
    std::vector<uint8_t> data( length );
    int ret = SDLNet_TCP_Recv( s, data.data(), length );

    if ( ret <= 0 )
    {
        std::printf( "SDLNet_TCP_Recv: %s\n", SDLNet_GetError() );
        close();
    }

    return data;
}

void Socket::writeInt( int val )
{
    std::vector<uint8_t> data(4);
    SDLNet_Write32( val, data.data() );
    write( data );
}

void Socket::write( const std::vector<uint8_t>& data )
{
    int ret = SDLNet_TCP_Send( s, data.data(), data.size() );

    if ( ret < data.size() )
    {
        std::printf( "SDLNet_TCP_Send: %s\n", SDLNet_GetError() );
        close();
    }
}
