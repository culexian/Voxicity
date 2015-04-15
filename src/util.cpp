#include "util.h"

#include <chrono>

namespace Util
{
    long get_time_ms()
    {
        using namespace std::chrono;
        return duration_cast<milliseconds>(system_clock::now().time_since_epoch()).count();
    }
    
    long get_time_ns()
    {
        using namespace std::chrono;
        return duration_cast<nanoseconds>(system_clock::now().time_since_epoch()).count();
    }
}
