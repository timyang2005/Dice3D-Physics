precision mediump float;
uniform float uBrightness;
void main() {
    vec3 baseColor = vec3(0.55, 0.55, 0.55);
    gl_FragColor = vec4(baseColor * uBrightness, 1.0);
}
