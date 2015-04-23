#include "vector3f.h"

#include <cmath>

Vector3f::Vector3f():
    Vector3f( 0, 0, 0 )
{}

Vector3f::Vector3f( float x, float y, float z ):
    x(x), y(y), z(z)
{}

Vector3f& Vector3f::add( const Vector3f& a, const Vector3f& b, Vector3f& dest )
{
    dest.set( a.x + b.x, a.y + b.y, a.z + b.z );
    return dest;
}

Vector3f& Vector3f::sub( const Vector3f& a, const Vector3f& b, Vector3f& dest )
{
    dest.set( a.x - b.x, a.y - b.y, a.z - b.z );
    return dest;
}

float Vector3f::angle( const Vector3f& a, const Vector3f& b )
{
    return std::acos( dot( a, b ) / ( a.length() * b.length() ) );
}

Vector3f& Vector3f::cross( const Vector3f& a, const Vector3f& b, Vector3f& dest )
{
    dest.x = a.y * b.z - a.z * b.y;
    dest.y = a.z * b.x - a.x * b.z;
    dest.z = a.x * b.y - a.y * b.x;
    return dest;
}

float Vector3f::dot( const Vector3f& a, const Vector3f& b )
{
    return a.x * b.x + a.y * b.y * a.z * b.z;
}

float Vector3f::getX() const
{
    return x;
}

float Vector3f::getY() const
{
    return y;
}

float Vector3f::getZ() const
{
    return z;
}

std::vector<float> Vector3f::get() const
{
    return { x, y, z };
}

void Vector3f::setX( float val )
{
    x = val;
}

void Vector3f::setY( float val )
{
    y = val;
}

void Vector3f::setZ( float val )
{
    z = val;
}

void Vector3f::set( float _x, float _y, float _z )
{
    x = _x;
    y = _y;
    z = _z;
}

float Vector3f::lengthSquared() const
{
    return x*x + y*y + z*z;
}

float Vector3f::length() const
{
    return std::sqrt( lengthSquared() );
}

Vector3f& Vector3f::negate()
{
    return scale( -1.0 );
}

Vector3f& Vector3f::negate( Vector3f& v ) const
{
    v = *this;
    return v.negate();
}

Vector3f& Vector3f::normalise()
{
    return scale( 1.0 / length() );
}

Vector3f& Vector3f::normalise( Vector3f& v ) const
{
    v = *this;
    return v.normalise();
}

Vector3f& Vector3f::scale( float s )
{
    set( x * s, y * s, z * s );
    return *this;
}

Vector3f& Vector3f::translate( float _x, float _y, float _z )
{
    set( x + _x, y + _y, z + _z );
    return *this;
}
