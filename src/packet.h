#ifndef PACKET_H
#define PACKET_H

#include <cstdint>
#include <vector>

class Packet
{
    public:
    virtual int get_id() = 0; 
    virtual std::vector<uint8_t> serialize() const = 0;
};

#endif // PACKET_H
