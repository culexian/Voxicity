
varying vec2 tex;

void main()
{
	tex = gl_MultiTexCoord0.st;
	gl_Position = gl_ModelViewProjectionMatrix*gl_Vertex;
}
