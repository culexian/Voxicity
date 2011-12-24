
uniform sampler2D textures;

varying vec2 tex;

void main()
{
	gl_FragColor = vec4( 0.5, 0.0, 0.0, 1.0 ) + texture2D( textures, tex );
}
