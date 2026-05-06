precision mediump float;
varying vec2 vTextureCoord;
uniform sampler2D uTexture;
uniform float uBrightness;
void main() {
    vec4 texColor = texture2D(uTexture, vTextureCoord);
    gl_FragColor = vec4(texColor.rgb * uBrightness, texColor.a);
}
