#ifndef CONNECTION_H
#define CONNECTION_H

#include "blockingqueue.h"
#include "packet.h"

class Connection
{
    protected:
    BlockingQueue< Packet* > incoming;
    BlockingQueue< Packet* > outgoing;

    public:
    void send( Packet* p );
    Packet* receive();

    bool is_closed() const;
    void close();

    // Wait until all packets have been sent
    void wait_send();
};

#endif // CONNECTION_H
