
uniform sampler2D textures;

varying vec2 tex;

void main()
{
	gl_FragColor = texture2D( textures, tex );
}
