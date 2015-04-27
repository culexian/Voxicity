#ifndef VECTOR3F_H
#define VECTOR3F_H

#include <string>

class Vector3f
{
    public:
    float x;
    float y;
    float z;

    Vector3f();
    Vector3f( float x, float y, float z );

    static Vector3f& add( const Vector3f& a, const Vector3f& b, Vector3f& dest );
    static Vector3f& sub( const Vector3f& a, const Vector3f& b, Vector3f& dest );
    static float angle( const Vector3f& a, const Vector3f& b );
    static Vector3f& cross( const Vector3f& a, const Vector3f& b, Vector3f& dest );
    static float dot( const Vector3f& a, const Vector3f& b );

    void set( float _x, float _y, float _z );

    float lengthSquared() const;
    float length() const;

    Vector3f& negate();
    Vector3f& negate( Vector3f& dest ) const;
    Vector3f& normalise();
    Vector3f& normalise( Vector3f& dest ) const;
    Vector3f& scale( float scale );
    Vector3f& translate( float x, float y, float z );

    std::string to_string() const;
};

#endif // VECTOR3F_H
