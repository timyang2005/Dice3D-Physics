precision mediump float;
varying vec4 vAmbient;
varying vec4 vDiffuse;
varying vec4 vSpecular;
varying vec2 vTextureCoord;
varying vec3 vWorldPos;
uniform vec3 uDiceColor;
uniform sampler2D uTexture;

void main() {
    vec4 texColor = texture(uTexture, vTextureCoord);
    float hasTexture = step(0.5, texColor.a);
    vec3 diceColor = mix(uDiceColor, texColor.rgb, hasTexture);
    vec4 baseColor = vec4(diceColor, 1.0);
    vec4 finalColor = baseColor * vAmbient + baseColor * vDiffuse + baseColor * vSpecular;
    gl_FragColor = finalColor;
}
