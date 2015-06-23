#include "connectioninfo.h"

#include <random>

ConnectionInfo::ConnectionInfo()
{
    update();
}

int ConnectionInfo::get_update_id() const
{
    return update_id;
}

long ConnectionInfo::get_last_update() const
{
    return last_update;
}

long ConnectionInfo::get_next_update() const
{
    return last_update + 10000;
}

void ConnectionInfo::update()
{
    last_update = Util::get_time_ms();
    std::minstd_rand0 gen(last_update );
    update_id = gen();
    awaiting_update = false;
}
