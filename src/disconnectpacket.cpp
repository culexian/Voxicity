#include "disconnectpacket.h"

#include "constants.h"

int DisconnectPacket::get_id()
{
    return Constants::Packet::Disconnect;
}

std::vector<uint8_t> DisconnectPacket::serialize() const
{
    return {};
}
