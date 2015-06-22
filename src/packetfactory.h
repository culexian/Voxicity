#ifndef PACKETFACTORY_H
#define PACKETFACTORY_H

#include "packet.h"

#include <cstdint>
#include <vector>

namespace PacketFactory
{
    Packet* create( const int id, const std::vector<uint8_t>& data );
};

#endif // PACKETFACTORY_H
