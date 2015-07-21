#ifndef BLOCKINGQUEUE_H
#define BLOCKINGQUEUE_H

#include <condition_variable>
#include <mutex>
#include <queue>

template<typename T>
class BlockingQueue
{
    std::mutex mtx;
    std::condition_variable cv;
    std::queue<T> queue;

    public:
    void put( T t )
    {
        std::lock_guard<std::mutex> lock( mtx );
        queue.push( t );
        cv.notify_one();
    }

    T take()
    {
        std::unique_lock<std::mutex> lock( mtx );
        cv.wait( lock, [&]{ return !empty(); } );
        T t = queue.front();
        queue.pop();
        return t;
    }

    T poll()
    {
        std::lock_guard<std::mutex> lock( mtx );
        if ( queue.empty() )
            return nullptr;
        else
        {   
            T t = queue.front();
            queue.pop();
            return t;
        }
    }

    bool empty()
    {
        std::lock_guard<std::mutex> lock( mtx );
        return queue.empty();
    }
};

#endif // BLOCKINGQUEUE_H
