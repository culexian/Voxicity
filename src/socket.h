#ifndef SOCKET_H
#define SOCKET_H

#include <string>

#include <SDL2/SDL_net.h>

#include <cstdint>
#include <vector>

class Socket
{
    TCPsocket s;
    bool closed = false;

    public:
    Socket( const std::string& host, int port );

    void close();
    bool isClosed() const;

    int readInt();
    std::vector<uint8_t> read( int length );

    void writeInt( int data );
    void write( const std::vector<uint8_t>& data );
};

#endif // SOCKET_H
