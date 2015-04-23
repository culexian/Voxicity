#ifndef VECTOR3F_H
#define VECTOR3F_H

#include <vector>

class Vector3f
{
    float x;
    float y;
    float z;

    public:
    Vector3f();
    Vector3f( float x, float y, float z );

    static Vector3f& add( const Vector3f& a, const Vector3f& b, Vector3f& dest );
    static Vector3f& sub( const Vector3f& a, const Vector3f& b, Vector3f& dest );
    static float angle( const Vector3f& a, const Vector3f& b );
    static Vector3f& cross( const Vector3f& a, const Vector3f& b, Vector3f& dest );
    static float dot( const Vector3f& a, const Vector3f& b );

    float getX() const;
    float getY() const;
    float getZ() const;
    std::vector<float> get() const;

    void setX( float val );
    void setY( float val );
    void setZ( float val );
    void set( float _x, float _y, float _z );

    float lengthSquared() const;
    float length() const;

    Vector3f& negate();
    Vector3f& negate( Vector3f& dest ) const;
    Vector3f& normalise();
    Vector3f& normalise( Vector3f& dest ) const;
    Vector3f& scale( float scale );
    Vector3f& translate( float x, float y, float z );
};

#endif // VECTOR3F_H
